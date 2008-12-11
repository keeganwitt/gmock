package org.gmock

abstract class GMockTestCase extends GroovyTestCase {

    protected gMockController = new GMockController()

    protected mock = gMockController.&mock

    protected play = gMockController.&play

    protected match = GMock.&match

}
