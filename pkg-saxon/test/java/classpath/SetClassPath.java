/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classpath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Test;

/**
 *
 * @author georgfl
 */
public class SetClassPath
{
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
        ClassLoader l1 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonsa9-1-0-7j/saxon9.jar") }, null);
        Thread.currentThread().setContextClassLoader(l1);
        Class<?> v1 = Class.forName("net.sf.saxon.Version", true, l1);
        Method m1 = v1.getMethod("getProductVersion");
        System.err.println(m1.invoke(null));

        ClassLoader l2 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonhe9-2-0-2j/saxon9he.jar") }, null);
        Thread.currentThread().setContextClassLoader(l2);
        Class<?> c2 = Class.forName("net.sf.saxon.Configuration", true, l2);
        Class<?> v2 = Class.forName("net.sf.saxon.Version", true, l2);
        Method m2 = v2.getMethod("getProductVariantAndVersion", c2);
        System.err.println(m2.invoke(null, c2.newInstance()));

        ClassLoader l3 = new URLClassLoader(new URL[]{ new URL("file:/d:/java/com/saxonica/saxonhe9-2-0-2j/saxon9he.jar") }, null);
        Thread.currentThread().setContextClassLoader(l3);
        Class<?> c3 = Class.forName("net.sf.saxon.Configuration", true, l3);
        Class<?> v3 = Class.forName("net.sf.saxon.Version", true, l3);
        Method m3 = v3.getMethod("getProductVariantAndVersion", c3);
        System.err.println(m3.invoke(null, c3.newInstance()));

        System.err.println("Eq c: " + ( c2 == c3 ) );
        System.err.println("Eq l: " + ( c2.getClassLoader() == c3.getClassLoader() ) );
    }
}
