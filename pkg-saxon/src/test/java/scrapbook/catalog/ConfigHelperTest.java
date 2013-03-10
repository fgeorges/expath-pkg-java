/****************************************************************************/
/*  File:       ConfigHelperTest.java                                       */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-07-28                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009, 2010 Florent Georges (see end of file.)         */
/* ------------------------------------------------------------------------ */
package scrapbook.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.saxon.ConfigHelper;
import org.expath.pkg.saxon.SaxonRepository;
import org.junit.Test;
import transform.DevNullOutputStream;
import static transform.TestConstants.REPO_LOCATION;

/**
 *
 * @author georgfl
 */
public class ConfigHelperTest
{
    //private static final String XSLT_NAME = "scrapbook/catalog/http-test.xsl";
    //private static final String XQUERY_NAME = "scrapbook/catalog/http-test.xq";
    private static final String XSLT_NAME = "transform/style.xsl";
    private static final String XQUERY_NAME = "transform/query.xq";
    // private static final OutputStream OUT  = System.err;
    private static final OutputStream OUT  = new DevNullOutputStream();
    
    private final SaxonRepository REPO;
    
    public ConfigHelperTest()
            throws PackageException
    {
        final Storage storage = new FileSystemStorage(new File(REPO_LOCATION));
        REPO = new SaxonRepository(storage);
    }

    //Using S9api Processor.
    @Test
    public void xslt_s9api()
            throws TransformerException
                 , SaxonApiException
                 , PackageException
    {
        // the processor
        final Processor proc = new Processor(false);
        // configure the processor for Packaging System
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());
        // compiling
        final XsltCompiler compiler = proc.newXsltCompiler();
        final Source style = new StreamSource(getResource(XSLT_NAME));
        final XsltExecutable exec = compiler.compile(style);
        // actually evaluate
        final XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        final Serializer out = new Serializer();
        out.setOutputStream(OUT);
        trans.setDestination(out);
        trans.transform();
    }

    //Using S9api Processor.
    @Test
    public void xquery_s9api()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , PackageException
    {
        // the processor
        final Processor proc = new Processor(false);
        // configure the processor for Packaging System
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());
        // compiling
        final XQueryCompiler compiler = proc.newXQueryCompiler();
        final XQueryExecutable exec = compiler.compile(getResource(XQUERY_NAME));
        // actually evaluate
        final XQueryEvaluator eval = exec.load();
        final Serializer out = new Serializer();
        out.setOutputStream(OUT);
        eval.run(out);
    }

    //Using JAXP factory.
    @Test
    public void xslt_jaxp()
            throws TransformerException
                 , PackageException
    {
        // the factory
        final TransformerFactoryImpl factory = new TransformerFactoryImpl();
        // configure the factory for Packaging System
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(factory.getConfiguration());
        // compiling
        final Source style = new StreamSource(getResource(XSLT_NAME));
        final Templates templates = factory.newTemplates(style);
        // actually transform
        final Transformer trans = templates.newTransformer();
        final Source src = new StreamSource(getResource(XSLT_NAME));
        final Result res = new StreamResult(OUT);
        trans.transform(src, res);
    }

    //Using Saxon Configuration.
    @Test
    public void xquery_legacy()
            throws TransformerException
                 , XPathException
                 , IOException
                 , PackageException
    {
        // the config object
        final Configuration config = new Configuration();
        // configure the config object for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // compiling
        final StaticQueryContext ctxt = new StaticQueryContext(config);
        final XQueryExpression exp = ctxt.compileQuery(getResource(XQUERY_NAME), "utf-8");
        // actually evaluate
        final DynamicQueryContext dyn = new DynamicQueryContext(config);
        final Result res = new StreamResult(OUT);
        exp.run(dyn, res, null);
    }

    //Using XQJ.
    @Test
    public void xquery_xqj()
            throws TransformerException
                 , XPathException
                 , IOException
                 , XQException
                 , PackageException
    {
        // the config object
        final Configuration config = new Configuration();
        // configure the config object for Packaging System
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // compiling
        final XQDataSource ds = new SaxonXQDataSource(config);
        final XQConnection conn = ds.getConnection();
        final Reader query = new InputStreamReader(getResource(XQUERY_NAME), "utf-8");
        final XQPreparedExpression expr = conn.prepareExpression(query);
        // actually evaluate
        final XQResultSequence result = expr.executeQuery();
        while ( result.next() ) {
            // System.out.println(result.getItemAsString(null));
        }
        System.out.flush();
    }

    private InputStream getResource(final String resource)
    {
        final ClassLoader loader = getClass().getClassLoader();
        return loader.getResourceAsStream(resource);
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