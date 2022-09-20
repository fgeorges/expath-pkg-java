/****************************************************************************/
/*  File:       PkgConfigurer.java                                          */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.config.XMLCalabashConfigurer;
import com.xmlcalabash.config.JaxpConfigurer;
import com.xmlcalabash.config.JingConfigurer;
import com.xmlcalabash.config.SaxonConfigurer;
import com.xmlcalabash.config.XProcConfigurer;
import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.util.DefaultJaxpConfigurer;
import com.xmlcalabash.util.DefaultJingConfigurer;
import com.xmlcalabash.util.DefaultSaxonConfigurer;
import com.xmlcalabash.util.DefaultXMLCalabashConfigurer;
import java.io.File;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.tools.Logger;

/**
 * Calabash configurer factory for the EXPath Packaging System.
 *
 * @author Florent Georges
 */
public class PkgConfigurer
        implements XProcConfigurer
{
    public PkgConfigurer(XProcRuntime runtime)
    {
        myRuntime = runtime;
        String repo_value = System.getProperty("org.expath.pkg.calabash.repo");
        LOG.fine("org.expath.pkg.calabash.repo: {0}", repo_value);
        if ( repo_value == null ) {
            repo_value = System.getenv("EXPATH_REPO");
            LOG.fine("$EPATH_REPO: {0}", repo_value);
        }
        if ( repo_value == null ) {
            // TODO: Detect if --debug is enabled, and then display a message
            // properly formatted for humans (not through logs), including basic
            // doc and links to reference material.
            LOG.info("Unable to locate the EXPath repository, cannot set up packaging");
            LOG.info("Use ++repo, org.expath.pkg.calabash.repo, or $EXPATH_REPO to enable it");
        }
        else {
            LOG.info("Initialize EXPath Packaging with: {0}", repo_value);
            try {
                Storage storage = new FileSystemStorage(new File(repo_value));
                myRepo = new Repository(storage);
            }
            catch ( PackageException ex ) {
                throw new XProcException("Error instantiating the EXPath repository on " + repo_value, ex);
            }
        }
    }

    public PkgConfigurer(XProcRuntime runtime, Repository repo)
    {
        myRuntime = runtime;
        myRepo    = repo;
    }

    @Override
    public XMLCalabashConfigurer getXMLCalabashConfigurer()
    {
        if ( myCalabash == null ) {
            myCalabash = myRepo == null
                    ? new DefaultXMLCalabashConfigurer(myRuntime)
                    : new PkgCalabashConfigurer(myRuntime, myRepo);
        }
        return myCalabash;
    }

    @Override
    public JaxpConfigurer getJaxpConfigurer()
    {
        if ( myJaxp == null ) {
            myJaxp = myRepo == null
                    ? new DefaultJaxpConfigurer()
                    : new PkgJaxpConfigurer(myRepo);
        }
        return myJaxp;
    }

    @Override
    public JingConfigurer getJingConfigurer()
    {
        if ( myJing == null ) {
            myJing = myRepo == null
                    ? new DefaultJingConfigurer()
                    : new PkgJingConfigurer(myRepo);
        }
        return myJing;
    }

    @Override
    public SaxonConfigurer getSaxonConfigurer()
    {
        if ( mySaxon == null ) {
            try {
                mySaxon = myRepo == null
                        ? new DefaultSaxonConfigurer()
                        : new PkgSaxonConfigurer(myRepo);
            }
            catch ( PackageException ex ) {
                // TODO:
                throw new RuntimeException("FIXME: Implement proper error mgmt", ex);
            }
        }
        return mySaxon;
    }

    private XProcRuntime          myRuntime;
    private Repository            myRepo;
    private XMLCalabashConfigurer myCalabash = null;
    private JaxpConfigurer        myJaxp     = null;
    private JingConfigurer        myJing     = null;
    private SaxonConfigurer       mySaxon    = null;
    private static final Logger LOG = Logger.getLogger(PkgConfigurer.class);
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
