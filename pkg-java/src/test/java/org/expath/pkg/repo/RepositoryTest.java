/****************************************************************************/
/*  File:       RepositoryTest.java                                         */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-12-04                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.File;
import java.util.Collection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.expath.pkg.repo.TestConstants.REPOS_LOCATION;

/**
 * TODO: ...
 *
 * @author Florent Georges
 * @date   2010-12-04
 */
public class RepositoryTest
{
    @Test
    public void testConstructor()
            throws Exception
    {
        final Storage storage = new FileSystemStorage(new File(REPOS_LOCATION, "simple"));
        final Repository sut = new Repository(storage);
        final Collection<Packages> packages_list = sut.listPackages();
        assertEquals("number of packages", 1, packages_list.size());
        final Packages packages = packages_list.iterator().next();
        assertNotNull("packages not null", packages);
        assertEquals("packages name", HELLO_NAME, packages.name());
        assertNotNull("latest not null", packages.latest());
        assertNotNull("get 1.1.1 not null", packages.version("1.1.1"));
        assertNull("get 1.0 null", packages.version("1.0"));
        assertNull("get 1 null", packages.version("1"));
        assertSame("latest =is= get 1.1", packages.latest(), packages.version("1.1.1"));
        assertEquals("get 1.1.1 version", "1.1.1", packages.version("1.1.1").getVersion());
        final Collection<Package> package_list = packages.packages();
        assertEquals("number of package versions", 1, package_list.size());
        final Package pkg = package_list.iterator().next();
        assertNotNull("package not null", pkg);
        assertEquals("version", "1.1.1", pkg.getVersion());
        assertEquals("name", HELLO_NAME, pkg.getName());
    }

    private static final String HELLO_NAME = "http://www.example.org/lib/hello";
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
/*  Contributor(s): Adam Retter                                             */
/* ------------------------------------------------------------------------ */
