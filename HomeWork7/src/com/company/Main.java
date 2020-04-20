package com.company;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        start(TestsClass.class);
    }

    private static void start(Class clazz) throws Exception {
        Object testObj = clazz.newInstance();
        ArrayList<Method> testsArray = new ArrayList<>();
        Method beforeMethod = null;
        Method afterMethod = null;

        for (Method test : clazz.getDeclaredMethods()) {
            if (test.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeMethod == null) beforeMethod = test;
                else throw new RuntimeException("В классе более одного метода с аннотацией BeforeSuite");
            }
            if (test.isAnnotationPresent(Test.class)) {
                testsArray.add(test);
            }
            if (test.isAnnotationPresent(AfterSuite.class)) {
                if (afterMethod == null) afterMethod = test;
                else throw new RuntimeException("В классе более одного метода с аннотацией AfterSuite");
            }
            testsArray.sort((test1, test2) -> test2.getAnnotation(Test.class).priority() - test1.getAnnotation(Test.class).priority());
        }

        if (beforeMethod != null) beforeMethod.invoke(testObj, null);
        for (Method test : testsArray) test.invoke(testObj, null);
        if (afterMethod != null) afterMethod.invoke(testObj, null);
    }
}
