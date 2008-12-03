package org.gmock;

public class JTest {

    public void testLogger(Logger logger) {
        logger.info("call on java side");
    }

    public void testFinalMethod(AClassWithFinalMethod obj) {
        obj.aFinalMethod();
    }

}
