package com.almasb.java.framework.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokerDemo {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Invoker ");
            System.exit(1);
        }
        Class[] argTypes = new Class[1];
        argTypes[0] = String[].class;
        try {
            Method mainMethod = Class.forName(args[0]).getDeclaredMethod("main", argTypes);
            Object[] argListForInvokedMain = new Object[1];
            argListForInvokedMain[0] = new String[0];
            // Place whatever args you
            // want to pass into other
            // class's main here.
            mainMethod.invoke(null,
                    // This is the instance on which you invoke
                    // the method; since main is static, you can pass
                    // null in.
                    argListForInvokedMain);
        }
        catch (ClassNotFoundException ex) {
            System.err.println("Class " + args[0] + "not found in classpath.");
        }
        catch (NoSuchMethodException ex) {
            System.err.println("Class " + args[0] + "does not define public static void main(String[])");
        }
        catch (InvocationTargetException ex) {
            System.err.println("Exception while executing " + args[0] + ":" + ex.getTargetException());
        }
        catch (IllegalAccessException ex) {
            System.err.println("main(String[]) in class " + args[0] + " is not public");
        }
    }
}
