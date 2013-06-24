package org.gmock.test

import org.gmock.WithGMock
import org.junit.Test
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

@TestMixin(GroovyPageUnitTestMixin)
@WithGMock
class ConstructorMockingBugTests {
	
	@Test void "mocking file constructor"() {
		mock(File, constructor('test.txt')) {
			getText() returns 'some text'
		}
		play {
			def file = new File('test.txt')
			assert 'some text' == file.text
		}
	}
	
}