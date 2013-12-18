/****************************************************************************/
/*  File:       FileSystemStorage.java                                      */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-10-07                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.util.Logger;
import org.expath.pkg.repo.util.PackageTxt;

/**
 * Storage using the file system.
 *
 * @author Florent Georges
 */
public class FileSystemStorage
        extends Storage
{
    public FileSystemStorage(File root)
            throws PackageException
    {
        // the repository root directory
        if ( root == null ) {
            throw new NullPointerException("The repository root directory is null");
        }
        if ( ! root.exists() ) {
            String msg = "The repository root directory does not exist: " + root;
            throw new PackageException(msg);
        }
        if ( ! root.isDirectory() ) {
            String msg = "The repository root directory is not a directory: " + root;
            throw new PackageException(msg);
        }
        myRoot = root;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public PackageResolver makePackageResolver(String rsrc_name, String abbrev)
                 throws PackageException
    {
        File pkg_root = rsrc_name == null ? null : new File(myRoot, rsrc_name);
        return new FileSystemResolver(pkg_root, abbrev, rsrc_name);
    }

    @Override
    public Set<String> listPackageDirectories()
            throws PackageException
    {
        File f = new File(myRoot, ".expath-pkg/packages.txt");
        InputStream pkg_txt = null;
        if ( f.exists() ) {
            try {
                pkg_txt = new FileInputStream(f);
            }
            catch ( FileNotFoundException ex ) {
                throw new PackageException("File exists but is not found: " + f, ex);
            }
        }
        return PackageTxt.parseDirectories(pkg_txt);
    }

    @Override
    public String toString()
    {
        return "File system storage in " + myRoot.getAbsolutePath();
    }

    /**
     * If true (the default), an error is thrown if there is no content dir in the package.
     */
    public void setErrorIfNoContentDir(boolean value)
    {
        myErrorIfNoContentDir = value;
    }

    /** The root dir of the repo. */
    private File myRoot;
    /** The private area.  Must be used only through getPrivateFile(). */
    private File myPrivate;
    /** Throw an error if none content dir exist? */
    private boolean myErrorIfNoContentDir = true;

    public class FileSystemResolver
            extends PackageResolver
    {
        public FileSystemResolver(File pkg_dir, String abbrev, String rsrc_name)
                 throws PackageException
        {
            myPkgAbbrev = abbrev;
            myRsrcName = rsrc_name;
            setPkgDir(pkg_dir);
        }

        @Override
        public String getResourceName()
        {
            return myRsrcName;
        }

        private void setPkgDir(File dir)
                 throws PackageException
        {
            myPkgDir = dir;
            if ( dir == null || myPkgAbbrev == null ) {
                myContentDir = null;
            }
            else {
                myContentDir = getContenDir(dir, myPkgAbbrev);
            }
        }

        private File getContenDir(File pkg_dir, String abbrev)
                 throws PackageException
        {
            File old_style = new File(pkg_dir, abbrev);
            File new_style = new File(pkg_dir, "content");
            boolean old_exists = old_style.exists();
            boolean new_exists = new_style.exists();
            boolean old_isdir = old_style.isDirectory();
            boolean new_isdir = new_style.isDirectory();
            LOG.finer("Content dir ''{0}'' (exists:{1}/isdir:{2}), and ''{3}'' (exists:{4}/isdir:{5})",
                    new_style, new_exists, new_isdir, old_style, old_exists, old_isdir);
            if ( ! old_exists && ! new_exists ) {
                String msg = "None of content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                if ( myErrorIfNoContentDir ) {
                    throw new PackageException(msg);
                }
                return null;
            }
            else if ( old_exists && new_exists ) {
                String msg = "Both content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                throw new PackageException(msg);
            }
            else if ( new_exists ) {
                if ( ! new_isdir ) {
                    String msg = "Content dir is not a directory: '" + new_style + "'";
                    LOG.info(msg);
                    throw new PackageException(msg);
                }
                return new_style;
            }
            else {
                if ( ! old_isdir ) {
                    String msg = "Content dir is not a directory: '" + old_style + "'";
                    LOG.info(msg);
                    throw new PackageException(msg);
                }
                LOG.info("Warning: package uses old-style content dir: ''{0}''", old_style);
                return old_style;
            }
        }

        public File resolveResourceAsFile(String path)
        {
            return new File(myPkgDir, path);
        }

        public File resolveComponentAsFile(String path)
        {
            if ( myContentDir == null ) {
                return null;
            }
            return new File(myContentDir, path);
        }

        @Override
        public StreamSource resolveResource(String path)
                throws PackageException
                     , NotExistException
        {
            return resolveWithin(path, myPkgDir);
        }

        @Override
        public StreamSource resolveComponent(String path)
                throws PackageException
                     , NotExistException
        {
            if ( myContentDir == null ) {
                return null;
            }
            return resolveWithin(path, myContentDir);
        }

        private StreamSource resolveWithin(String path, File dir)
                throws PackageException
                     , NotExistException
        {
            LOG.fine("Trying to resolve ''{0}'' within ''{1}''", path, dir);
            File f = new File(dir, path);
            if ( ! f.exists() ) {
                String msg = "File '" + f + "' does not exist";
                LOG.fine(msg);
                throw new NotExistException(msg);
            }
            try {
                InputStream in = new FileInputStream(f);
                StreamSource src = new StreamSource(in);
                src.setSystemId(f.toURI().toString());
                return src;
            }
            catch( FileNotFoundException ex ) {
                String msg = "File '" + f + "' exists but is not found";
                LOG.severe(msg);
                throw new PackageException(msg, ex);
            }
        }

        private String getDirName()
        {
            return myPkgDir.getName();
        }

        private File   myPkgDir;
        private String myRsrcName;
        private File   myContentDir;
        private String myPkgAbbrev;
    }

    @Override
    public void beforeInstall(boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        // nothing
    }

    @Override
    public File makeTempDir(String prefix)
            throws PackageException
    {
        File priv_dir = ensurePrivateDir();
        return FileHelper.makeTempDir(prefix, priv_dir);
    }

    @Override
    public boolean packageKeyExists(String key)
            throws PackageException
    {
        File f = new File(myRoot, key);
        return f.exists();
    }

    @Override
    public void storeInstallDir(File dir, String key, Package pkg)
            throws PackageException
    {
        // move the temporary dir content to the repository
        File dest = new File(myRoot, key);
        FileHelper.renameTmpDir(dir, dest);
        FileSystemResolver resolver = getResolver(pkg);
        resolver.setPkgDir(dest);
    }

    @Override
    public void updatePackageLists(Package pkg)
            throws PackageException
    {
        File dir = ensurePrivateDir();
        File xml_file = new File(dir, "packages.xml");
        if ( ! xml_file.exists() ) {
            createPackagesXml(xml_file, pkg);
        }
        else {
            updatePackagesXml(xml_file, pkg);
        }
        File txt_file = new File(dir, "packages.txt");
        if ( ! txt_file.exists() ) {
            createPackagesTxt(txt_file, pkg);
        }
        else {
            updatePackagesTxt(txt_file, pkg);
        }
    }

    @Override
    public void remove(Package pkg)
            throws PackageException
    {
        File priv_dir = ensurePrivateDir();
        FileSystemResolver resolver = getResolver(pkg);
        // remove the entries from the packages.* files
        String dir = resolver.getDirName();
        File xml_file = new File(priv_dir, "packages.xml");
        removePackageInXml(xml_file, dir);
        File txt_file = new File(priv_dir, "packages.txt");
        removePackageInTxt(txt_file, dir);
        // actually delete the files
        deleteDirRecurse(resolver.myPkgDir);
    }

    private FileSystemResolver getResolver(Package pkg)
            throws PackageException
    {
        Storage.PackageResolver base_resolver = pkg.getResolver();
        if ( ! (base_resolver instanceof FileSystemResolver) ) {
            throw new PackageException("The package has not been installed in this storage.");
        }
        return (FileSystemResolver) base_resolver;
    }

    private void removePackageInXml(File file, String dir)
            throws PackageException
    {
        if ( ! file.exists() ) {
            throw new PackageException("The file does not exist, whilst removing a package: " + file);
        }
        try {
            // cache the compiled stylesheet?
            ClassLoader loader = FileSystemStorage.class.getClassLoader();
            InputStream style_in = loader.getResourceAsStream(REMOVE_PACKAGE_XSL);
            if ( style_in == null ) {
                throw new PackageException("Resource not found: " + REMOVE_PACKAGE_XSL);
            }
            Source style_src = new StreamSource(style_in);
            style_src.setSystemId(REMOVE_PACKAGE_XSL);
            Templates style = TransformerFactory.newInstance().newTemplates(style_src);
            Source src = new StreamSource(file);
            StringWriter res_out = new StringWriter();
            Result res = new StreamResult(res_out);
            Transformer trans = style.newTransformer();
            trans.setParameter("dir", dir);
            trans.transform(src, res);
            OutputStream out = new FileOutputStream(file);
            out.write(res_out.getBuffer().toString().getBytes());
            out.close();
        }
        catch ( TransformerConfigurationException ex ) {
            throw new PackageException("Impossible to compile the stylesheet: " + ADD_PACKAGE_XSL, ex);
        }
        catch ( TransformerException ex ) {
            throw new PackageException("Error transforming " + file, ex);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found (wtf? - I just transformed it): " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + file, ex);
        }
    }

    private void removePackageInTxt(File file, String dir)
            throws PackageException
    {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            StringWriter buffer = new StringWriter();
            String line;
            while ( (line = in.readLine()) != null ) {
                int pos = line.indexOf(' ');
                String d = line.substring(0, pos);
                // we don't write the line of the dir of the package to remove
                if ( ! d.equals(dir) ) {
                    buffer.write(line);
                    buffer.write("\n");
                }
            }
            in.close();
            Writer out = new FileWriter(file);
            out.write(buffer.getBuffer().toString());
            out.close();
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + file, ex);
        }
    }

    /**
     * Delete a complete directory (with its descendants).
     */
    private void deleteDirRecurse(File dir)
            throws PackageException
    {
        File[] children = dir.listFiles();
        if ( children != null ) {
            for ( File child : children ) {
                deleteDirRecurse(child);
            }
        }
        if ( ! dir.delete() ) {
            throw new PackageException("Error deleting a dir: " + dir);
        }
    }

    /**
     * Ensure the private directory exists.
     *
     * This method must be used instead of using {@code myPrivate} directly.
     */
    private synchronized File ensurePrivateDir()
            throws PackageException
    {
        if ( myPrivate == null ) {
            File dir = new File(myRoot, ".expath-pkg");
            FileHelper.ensureDir(dir);
            myPrivate = dir;
        }
        return myPrivate;
    }

    private void createPackagesXml(File file, Package pkg)
            throws PackageException
    {
        try {
            FileSystemResolver resolver = getResolver(pkg);
            Writer out = new FileWriter(file);
            out.write("<packages xmlns=\"http://expath.org/ns/repo/packages\">\n");
            out.write("   <package name=\"");
            out.write(pkg.getName());
            out.write("\"\n");
            out.write("            dir=\"");
            out.write(resolver.getDirName());
            out.write("\"\n");
            out.write("            version=\"");
            out.write(pkg.getVersion());
            out.write("\"/>\n");
            out.write("</packages>\n");
            out.close();
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("Impossible to create the packages xml list: " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error creating the packages xml list: " + file, ex);
        }
    }

    /**
     * Use a stylesheet to update packages.xml.
     *
     * Because we want to write the result back to the same file as the input,
     * we want to be sure we won't delete it first to read it.  So we buffer
     * the output.  In order to avoid creating a temporary file, we buffer the
     * result in memory, by serializing it to a string.  This is ok for such a
     * small file.
     */
    private void updatePackagesXml(File file, Package pkg)
            throws PackageException
    {
        try {
            // cache the compiled stylesheet?
            ClassLoader loader = FileSystemStorage.class.getClassLoader();
            InputStream style_in = loader.getResourceAsStream(ADD_PACKAGE_XSL);
            if ( style_in == null ) {
                throw new PackageException("Resource not found: " + ADD_PACKAGE_XSL);
            }
            Source style_src = new StreamSource(style_in);
            style_src.setSystemId(ADD_PACKAGE_XSL);
            Templates style = TransformerFactory.newInstance().newTemplates(style_src);
            Source src = new StreamSource(file);
            StringWriter res_out = new StringWriter();
            Result res = new StreamResult(res_out);
            Transformer trans = style.newTransformer();
            FileSystemResolver resolver = getResolver(pkg);
            trans.setParameter("name", pkg.getName());
            trans.setParameter("dir", resolver.getDirName());
            trans.setParameter("version", pkg.getVersion());
            trans.transform(src, res);
            OutputStream out = new FileOutputStream(file);
            out.write(res_out.getBuffer().toString().getBytes());
            out.close();
        }
        catch ( TransformerConfigurationException ex ) {
            throw new PackageException("Impossible to compile the stylesheet: " + ADD_PACKAGE_XSL, ex);
        }
        catch ( TransformerException ex ) {
            throw new PackageException("Error transforming packages.xml", ex);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found (wtf? - I just transformed it): " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + file, ex);
        }
    }

    private void createPackagesTxt(File file, Package pkg)
            throws PackageException
    {
        try {
            FileSystemResolver resolver = getResolver(pkg);
            Writer out = new FileWriter(file);
            out.write(resolver.getDirName());
            out.write(" ");
            out.write(pkg.getName());
            out.write(" ");
            out.write(pkg.getVersion());
            out.write("\n");
            out.close();
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("Impossible to create the packages text list: " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error creating the packages text list: " + file, ex);
        }
    }

    /**
     * For the same reasons as for updatePackagesXml(), we first create the file in memory.
     */
    private void updatePackagesTxt(File file, Package pkg)
            throws PackageException
    {
        FileSystemResolver resolver = getResolver(pkg);
        String pkg_name = pkg.getName();
        String pkg_dir = resolver.getDirName();
        String pkg_version = pkg.getVersion();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            StringWriter buffer = new StringWriter();
            String line;
            while ( (line = in.readLine()) != null ) {
                int pos = line.indexOf(' ');
                String dir = line.substring(0, pos);
                ++pos;
                int pos2 = line.indexOf(' ', pos);
                String name = line.substring(pos, pos2);
                pos = pos2 + 1;
                pos2 = line.length();
                String version = line.substring(pos, pos2);
                // we don't write the line if either the dir is the same, or if
                // both the name and the version are the same
                if ( ! dir.equals(pkg_dir) && ! ( name.equals(pkg_name) && version.equals(pkg_version) ) ) {
                    buffer.write(line);
                    buffer.write("\n");
                }
            }
            in.close();
            Writer out = new FileWriter(file);
            out.write(buffer.getBuffer().toString());
            out.write(pkg_dir);
            out.write(" ");
            out.write(pkg_name);
            out.write(" ");
            out.write(pkg_version);
            out.write("\n");
            out.close();
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + file, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + file, ex);
        }
    }

    private static final String ADD_PACKAGE_XSL    = "org/expath/pkg/repo/rsrc/add-package.xsl";
    private static final String REMOVE_PACKAGE_XSL = "org/expath/pkg/repo/rsrc/remove-package.xsl";
    private static final Logger LOG = Logger.getLogger(FileSystemStorage.class);
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
