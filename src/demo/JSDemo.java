package com.almasb.java.framework.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JSDemo {

    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        manager.getEngineFactories().forEach(
                factory -> System.out.println(factory.getEngineName() + " "
                        + factory.getLanguageName() + " " + factory.getEngineVersion()
                        + " " + factory.getLanguageVersion()));
        ScriptEngine engine = manager.getEngineByName("JavaScript");


        // read script file
        try (InputStream is = JSDemo.class.getResourceAsStream("test.js");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            engine.eval(br);

            Invocable inv = (Invocable) engine;

            // call function from script file
            Object var = inv.invokeFunction("myFunc");

            System.out.println(var);
            System.out.println(var.getClass());

//            // array
//            ScriptObjectMirror som = (ScriptObjectMirror) var;
//
//            System.out.println(var.getClass().getSimpleName());
//            System.out.println(som.values().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // test.js contains

    //    function myFunc() {
    //        var result = ["hi", "ho", "yoo"];
    //        return result;
    //    }
    //
    //    function helloFromJS() {
    //        return "Hello Java, I'm calling you from JavaScript";
    //    }
}
