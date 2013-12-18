/****************************************************************************/
/*  File:       QueryModuleResolver.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:                                                                   */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.lib.ModuleURIResolver;

import net.sf.saxon.trans.XPathException;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.URISpace;

/**
 * FIXME: Why does this class instantiate Repository?  Why is it not constructed
 * with a SaxonRepository as param?
 * 
 * @author Florent Georges
 */
public class QueryModuleResolver
        implements ModuleURIResolver
{
    /** Constructor */
    public QueryModuleResolver()
            throws PackageException
    {
        // TODO: FIXME: Clean up!
        String repo_name = System.getenv("EXPATH_REPO");
        File repo_file = new File(repo_name);
        Storage storage = new FileSystemStorage(repo_file);
        myRepo = new Repository(storage);
    }

    @Override
    public StreamSource[] resolve(String module_uri, String base_uri, String[] locations)
            throws XPathException
    {
        // can arise for instance when compiling XQuery from a file and using
        // the option -u (treat everything as a URI, so here, we only have a
        // location, and it is tried to be resolved...)
        if ( module_uri == null ) {
            return null;
        }

        Source result = null;
        try {
            result = myRepo.resolve(module_uri, URISpace.XQUERY);
        }
        catch ( PackageException ex ) {
            // ignore
            // TODO: Really?
        }
        if ( result == null ) {
            return null;
        }
        // TODO: Why requiring a StreamSource here...?
        if ( ! (result instanceof StreamSource) ) {
            throw new XPathException("The Source is not a StreamSource");
        }
        return new StreamSource[]{ (StreamSource) result };
    }

    /** The underlying catalog */
    private Repository myRepo;
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
