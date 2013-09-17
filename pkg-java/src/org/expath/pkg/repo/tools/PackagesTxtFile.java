/****************************************************************************/
/*  File:       PackagesXmlFile.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;

/**
 * Represent the file [repo]/.expath-pkg/packages.txt.
 *
 * @author Florent Georges
 * @date   2013-09-17
 */
public class PackagesTxtFile
        extends UpdatableFile
{
    public PackagesTxtFile(File file)
            throws PackageException
    {
        super(file);
    }

    public void addPackage(Package pkg, String pkg_dir)
            throws PackageException
    {
        String pkg_name = pkg.getName();
        String pkg_version = pkg.getVersion();
        try {
            BufferedReader in = new BufferedReader(new FileReader(myFile));
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
            buffer.write(pkg_dir);
            buffer.write(" ");
            buffer.write(pkg_name);
            buffer.write(" ");
            buffer.write(pkg_version);
            buffer.write("\n");
            update(buffer);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    public void removePackageByDir(String dir)
            throws PackageException
    {
        try {
            BufferedReader in = new BufferedReader(new FileReader(myFile));
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
            update(buffer);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    protected void createEmpty(Writer out)
            throws IOException
    {
        out.write("\n");
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
