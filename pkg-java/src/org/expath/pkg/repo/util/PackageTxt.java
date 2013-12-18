/****************************************************************************/
/*  File:       PackageTxt.java                                             */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-02-11                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.expath.pkg.repo.PackageException;

/**
 * TODO: ...
 *
 * @author Florent Georges
 */
public class PackageTxt
{
    /**
     * Implement listPackageDirectories() from package.txt.
     */
    public static Set<String> parseDirectories(InputStream pkg_txt)
            throws PackageException
    {
        Set<String> result = new HashSet<String>();
        try {
            if ( pkg_txt == null ) {
                // return an empty set if the list does not exist
                // that can be the case for instance when the repo is still empty
                return result;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(pkg_txt));
            String line;
            while ( (line = in.readLine()) != null ) {
                int pos = line.indexOf(' ');
                String dir = line.substring(0, pos);
                result.add(dir);
            }
            return result;
        }
        catch ( IOException ex ) {
            throw new PackageException("Error reading the package list", ex);
        }
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
