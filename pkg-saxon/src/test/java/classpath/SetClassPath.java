/****************************************************************************/
/*  File:       SetClassPath.java                                           */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-07-28                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009, 2010 Florent Georges (see end of file.)         */
/* ------------------------------------------------------------------------ */
package classpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author georgfl
 */
public class SetClassPath
{
    @Ignore
    @Test
    public void twiceSameClass()
            throws ClassNotFoundException
                 , MalformedURLException
                 , NoSuchMethodException
                 , IllegalAccessException
                 , IllegalArgumentException
                 , InvocationTargetException
                 , InstantiationException
    {
        final ClassLoader l1 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonsa9-1-0-7j/saxon9.jar") }, null);
        Thread.currentThread().setContextClassLoader(l1);
        final Class<?> v1 = Class.forName("net.sf.saxon.Version", true, l1);
        final Method m1 = v1.getMethod("getProductVersion");
        System.err.println(m1.invoke(null));

        final ClassLoader l2 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonhe9-2-0-2j/saxon9he.jar") }, null);
        Thread.currentThread().setContextClassLoader(l2);
        final Class<?> c2 = Class.forName("net.sf.saxon.Configuration", true, l2);
        final Class<?> v2 = Class.forName("net.sf.saxon.Version", true, l2);
        final Method m2 = v2.getMethod("getProductVariantAndVersion", c2);
        System.err.println(m2.invoke(null, c2.newInstance()));

        final ClassLoader l3 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonhe9-2-0-2j/saxon9he.jar") }, null);
        Thread.currentThread().setContextClassLoader(l3);
        final Class<?> c3 = Class.forName("net.sf.saxon.Configuration", true, l3);
        final Class<?> v3 = Class.forName("net.sf.saxon.Version", true, l3);
        final Method m3 = v3.getMethod("getProductVariantAndVersion", c3);
        System.err.println(m3.invoke(null, c3.newInstance()));

        System.err.println("Eq c: " + ( c2 == c3 ) );
        System.err.println("Eq l: " + ( c2.getClassLoader() == c3.getClassLoader() ) );
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
/*  Contributor(s): Adam Retter                                             */
/* ------------------------------------------------------------------------ */