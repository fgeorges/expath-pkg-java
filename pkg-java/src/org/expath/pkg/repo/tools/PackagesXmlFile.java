/****************************************************************************/
/*  File:       PackagesXmlFile.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;

/**
 * Represent the file [repo]/.expath-pkg/packages.xml.
 *
 * @author Florent Georges
 * @date   2013-09-17
 */
public class PackagesXmlFile
{
    /**
     * Create a new instance.
     * 
     * @param file The actual file, for [repo]/.expath-pkg/packages.xml.
     */
    public PackagesXmlFile(File file)
            throws PackageException
    {
        myFile = file;
        if ( ! myFile.exists() ) {
            createIt();
        }
    }

    /**
     * Add a package to packages.xml.
     * 
     * @param dir The name of the directory where the package is installed,
     * right below the repository root.
     */
    public void addPackage(Package pkg, String dir)
            throws PackageException
    {
        Transformer trans = compile(ADD_PACKAGE_XSL);
        trans.setParameter("name",    pkg.getName());
        trans.setParameter("dir",     dir);
        trans.setParameter("version", pkg.getVersion());
        transform(trans);
    }

    /**
     * Remove a package from packages.xml.
     * 
     * The package is identified by its directory name (the name of the
     * directory where it is installed, right below the repository root).
     */
    public void removePackageByDir(String dir)
            throws PackageException
    {
        Transformer trans = compile(REMOVE_PACKAGE_XSL);
        trans.setParameter("dir", dir);
        transform(trans);
    }

    /**
     * Create an empty file.
     * 
     * By empty, means with the root element, with no package element.
     */
    private void createIt()
            throws PackageException
    {
        try {
            Writer out = new FileWriter(myFile);
            out.write("<packages xmlns=\"http://expath.org/ns/repo/packages\"/>\n");
            out.close();
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("Impossible to create the packages xml list: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error creating the packages xml list: " + myFile, ex);
        }
    }

    /**
     * Compile a stylesheet.
     */
    private Transformer compile(String rsrc_name)
            throws PackageException
    {
        try {
            // cache the compiled stylesheet?
            ClassLoader loader = FileSystemStorage.class.getClassLoader();
            InputStream style_in = loader.getResourceAsStream(rsrc_name);
            if ( style_in == null ) {
                throw new PackageException("Resource not found: " + rsrc_name);
            }
            Source style_src = new StreamSource(style_in);
            style_src.setSystemId(rsrc_name);
            Templates style = TransformerFactory.newInstance().newTemplates(style_src);
            return style.newTransformer();
        }
        catch ( TransformerConfigurationException ex ) {
            throw new PackageException("Impossible to compile the stylesheet: " + rsrc_name, ex);
        }
    }

    /**
     * Transform the file with the transformer.
     * 
     * Because we want to write the result back to the same file as the input,
     * we want to be sure we won't delete it first to read it.  So we buffer
     * the output.  In order to avoid creating a temporary file, we buffer the
     * result in memory, by serializing it to a string.  This is OK for such a
     * typically small file.
     */
    private void transform(Transformer trans)
            throws PackageException
    {
        try {
            Source src = new StreamSource(myFile);
            StringWriter res_out = new StringWriter();
            Result res = new StreamResult(res_out);
            trans.transform(src, res);
            OutputStream out = new FileOutputStream(myFile);
            out.write(res_out.getBuffer().toString().getBytes());
            out.close();
        }
        catch ( TransformerException ex ) {
            throw new PackageException("Error transforming the file: " + myFile, ex);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found (wtf? - I just transformed it): " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    /** The stylesheet to add a package, as a Java resource name. */
    private static final String ADD_PACKAGE_XSL    = "org/expath/pkg/repo/rsrc/add-package.xsl";
    /** The stylesheet to remove a package, as a Java resource name. */
    private static final String REMOVE_PACKAGE_XSL = "org/expath/pkg/repo/rsrc/remove-package.xsl";

    /** The actual file object to [repo]/.expath-pkg/packages.xml. */
    private File myFile;
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
