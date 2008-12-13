package org.gmock

abstract class GMockTestCase extends GroovyTestCase {

    protected gMockController = new GMockController()

    protected mock = gMockController.&mock

    protected play = gMockController.&play

    protected match = GMock.&match

    public void runBare() {
        Throwable exception = null
        try {
            super.runBare()
        } catch (Throwable e) {
            exception = e
        } finally {
            try {
                gMockController.stop()
            } catch (Throwable e) {
                exception = exception ?: e
            }
        }
        if (exception) throw exception
    }

}
