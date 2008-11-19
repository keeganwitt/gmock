package org.gmock

abstract class GMockTestCase extends GroovyTestCase {

    def gMockController = new GMockController()

    def mock = gMockController.&mock

    def play = gMockController.&play

    def match = GMock.&match

}
