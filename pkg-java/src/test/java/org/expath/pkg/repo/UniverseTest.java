/****************************************************************************/
/*  File:       UniverseTest.java                                           */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-01-29                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.IOException;
import java.net.URI;
import java.io.File;
import java.net.URISyntaxException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.expath.pkg.repo.TestConstants.REPOS_LOCATION;

/**
 * Test the {@code Universe}, especially its resolve mechanism.
 *
 * @author Florent Georges
 * @date   2012-01-29
 */
public class UniverseTest
{
    private File pkgComponentFile(final String repo, final String lib, final String version, final String component)
    {
        final File f = new File(REPOS_LOCATION,  repo + "/" + lib + "-" + version + "/" + lib + "/" + component);
        System.err.println(f.getPath());
        return f;
    }

    private File sourceFile(final Source src)
            throws URISyntaxException
    {
        assertNotNull(src);
        final String sysid = src.getSystemId();
        final URI uri = new URI(sysid);
        return new File(uri);
    }

    private void assertComponent(final String repo, final String lib, final String version, final String component, final Source src)
            throws URISyntaxException, IOException
    {
        final File expected = pkgComponentFile(repo, lib, version, component);
        final File actual   = sourceFile(src);
        final String exp_path = expected.getCanonicalPath();
        final String act_path = actual.getCanonicalPath();
        assertEquals(exp_path, act_path);
    }

    private Repository getRepository(final String repo)
            throws PackageException
    {
        final File repo_dir = new File(REPOS_LOCATION, repo);
        final Storage storage = new FileSystemStorage(repo_dir);
        return new Repository(storage);
    }

    private Package latestPackage(final Repository repo, final String name)
    {
        Packages pp = repo.getPackages(name);
        return pp.latest();
    }

    private static class TestSource
            extends StreamSource
    {
        public TestSource(final String str) {
            myStr = str;
        }
        @Override
        public String toString() {
            return myStr;
        }
        private String myStr;
    }

    private static class TestResolver
            extends Storage.PackageResolver
    {
        public TestResolver(final String pkg) {
            myPkg = pkg;
        }
        @Override
        public String getResourceName() {
            throw new UnsupportedOperationException("Not supported in tests.");
        }
        @Override
        public StreamSource resolveResource(final String path) throws PackageException {
            throw new UnsupportedOperationException("Not supported in tests.");
        }
        @Override
        public StreamSource resolveComponent(final String path) throws PackageException {
            return new TestSource(myPkg + " | component | " + path);
        }
        private String myPkg;
    }

    private static final String APP_PKG_NAME   = "http://example.com/my-app";
    private static final String LIB_Y_PKG_NAME = "http://example.org/lib-y";

    /**
     * Use "by-hand-constructed" repository and packages.
     *
     * Other tests parse real repositories directly from the file system.
     */
    @Test
    public void firstSimpleTest()
            throws PackageException
    {
        // the fixture (the repository and packages)
        final Repository repo = new Repository();
        final Package p1 = new Package(repo, new TestResolver("pkg 1"), "urn:test:1", "p1", "1.0", "title 1", "http://home/1");
        p1.addPublicUri(URISpace.XSLT, "urn:test:1:some.xsl", "dir/some.xsl");
        p1.addPublicUri(URISpace.XSLT, "urn:test:1:other.xsl", "dir/other.xsl");
        repo.addPackage(p1);
        final Package p2 = new Package(repo, new TestResolver("pkg 2"), "urn:test:2", "p2", "1.0", "title 2", "http://home/2");
        p2.addPublicUri(URISpace.XSLT, "urn:test:2:some.xsl", "dir/some.xsl");
        p2.addPublicUri(URISpace.XSLT, "urn:test:2:other.xsl", "dir/other.xsl");
        repo.addPackage(p2);
        // the SUT
        final CompositeUniverse sut = new CompositeUniverse(true);
        sut.addUniverse(p1);
        sut.addUniverse(p2);
        // do it
        final Source src = sut.resolve("urn:test:1:some.xsl", URISpace.XSLT);
        // assert
        assertEquals("the resolved component", "pkg 1 | component | dir/some.xsl", src.toString());
    }

    /**
     * Use one package
     */
    @Test
    public void onePackageDefaultDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-x/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-x", "12.9.0", "query.xql", src);
    }

    @Test
    public void onePackageDefaultDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Packages sut = repo.getPackages(LIB_Y_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-y/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "query.xql", src);
    }

    @Test
    public void onePackageTransitiveDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, true);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "style.xsl", src);
    }

    @Test
    public void onePackageTransitiveDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Packages sut = repo.getPackages(APP_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, true);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "style.xsl", src);
    }

    @Test
    public void onePackageStrictDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, false);
        // assertions
        assertNull(src);
    }

    @Test
    public void onePackageStrictDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        final Repository repo = getRepository("deps-1");
        // the sut
        final Packages sut = repo.getPackages(APP_PKG_NAME);
        // do it
        final Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, false);
        // assertions
        assertNull(src);
    }

    @Test
    public void wholeRepoUniverse()
            throws PackageException, URISyntaxException, IOException
    {
        // the sut
        final Repository sut = getRepository("deps-1");
        // do it
        final Source src = sut.resolve("http://example.org/lib-b/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-b", "0.1.0", "query.xql", src);
    }
}


/* ------------------------------------------------------------------------ */
/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
/*                                                                          */
/*  The contents of this file are subject to the Mozilla Public License     */
/*  Version 1.0 (the "License"); you may not use this file except in        */
/*  compliance with the License. You may obtain a copy of the License at    */
/*  http://www.mozilla.org/MPL/.                                            */
/*                                                                          */
/*  Software distributed under the License is distributed on an "AS IS"     */
/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
/*  the License for the specific language governing rights and limitations  */
/*  under the License.                                                      */
/*                                                                          */
/*  The Original Code is: all this file.                                    */
/*                                                                          */
/*  The Initial Developer of the Original Code is Florent Georges.          */
/*                                                                          */
/*  Contributor(s): Adam Retter                                             */
/* ------------------------------------------------------------------------ */
