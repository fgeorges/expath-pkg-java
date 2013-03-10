/****************************************************************************/
/*  File:       DescriptorParserTest.java                                   */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-12-02                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.parser;

import java.net.URISyntaxException;
import javax.xml.transform.Source;
import java.net.URI;
import org.expath.pkg.repo.Storage.PackageResolver;
import java.io.File;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.URISpace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.expath.pkg.repo.TestConstants.REPOS_LOCATION;

/**
 * TODO: ...
 *
 * @author Florent Georges
 * @date   2010-12-02
 */
public class DescriptorParserTest
{
    @Test
    public void testParse()
            throws Exception
    {
        // the SUT
        final DescriptorParser sut = new DescriptorParser();
        final Source desc = new StreamSource(PKG_DIR + "expath-pkg.xml");
        final Storage storage = new FileSystemStorage(new File(REPOS_LOCATION, "simple"));
        // get the package
        final Package pkg = sut.parse(desc, "hello-1.1.1", storage, null);
        // the simple properties
        assertEquals("the abbrev", "hello", pkg.getAbbrev());
        assertEquals("the name", "http://www.example.org/lib/hello", pkg.getName());
        assertEquals("the version", "1.1.1", pkg.getVersion());
        // the resolver
        final PackageResolver resolver = pkg.getResolver();
        assertEquals("the resource (dir) name", "hello-1.1.1", resolver.getResourceName());
        assertSourceIsFile("the resource",                "expath-pkg.xml",  resolver.resolveResource("expath-pkg.xml"));
        assertSourceIsFile("the component as a resource", "hello/hello.xq",  resolver.resolveResource("hello/hello.xq"));
        assertSourceIsFile("the component",               "hello/hello.xsl", resolver.resolveComponent("hello.xsl"));
        // the resolve method with correct public URIs
        assertSourceIsFile("resolve XQuery OK",           "hello/hello.xq",  pkg.resolve("http://www.example.org/hello", URISpace.XQUERY));
        assertSourceIsFile("resolve XSLT OK",             "hello/hello.xsl", pkg.resolve("http://www.example.org/hello.xsl", URISpace.XSLT));
        // the resolve method with wrong public URIs
        assertNull("resolve non existing", pkg.resolve("http://www.example.org/dummy.xsl", URISpace.XSLT));
        assertNull("resolve XQuery within XSLT", pkg.resolve("http://www.example.org/hello", URISpace.XSLT));
        assertNull("resolve XSLT within XQuery", pkg.resolve("http://www.example.org/hello.xsl", URISpace.XQUERY));
    }

    static private void assertSourceIsFile(final String msg, final String relative_file, final Source src)
            throws URISyntaxException
    {
        final File f = new File(PKG_DIR, relative_file);
        final URI uri = new URI(src.getSystemId());
        assertEquals(msg, f.getAbsolutePath(), new File(uri).getAbsolutePath());
    }

    static private final String PKG_DIR = REPOS_LOCATION + "/simple/hello-1.1.1/";
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
