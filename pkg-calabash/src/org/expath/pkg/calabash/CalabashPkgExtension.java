/****************************************************************************/
/*  File:       CalabashPkgExtension.java                                   */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-09-06                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.QName;
import org.expath.pkg.repo.DescriptorExtension;
import org.expath.pkg.repo.FileSystemStorage.FileSystemResolver;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.PackageInfo;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.parser.XMLStreamHelper;

/**
 * TODO: ...
 *
 * @author Florent Georges
 * @date   2011-09-06
 */
public class CalabashPkgExtension
        extends DescriptorExtension
{
    public CalabashPkgExtension()
    {
        super("calabash", "calabash.xml");
    }

    @Override
    protected void parseDescriptor(XMLStreamReader parser, Package pkg)
            throws PackageException
    {
        myXSHelper.ensureNextElement(parser, "package");
        CalabashPkgInfo info = new CalabashPkgInfo(pkg);
        try {
            parser.next();
            while ( parser.getEventType() == XMLStreamConstants.START_ELEMENT ) {
                if ( CALABASH_PKG_NS.equals(parser.getNamespaceURI()) ) {
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
        CalabashPkgInfo info = getInfo(pkg);
        if ( info == null ) {
            return;
        }
        if ( ! info.hasJars() ) {
            return;
        }
        setupPackage(pkg, info);
    }

    private CalabashPkgInfo getInfo(Package pkg)
            throws PackageException
    {
        PackageInfo info = pkg.getInfo(getName());
        if ( info == null ) {
            return null;
        }
        if ( ! (info instanceof CalabashPkgInfo) ) {
            throw new PackageException("Not a Calabash-specific package info: " + info.getClass());
        }
        return (CalabashPkgInfo) info;
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

    private void handleElement(XMLStreamReader parser, Package pkg, CalabashPkgInfo info)
            throws PackageException
                 , XMLStreamException
    {
        String local = parser.getLocalName();
        if ( "jar".equals(local) ) {
            String jar = myXSHelper.getElementValue(parser);
            info.addJar(jar);
        }
        else if ( "step".equals(local) ) {
            myXSHelper.ensureNextElement(parser, "type");
            String type = myXSHelper.getElementValue(parser);
            myXSHelper.ensureNextElement(parser, "class");
            String clazz = myXSHelper.getElementValue(parser);
            // position to </step>
            parser.next();
            // push the values in info
            QName qname = QName.fromClarkName(type);
            info.addStep(qname, clazz);
        }
        else {
            throw new PackageException("Unknown Calabash component type: " + local);
        }
    }

    private void setupPackage(Package pkg, CalabashPkgInfo info)
            throws PackageException
    {
        FileSystemResolver res = getFileSystemResolver(pkg);
        File classpath = res.resolveResourceAsFile(".calabash/classpath.txt");
        if ( classpath.exists() ) {
            throw new PackageException("classpath.txt already exists: " + classpath);
        }

        // create [pkg_dir]/.calabash/classpath.txt
        File calabash = classpath.getParentFile();
        if ( ! calabash.exists() && ! calabash.mkdir() ) {
            throw new PackageException("Impossible to create directory: " + calabash);
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
            throw new PackageException("The Calabash descriptor refers to an inexistent JAR", ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the Calabash classpath file: " + classpath, ex);
        }
    }

    public static final String CALABASH_PKG_NS = "http://xmlcalabash.com/ns/expath-pkg";
    private final XMLStreamHelper myXSHelper = new XMLStreamHelper(CALABASH_PKG_NS);
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
