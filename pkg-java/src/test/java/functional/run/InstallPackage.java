/****************************************************************************/
/*  File:       InstallPackage.java                                         */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-11-02                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package functional.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.UserInteractionStrategy;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Functional tests for package install.
 *
 * @author Florent Georges
 * @date   2009-11-02
 */
public class InstallPackage
{
    public static final String HELLO_XAR_OLD = "../samples/hello-pkg/hello-1.1.xar";
    public static final String HELLO_XAR_NEW = "../samples/hello-pkg/hello-1.2.xar";
    public static final String TMP_REPO_DIR  = "target/tmp/repo";

    
    @BeforeClass
    public static void setupRepo()
            throws Throwable
    {
        // Initialize a new temporary repo.
        final File repo_dir = new File(TMP_REPO_DIR);
        if ( repo_dir.exists() ) {
            fail("The directory exists: " + TMP_REPO_DIR);
        }
        if ( ! repo_dir.mkdirs() ) {
            fail("Error creating the directory: " + TMP_REPO_DIR);
        }
    }

    @AfterClass
    public static void removeRepo() {
        final File repo_dir = new File(TMP_REPO_DIR);
        recursiveDelete(repo_dir);
    }
    
    private static void recursiveDelete(final File file) {
        if ( file.isDirectory() ) {
            for ( final File child : file.listFiles() ) {
                recursiveDelete(child);
            }
        }
        if ( ! file.delete() ) {
            fail("Error deleting the file/dir: " + file);
        }
    }
    
    
    
    
    @Test
    public void testInstall()
            throws Exception
    {
        testOne(HELLO_XAR_OLD, PACKAGES_TXT_CONTENT_OLD, PACKAGES_XML_CONTENT_OLD, "hello-1.1/hello");
        testOne(HELLO_XAR_NEW, PACKAGES_TXT_CONTENT_NEW, PACKAGES_XML_CONTENT_NEW, "hello-1.2/content");
    }

    private void testOne(final String xar, final String txt_content, final String xml_content, final String content_dir)
            throws Exception
    {
        // the SUT
        final File       repo_dir = new File(TMP_REPO_DIR);
        final Storage    storage  = new FileSystemStorage(repo_dir);
        final Repository repo     = new Repository(storage);
        final File       pkg      = new File(xar);
        // do it
        repo.installPackage(pkg, true, new FakeInteract());
        // .expath-pkg/packages.txt
        final File packages_txt = new File(repo_dir, ".expath-pkg/packages.txt");
        assertTrue("the file .expath-pkg/packages.txt exist", packages_txt.exists());
        assertEquals("the file .expath-pkg/packages.txt content", txt_content, readFile(packages_txt));
        // .expath-pkg/packages.xml
        final File packages_xml = new File(repo_dir, ".expath-pkg/packages.xml");
        assertTrue("the file .expath-pkg/packages.xml exist", packages_xml.exists());
        assertEquals("the file .expath-pkg/packages.xml content", xml_content, readFile(packages_xml));
        // content dir
        final File c_dir = new File(repo_dir, content_dir);
        assertTrue("the content dir exist", c_dir.exists());
        // TODO: Write more assertions...
    }

    private String readFile(final File f)
            throws IOException
    {
        final BufferedReader reader = new BufferedReader(new FileReader(f));
        final StringBuilder buffer = new StringBuilder();
        String s;
        while ( (s = reader.readLine()) != null ) {
            buffer.append(s);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    private static final String PACKAGES_TXT_CONTENT_OLD =
            "hello-1.1 http://www.example.org/lib/hello 1.1\n";
    private static final String PACKAGES_XML_CONTENT_OLD =
            "<packages xmlns=\"http://expath.org/ns/repo/packages\">\n"
            + "   <package name=\"http://www.example.org/lib/hello\"\n"
            + "            dir=\"hello-1.1\"\n"
            + "            version=\"1.1\"/>\n"
            + "</packages>\n";
    private static final String PACKAGES_TXT_CONTENT_NEW =
            "hello-1.1 http://www.example.org/lib/hello 1.1\n"
            + "hello-1.2 http://www.example.org/lib/hello 1.2\n";
    private static final String PACKAGES_XML_CONTENT_NEW =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<packages xmlns=\"http://expath.org/ns/repo/packages\">\n"
            + "<package name=\"http://www.example.org/lib/hello\" dir=\"hello-1.1\" version=\"1.1\"/>\n"
            + "<package name=\"http://www.example.org/lib/hello\" dir=\"hello-1.2\" version=\"1.2\"/>\n"
            + "</packages>\n";

    private static class FakeInteract
            implements UserInteractionStrategy
    {
        @Override
        public void messageInfo(final String msg) throws PackageException {
            System.out.println("INFO: " + msg);
        }
        @Override
        public void messageError(final String msg) throws PackageException {
            System.out.println("ERROR: " + msg);
        }
        @Override
        public void logInfo(final String msg) throws PackageException {
            System.out.println("LOG: " + msg);
        }
        @Override
        public boolean ask(final String prompt, final boolean dflt) throws PackageException {
            System.out.println("ASK: " + prompt + " / " + dflt);
            return dflt;
        }
        @Override
        public String ask(final String prompt, final String dflt) throws PackageException {
            System.out.println("ASK: " + prompt + " / " + dflt);
            return dflt;
        }
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
