/****************************************************************************/
/*  File:       ClasspathStorage.java                                       */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-10-09                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.util.Logger;
import org.expath.pkg.repo.util.PackageTxt;

/**
 * Storage using the classpath.
 *
 * @author Florent Georges
 * @date   2010-10-09
 */
public class ClasspathStorage
        extends Storage
{
    /**
     * @param root The common prefix for all resources.
     */
    public ClasspathStorage(String root)
    {
        myRoot = root;
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public PackageResolver makePackageResolver(String rsrc_name, String abbrev)
            throws PackageException
    {
        String pkg_root = myRoot.replace('.', '/') + "/" + rsrc_name + "/";
        return new ClasspathResolver(pkg_root, abbrev, rsrc_name);
    }

    @Override
    public Set<String> listPackageDirectories()
            throws PackageException
    {
        InputStream pkg_txt = null;
        String rsrc = myRoot.replace('.', '/') + "/" + ".expath-pkg/packages.txt";
        ClassLoader loader = ClasspathStorage.class.getClassLoader();
        InputStream res = loader.getResourceAsStream(rsrc);
        LOG.fine("Resolve resource .expath-pkg/packages.txt to ''{0}''", res);
        return PackageTxt.parseDirectories(pkg_txt);
    }

    @Override
    public void beforeInstall(boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        throw new UnsupportedOperationException("Classpath storage is read only.");
    }

    @Override
    public File makeTempDir(String prefix)
            throws PackageException
    {
        throw new UnsupportedOperationException("Classpath storage is read only.");
    }

    @Override
    public boolean packageKeyExists(String key)
            throws PackageException
    {
        throw new UnsupportedOperationException(
                "Could be impemented on classpath storage, but is only used to install...");
    }

    @Override
    public void storeInstallDir(File dir, String key, Package pkg)
            throws PackageException
    {
        throw new UnsupportedOperationException("Classpath storage is read only.");
    }

    @Override
    public void updatePackageLists(Package pkg)
            throws PackageException
    {
        throw new UnsupportedOperationException("Classpath storage is read only.");
    }

    @Override
    public void remove(Package pkg)
            throws PackageException
    {
        throw new UnsupportedOperationException("Classpath storage is read only.");
    }

    @Override
    public String toString()
    {
        return "Classpath storage in " + myRoot;
    }

    private String myRoot;

    public static class ClasspathResolver
            extends PackageResolver
    {
        public ClasspathResolver(String pkg_root, String abbrev, String rsrc_name)
                throws PackageException
        {
            myPkgRoot = pkg_root;
            myRsrcName = rsrc_name;
            myLoader = ClasspathResolver.class.getClassLoader();
            myContent = getContent(myLoader, pkg_root, abbrev);
        }

        private static String getContent(ClassLoader loader, String pkg_root, String abbrev)
                throws PackageException
        {
            String old_style = pkg_root + abbrev + "/";
            String new_style = pkg_root + "content/";
            URL old_url = loader.getResource(old_style);
            URL new_url = loader.getResource(new_style);
            LOG.finer("Content dir ''{0}'' is ''{1}'', and ''{2}'' is ''{3}''", new_style, new_url, old_style, old_url);
            if ( old_url == null && new_url == null ) {
                String msg = "None of content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                throw new PackageException(msg);
            }
            else if ( old_url != null && new_url != null ) {
                String msg = "Both content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                throw new PackageException(msg);
            }
            else if ( old_url == null ) {
                // TODO: Any way to test it is a "directory", and not a "file"?
                return new_style;
            }
            else {
                // TODO: Any way to test it is a "directory", and not a "file"?
                LOG.info("Warning: package uses old-style content dir: ''{0}''", old_style);
                return old_style;
            }
        }

        @Override
        public String getResourceName()
        {
            return myRsrcName;
        }

        @Override
        public StreamSource resolveResource(String path)
                throws PackageException
        {
            return resolveWithin(path, myPkgRoot);
        }

        @Override
        public StreamSource resolveComponent(String path)
                throws PackageException
        {
            return resolveWithin(path, myContent);
        }

        private StreamSource resolveWithin(String path, String root)
                throws PackageException
        {
            if ( path.startsWith("/") ) {
                path = path.substring(1);
            }
            String rsrc = root + path;
            InputStream in = myLoader.getResourceAsStream(rsrc);
            if ( in == null ) {
                return null;
            }
            // FIXME:
            URL sysid = myLoader.getResource(rsrc);
            if ( sysid == null ) {
                throw new PackageException("The resource exists, but has no URL: " + rsrc);
            }
            StreamSource src = new StreamSource(in);
            src.setSystemId(sysid.toString());
            return src;
        }

        private String myPkgRoot;
        private String myContent;
        private String myRsrcName;
        private ClassLoader myLoader;
    }

    private static final Logger LOG = Logger.getLogger(ClasspathStorage.class);
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
