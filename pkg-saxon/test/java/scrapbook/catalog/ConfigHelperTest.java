package scrapbook.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.Reader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
//import javax.xml.xquery.XQConnection;
//import javax.xml.xquery.XQDataSource;
//import javax.xml.xquery.XQException;
//import javax.xml.xquery.XQPreparedExpression;
//import javax.xml.xquery.XQResultSequence;
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
//import net.sf.saxon.xqj.SaxonXQDataSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.saxon.ConfigHelper;
import org.expath.pkg.saxon.SaxonRepository;
import org.junit.Test;

/**
 *
 * @author georgfl
 */
public class ConfigHelperTest
{
    public ConfigHelperTest()
            throws PackageException
    {
        Storage storage = new FileSystemStorage(new File("test/java/transform/repo"));
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
        Processor proc = new Processor(false);
        // configure the processor for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());
        // compiling
        XsltCompiler compiler = proc.newXsltCompiler();
        Source style = new StreamSource(getResource(XSLT_NAME));
        XsltExecutable exec = compiler.compile(style);
        // actually evaluate
        XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        Serializer out = new Serializer();
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
        Processor proc = new Processor(false);
        // configure the processor for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());
        // compiling
        XQueryCompiler compiler = proc.newXQueryCompiler();
        XQueryExecutable exec = compiler.compile(getResource(XQUERY_NAME));
        // actually evaluate
        XQueryEvaluator eval = exec.load();
        Serializer out = new Serializer();
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
        TransformerFactoryImpl factory = new TransformerFactoryImpl();
        // configure the factory for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(factory.getConfiguration());
        // compiling
        Source style = new StreamSource(getResource(XSLT_NAME));
        Templates templates = factory.newTemplates(style);
        // actually transform
        Transformer trans = templates.newTransformer();
        Source src = new StreamSource(getResource(XSLT_NAME));
        Result res = new StreamResult(OUT);
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
        Configuration config = new Configuration();
        // configure the config object for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // compiling
        StaticQueryContext ctxt = new StaticQueryContext(config);
        XQueryExpression exp = ctxt.compileQuery(getResource(XQUERY_NAME), "utf-8");
        // actually evaluate
        DynamicQueryContext dyn = new DynamicQueryContext(config);
        Result res = new StreamResult(OUT);
        exp.run(dyn, res, null);
    }

//    //Using XQJ.
//    @Test
//    public void xquery_xqj()
//            throws TransformerException
//                 , XPathException
//                 , IOException
//                 , XQException
//                 , PackageException
//    {
//        // the config object
//        Configuration config = new Configuration();
//        // configure the config object for Packaging System
//        ConfigHelper helper = new ConfigHelper(REPO);
//        helper.config(config);
//        // compiling
//        XQDataSource ds = new SaxonXQDataSource(config);
//        XQConnection conn = ds.getConnection();
//        Reader query = new InputStreamReader(getResource(XQUERY_NAME), "utf-8");
//        XQPreparedExpression expr = conn.prepareExpression(query);
//        // actually evaluate
//        XQResultSequence result = expr.executeQuery();
//        while ( result.next() ) {
//            // System.out.println(result.getItemAsString(null));
//        }
//        System.out.flush();
//    }

    private InputStream getResource(String resource)
    {
        ClassLoader loader = getClass().getClassLoader();
        return loader.getResourceAsStream(resource);
    }

    private SaxonRepository REPO;
//    private static final String XSLT_NAME = "scrapbook/catalog/http-test.xsl";
//    private static final String XQUERY_NAME = "scrapbook/catalog/http-test.xq";
    private static final String XSLT_NAME = "transform/style.xsl";
    private static final String XQUERY_NAME = "transform/query.xq";
    // private static final OutputStream OUT  = System.err;
    private static final OutputStream OUT  = new DevNull();

    private static class DevNull
            extends OutputStream
    {
        @Override
        public void close() throws IOException {
            // nothing
        }
        @Override
        public void flush() throws IOException {
            // nothing
        }
        @Override
        public void write(byte[] b) throws IOException {
            // nothing
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // nothing
        }
        @Override
        public void write(int b) throws IOException {
            // nothing
        }
    }
}
