/****************************************************************************/
/*  File:       RunCalabash.java                                            */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package functional.run;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritableDocument;
import com.xmlcalabash.model.Serialization;
import com.xmlcalabash.runtime.XPipeline;
import com.xmlcalabash.config.XProcConfigurer;
import java.io.File;
import net.sf.saxon.s9api.SaxonApiException;
import org.expath.pkg.calabash.PkgConfigurer;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Florent Georges
 * @date   2009-10-19
 */
public class RunCalabash
{
    private static final String PIPE_DIR = "src/test/resources/functional/run/";
    private static final String REPO     = PIPE_DIR + "repo/";
    
    // DEBUG: To be able to use the debugger...
//    public static void main(String[] args)
//            throws SaxonApiException
//                 , PackageException
//    {
//        RunCalabash run = new RunCalabash();
//        run.run_xquery_data();
//    }

    @Test
    public void run_pipe() throws SaxonApiException, PackageException {
        runConfiguredPipeline("pipe.xpl");
    }

    // Must fail if not configured (runPlainPipeline() does not set the EXPath
    // Pkg Configurer object on the XProcRuntime.)
    @Test(expected=XProcException.class)
    public void run_rnc_error() throws SaxonApiException {
        runPlainPipeline("rnc-data.xpl");
    }

    @Test
    @Ignore("Packaging not supported for p:load with Calabash 0.9.34")
    public void run_load() throws SaxonApiException, PackageException {
        runConfiguredPipeline("load.xpl");
    }

    @Test
    public void run_rnc_data() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rnc-data.xpl");
    }

    @Test
    public void run_rnc_external() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rnc-external.xpl");
    }

    @Test
    public void run_rnc_include() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rnc-include.xpl");
    }

    @Test
    public void run_rnc_replace() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rnc-replace.xpl");
    }

    @Test
    public void run_rng_doc() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rng-doc.xpl");
    }

    @Test
    public void run_rng_external() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rng-external.xpl");
    }

    @Test
    public void run_rng_include() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rng-include.xpl");
    }

    @Test
    public void run_rng_replace() throws SaxonApiException, PackageException {
        runConfiguredPipeline("rng-replace.xpl");
    }

    @Test
    public void run_schematron_doc() throws SaxonApiException, PackageException {
        runConfiguredPipeline("schematron-doc.xpl");
    }

    @Test
    public void run_schematron_include() throws SaxonApiException, PackageException {
        runConfiguredPipeline("schematron-include.xpl");
    }

    @Test
    public void run_xquery_simple() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xquery-simple.xpl");
    }

    @Test
    public void run_xquery_data() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xquery-data.xpl");
    }

    @Test
    public void run_xquery_import() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xquery-import.xpl");
    }

    @Test
    public void run_xquery_java() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xquery-java.xpl");
    }

    @Test
    public void run_xsd_doc() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xsd-doc.xpl");
    }

    @Test
    public void run_xsd_import() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xsd-import.xpl");
    }

    @Test
    public void run_xsd_include() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xsd-include.xpl");
    }

    @Test
    public void run_xsd_redefine() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xsd-redefine.xpl");
    }

    @Test
    public void run_xslt_doc() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xslt-doc.xpl");
    }

    @Test
    public void run_xslt_import() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xslt-import.xpl");
    }

    @Test
    public void run_xslt_include() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xslt-include.xpl");
    }

    @Test
    public void run_xslt_java() throws SaxonApiException, PackageException {
        runConfiguredPipeline("xslt-java.xpl");
    }

    private void runPlainPipeline(final String name)
            throws SaxonApiException
    {
        final XProcRuntime runtime = makePlainRuntime();
        runPipeline(name, runtime);
    }

    private void runConfiguredPipeline(final String name)
            throws SaxonApiException
                 , PackageException
    {
        final XProcRuntime runtime = makeConfiguredRuntime();
        runPipeline(name, runtime);
    }

    private void runPipeline(String name, XProcRuntime runtime)
            throws SaxonApiException
    {
        System.err.println(" ------------ ");
        System.err.println("I AM GONNA RUN " + name);
        final XPipeline pipe = runtime.load(PIPE_DIR + name);
        pipe.run();
        copyPortToStdout(pipe, "result", runtime);
    }

    private XProcRuntime makePlainRuntime()
    {
        final XProcConfiguration conf = new XProcConfiguration();
        return new XProcRuntime(conf);
    }

    private XProcRuntime makeConfiguredRuntime()
            throws PackageException
    {
        final XProcRuntime runtime = makePlainRuntime();
        final XProcConfigurer configurer = makeConfigurer(runtime);
        runtime.setConfigurer(configurer);
        return runtime;
    }

    private XProcConfigurer makeConfigurer(final XProcRuntime runtime)
            throws PackageException
    {
        final File dir = new File(REPO);
        final Storage storage = new FileSystemStorage(dir);
        final Repository repo = new Repository(storage);
        return new PkgConfigurer(runtime, repo);
    }

    private void copyPortToStdout(final XPipeline pipe, final String port, final XProcRuntime runtime)
            throws SaxonApiException
    {
        final Serialization    serial = pipe.getSerialization(port);
        final WritableDocument wdoc   = new WritableDocument(runtime, null, serial);
        final ReadablePipe     rpipe  = pipe.readFrom(port);
        while ( rpipe.moreDocuments() ) {
            wdoc.write(rpipe.read());
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
