package org.gmock

abstract class GMockTestCase extends GroovyTestCase {

    protected gMockController = new GMockController()

    protected mock = gMockController.&mock

    protected play = gMockController.&play

    protected match = GMock.&match

    // TODO: find out a better place for restoring the original meta classes of final Java classes    
    protected void tearDown() {
        super.tearDown()
        gMockController.stop()
    }

}
