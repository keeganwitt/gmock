package org.gmock

import junit.framework.AssertionFailedError

class FunctionalTest extends GMockTestCase {

    void testBasic(){
        def mockLoader = mock()
        mockLoader.load('key').returns('value')
        play {
            assertEquals "value", mockLoader.load('key')
        }
    }


    void testCallMethodToManyTime(){
        def mockLoader = mock()
        mockLoader.load('foo').returns('result1')
        mockLoader.load('bar').returns('result1')
        mockLoader.load('bar').returns('result1')
        def message
        play {
            try {
                mockLoader.load("foo")
                mockLoader.load("bar")
                mockLoader.load("bar")
                mockLoader.load("bar")
                fail("Should have throw an exception")
            } catch (AssertionFailedError e){
                message = e.message
            }
        }
        def expected = "Unexpected method call 'load(\"bar\")'\n" +
                       "  'load(\"foo\")': expected 1, actual 1\n" +
                       "  'load(\"bar\")': expected 2, actual 2"
        assertEquals expected, message
    }

    void testMultipleArguments(){
        def mockLoader1 = mock()
        def mockLoader2 = mock()
        mockLoader1.put("1A", "1B").returns("result1")
        mockLoader2.put("2A", "2B").returns("result2")

        play {
            assertEquals "result1", mockLoader1.put("1A", "1B")
            assertEquals "result2", mockLoader2.put("2A", "2B")
        }
    }


  void testMultipleArgumentsSameMock(){
    def mockLoader = mock()
    mockLoader.load("key1", "other1").returns("key1 other1")
    mockLoader.load("key2", "other2").returns("key2 other2")
    play {
        assertEquals "key1 other1", mockLoader.load("key1", "other1")
        assertEquals "key2 other2", mockLoader.load("key2", "other2")
    }
  }


  void testMethodNotCall(){
    def mockLoader = mock()
    mockLoader.load("load1").returns("result")
    mockLoader.load("load2").returns("result")
    mockLoader.load("load2").returns("result")
    def expected = "Expectation not matched on verify:\n" +
                   "  'load(\"load1\")': expected 1, actual 0\n" +
                   "  'load(\"load2\")': expected 2, actual 0"

    try {
      play{}
      fail("Should have throw an exception")
    } catch (AssertionFailedError e){
      assertEquals expected, e.message
    }
  }

  void testVerifyObjectNotReplayed(){
      def mockLoader = mock()
      try {
          mockLoader._verify()
          fail("Should have throw an exception")
      } catch (Throwable e){}
  }


    void testMultipleExpectation(){
        def mockLoader = mock()
        mockLoader.load("key").returns("result")
        mockLoader.put("key")
        play {
            assertEquals "result", mockLoader.load("key")
            assertNull mockLoader.put("key")
        }
    }

    void testCallNotExpected(){
        def mockLoader = mock()
        play {
            try {
                mockLoader.load("key")
                fail("Should have throw an exception")
            } catch (AssertionFailedError e){
                assertEquals "Unexpected method call 'load(\"key\")'", e.message
            }
        }
    }

    void testSameExpectation(){
        def mockLoader = mock()
        mockLoader.load('key').returns('first')
        mockLoader.load('key').returns('second')
        play {
            assertEquals "first", mockLoader.load("key")
            assertEquals "second", mockLoader.load("key")
        }
    }

    void testStubMethod(){
        def mockLoader = mock()
        mockLoader.load("key").returns("stub").stub()
        play {
            3.times {
                assertEquals 'stub', mockLoader.load('key')
            }
        }
    }

    void testStubedMethodNotCall(){
        def mockLoader = mock()
        mockLoader.load("key").returns("stub").stub()
        play{}
    }

    void testRaiseException(){
        def mockLoader = mock()
        mockLoader.load("key").raises(new IllegalArgumentException())
        play {
            shouldFail(IllegalArgumentException){
                mockLoader.load("key")
            }
        }
    }

    void testRaiseExceptionNotCalled(){
        def mockLoader = mock()
        mockLoader.load("key").raises(new IllegalArgumentException())
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(\"key\")': expected 1, actual 0"

        try {
            play {}
        } catch (AssertionFailedError e){
            assertEquals expected, e.message
        }
    }

    void testRaiseExceptionClass() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException)

        play {
            shouldFail(IllegalArgumentException) {
                mockLoader.load("key")
            }
        }
    }

    void testRaiseExceptionClassWithMessage() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException, "error message")

        play {
            def expected = "error message"
            def message = shouldFail(IllegalArgumentException) {
                mockLoader.load("key")
            }
            assertEquals expected, message
        }
    }

    void testRaiseExceptionClassWithMessageAndCause() {
        def mockLoader = mock()
        def message = "error message"
        def cause = new Exception()
        mockLoader.load("key").raises(IllegalArgumentException, message, cause)

        play {
            try {
                mockLoader.load("key")
                fail("Should have throw an exception")
            } catch (IllegalArgumentException e) {
                assertEquals message, e.message
                assertEquals cause, e.cause
            }
        }
    }

    void testRaiseExceptionClassNotCalled() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException)
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(\"key\")': expected 1, actual 0"

        try {
            play {}
        } catch (AssertionFailedError e){
            assertEquals expected, e.message
        }
    }

    void testMultiplePlay(){
        def mockLoader = mock()
        mockLoader.load("key1").returns("value1")
        play {
            assertEquals "value1", mockLoader.load("key1")
        }

        mockLoader.load("key2").returns("value2")
        mockLoader.put("value3")
        play {
            assertEquals "value2", mockLoader.load("key2")
            assertNull mockLoader.put("value3")
        }
    }


}

