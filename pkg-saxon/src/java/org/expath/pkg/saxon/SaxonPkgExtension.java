/****************************************************************************/
/*  File:       SaxonPkgExtension.java                                      */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-09-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.DescriptorExtension;
import org.expath.pkg.repo.FileSystemStorage.FileSystemResolver;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.PackageInfo;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.parser.XMLStreamHelper;

/**
 * Repo management extension class for Saxon.
 *
 * @author Florent Georges
 * @date   2010-09-19
 */
public class SaxonPkgExtension
        extends DescriptorExtension
{
    public SaxonPkgExtension()
    {
        super("saxon", "saxon.xml");
    }

    @Override
    protected void parseDescriptor(XMLStreamReader parser, Package pkg)
            throws PackageException
    {
        myXSHelper.ensureNextElement(parser, "package");
        SaxonPkgInfo info = new SaxonPkgInfo(pkg);
        try {
            parser.next();
            while ( parser.getEventType() == XMLStreamConstants.START_ELEMENT ) {
                if ( SAXON_PKG_NS.equals(parser.getNamespaceURI()) ) {
                    handleElement(parser, pkg, info);
                }
                else {
                    // ignore elements not in the Saxon Pkg namespace
                    // TODO: FIXME: Actually ignore (pass it.)
                    throw new PackageException("TODO: Ignore elements in other namespace");
                }
                parser.next();
            }
            // position to </package>
            parser.next();
        }
        catch ( XMLStreamException ex ) {
            throw new PackageException("Error reading the saxon descriptor", ex);
        }
        pkg.addInfo(getName(), info);
    }

    @Override
    public void install(Repository repo, Package pkg)
            throws PackageException
    {
        init(repo, pkg);
        SaxonPkgInfo info = getInfo(pkg);
        if ( info == null ) {
            return;
        }
        if ( ! info.hasJars() ) {
            return;
        }
        setupPackage(pkg, info);
    }

    private SaxonPkgInfo getInfo(Package pkg)
            throws PackageException
    {
        PackageInfo info = pkg.getInfo(getName());
        if ( info == null ) {
            return null;
        }
        if ( ! (info instanceof SaxonPkgInfo) ) {
            throw new PackageException("Not a Saxon-specific package info: " + info.getClass());
        }
        return (SaxonPkgInfo) info;
    }

    private FileSystemResolver getFileSystemResolver(Package pkg)
            throws PackageException
    {
        Storage.PackageResolver res = pkg.getResolver();
        if ( res == null ) {
            throw new PackageException("Resolver is null on package: " + pkg.getName());
        }
        if ( ! (res instanceof FileSystemResolver) ) {
            throw new PackageException("Not a file system resolver: " + res.getClass());
        }
        return (FileSystemResolver) res;
    }

    private void handleElement(XMLStreamReader parser, Package pkg, SaxonPkgInfo info)
            throws PackageException
                 , XMLStreamException
    {
        String local = parser.getLocalName();
        if ( "jar".equals(local) ) {
            String jar = myXSHelper.getElementValue(parser);
            info.addJar(jar);
        }
        else if ( "function".equals(local) ) {
            String fun = myXSHelper.getElementValue(parser);
            info.addFunction(fun);
        }
        else if ( "xslt".equals(local) ) {
            Mapping m = handleMapping(parser, "import-uri", pkg);
            info.addXslt(m.href, m.file);
        }
        // TODO: Handle main modules (with pkg:import-uri instead of pkg:namespace).
        else if ( "xquery".equals(local) ) {
            Mapping m = handleMapping(parser, "namespace", pkg);
            info.addXQuery(m.href, m.file);
        }
        else if ( "xslt-wrapper".equals(local) ) {
            Mapping m = handleMapping(parser, "import-uri", pkg);
            info.addXsltWrapper(m.href, m.file);
        }
        else if ( "xquery-wrapper".equals(local) ) {
            Mapping m = handleMapping(parser, "namespace", pkg);
            info.addXQueryWrapper(m.href, m.file);
        }
        else {
            throw new PackageException("Unknown Saxon component type: " + local);
        }
    }

    private Mapping handleMapping(XMLStreamReader parser, String uri_name, Package pkg)
            throws PackageException, XMLStreamException
    {
        myXSHelper.ensureNextElement(parser, uri_name);
        String href = myXSHelper.getElementValue(parser);
        myXSHelper.ensureNextElement(parser, "file");
        String file = myXSHelper.getElementValue(parser);
        // position to </...> (xslt, xquery, xslt-wrapper or xquery-wrapper)
        parser.next();
        return new Mapping(href, file);
    }

    private void setupPackage(Package pkg, SaxonPkgInfo info)
            throws PackageException
    {
        FileSystemResolver res = getFileSystemResolver(pkg);
        File classpath = res.resolveResourceAsFile(".saxon/classpath.txt");
        if ( classpath.exists() ) {
            throw new PackageException("classpath.txt already exists: " + classpath);
        }

        // create [pkg_dir]/.saxon/classpath.txt
        File saxon = classpath.getParentFile();
        if ( ! saxon.exists() && ! saxon.mkdir() ) {
            throw new PackageException("Impossible to create directory: " + saxon);
        }
        Set<String> jars = info.getJars();
        try {
            FileWriter out = new FileWriter(classpath);
            for ( String jar : jars ) {
                StreamSource jar_src = res.resolveComponent(jar);
                String sysid = jar_src.getSystemId();
                URI uri = URI.create(sysid);
                File file = new File(uri);
                out.write(file.getCanonicalPath());
                out.write("\n");
            }
            out.close();
        }
        catch ( Storage.NotExistException ex ) {
            throw new PackageException("The Saxon descriptor refers to an inexistent JAR", ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the Saxon classpath file: " + classpath, ex);
        }
    }

    public static final String SAXON_PKG_NS = "http://saxon.sf.net/ns/expath-pkg";
    private final XMLStreamHelper myXSHelper = new XMLStreamHelper(SAXON_PKG_NS);

    private static class Mapping
    {
        public Mapping(String h, String f) {
            href = h;
            file = f;
        }
        public String href;
        public String file;
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
/*  Contributor(s): none.                                                   */
/* ------------------------------------------------------------------------ */
