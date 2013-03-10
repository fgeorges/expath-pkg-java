/****************************************************************************/
/*  File:       TestConfigProcessor.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:                                                                   */
/*  Tags:                                                                   */
/*      Copyright (c) 2009, 2010 Florent Georges (see end of file.)         */
/* ------------------------------------------------------------------------ */


package transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import static transform.TestConstants.REPO_LOCATION;
import static transform.TestConstants.TRANSFORM_LOCATION;

/**
 * ...
 *
 * @author Florent Georges
 * @date
 */
public class TestConfigProcessor
{
    private static final String STYLE_STD  = TRANSFORM_LOCATION + "/style.xsl";
    private static final String STYLE_JAVA = TRANSFORM_LOCATION + "/using-java.xsl";
    private static final String QUERY_STD  = TRANSFORM_LOCATION + "/query.xq";
    private static final String QUERY_JAVA = TRANSFORM_LOCATION + "/using-java.xq";
    // private static final OutputStream OUT  = System.err;
    private static final OutputStream OUT  = new DevNullOutputStream();
    
    private final SaxonRepository REPO;
    
    public TestConfigProcessor()
            throws PackageException
    {
        final Storage storage = new FileSystemStorage(new File(REPO_LOCATION));
        REPO = new SaxonRepository(storage);
    }

    @Test(
        // error while compiling the stylesheet...
        expected=SaxonApiException.class
    )
    public void configProcessor_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configProcessor_fail");
        final Processor proc = new Processor(false);

        final XsltCompiler compiler = proc.newXsltCompiler();
        final Source style = new StreamSource(STYLE_STD);
        final XsltExecutable exec = compiler.compile(style);

        final XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        final Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        trans.setDestination(serial);
        trans.transform();
        OUT.flush();
    }

    @Test
    public void configProcessor_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configProcessor_successful");
        final Processor proc = new Processor(false);
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());

        final XsltCompiler compiler = proc.newXsltCompiler();
        final Source style = new StreamSource(STYLE_STD);
        final XsltExecutable exec = compiler.compile(style);

        final XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        final Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        trans.setDestination(serial);
        trans.transform();
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void configFactory_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configFactory_fail");
        final TransformerFactoryImpl factory = new TransformerFactoryImpl();
        final Source style = new StreamSource(STYLE_STD);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void configFactory_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configFactory_successful");
        final TransformerFactoryImpl factory = new TransformerFactoryImpl();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(factory.getConfiguration());

        final Source style = new StreamSource(STYLE_STD);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void configConfiguration_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configConfiguration_fail");
        final Configuration config = new Configuration();
        final TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        final Source style = new StreamSource(STYLE_STD);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void configConfiguration_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configConfiguration_successful");
        final Configuration config = new Configuration();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        final TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        final Source style = new StreamSource(STYLE_STD);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void usingJava_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("usingJava_fail");
        final Configuration config = new Configuration();
        final TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        final Source style = new StreamSource(STYLE_JAVA);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void usingJava_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("usingJava_successful");
        final Configuration config = new Configuration();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        final TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        final Source style = new StreamSource(STYLE_JAVA);
        final Transformer trans = factory.newTransformer(style);
        final Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=SaxonApiException.class
    )
    public void queryProcessor_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryProcessor_fail");
        final Processor proc = new Processor(false);
        final XQueryCompiler compiler = proc.newXQueryCompiler();
        final XQueryExecutable exec = compiler.compile(new File(QUERY_STD));

        final XQueryEvaluator eval = exec.load();
        final Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        eval.evaluate();
        OUT.flush();
    }

    @Test
    public void queryProcessor_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryProcessor_successful");
        final Processor proc = new Processor(false);
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());

        final XQueryCompiler compiler = proc.newXQueryCompiler();
        final XQueryExecutable exec = compiler.compile(new File(QUERY_STD));

        final XQueryEvaluator eval = exec.load();
        final Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        eval.evaluate();
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=XPathException.class
    )
    public void queryConfiguration_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryConfiguration_fail");
        Configuration config = new Configuration();
        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        final StaticQueryContext static_ctxt = new StaticQueryContext(config);
        final Reader query = new FileReader(new File(QUERY_STD));
        final XQueryExpression expr = static_ctxt.compileQuery(query);
        final DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test
    public void queryConfiguration_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryConfiguration_successful");
        final Configuration config = new Configuration();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        final StaticQueryContext static_ctxt = new StaticQueryContext(config);
        final Reader query = new FileReader(new File(QUERY_STD));
        final XQueryExpression expr = static_ctxt.compileQuery(query);
        final DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=XPathException.class
    )
    public void queryUsingJava_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryUsingJava_fail");
        Configuration config = new Configuration();
        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        final StaticQueryContext static_ctxt = new StaticQueryContext(config);
        final Reader query = new FileReader(new File(QUERY_JAVA));
        final XQueryExpression expr = static_ctxt.compileQuery(query);
        final DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test
    public void queryUsingJava_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryUsingJava_successful");
        final Configuration config = new Configuration();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        final StaticQueryContext static_ctxt = new StaticQueryContext(config);
        final Reader query = new FileReader(new File(QUERY_JAVA));
        final XQueryExpression expr = static_ctxt.compileQuery(query);
        final DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=XQException.class
    )
    public void queryXQJ_fail()
            throws XQException
                 , FileNotFoundException
    {
        System.err.println("queryXQJ_fail");
        final XQDataSource ds = new SaxonXQDataSource();
        final XQConnection conn = ds.getConnection();
        final Reader query = new FileReader(new File(QUERY_STD));
        final XQPreparedExpression expr = conn.prepareExpression(query);
        final XQResultSequence result = expr.executeQuery();
        while ( result.next() ) {
            // System.out.println(result.getItemAsString(null));
        }
        System.out.flush();
    }

    @Test
    public void queryXQJ_successful()
            throws XQException
                 , FileNotFoundException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryXQJ_successful");
        final Configuration config = new Configuration();
        final ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        final XQDataSource ds = new SaxonXQDataSource(config);
        final XQConnection conn = ds.getConnection();
        final Reader query = new FileReader(new File(QUERY_STD));
        final XQPreparedExpression expr = conn.prepareExpression(query);
        final XQResultSequence result = expr.executeQuery();
        while ( result.next() ) {
            // System.out.println(result.getItemAsString(null));
        }
        System.out.flush();
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
