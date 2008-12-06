package org.gmock;

import groovy.lang.GroovyObject;

public class JTest {

    public static void testLogger(Logger logger) {
        logger.info("call on java side");
    }

    public static void testFinalMethod(AClassWithFinalMethod obj) {
        obj.aFinalMethod();
    }

    public static void printMetaClass(GroovyObject go) {
        System.out.println(go.getMetaClass());
    }

}
