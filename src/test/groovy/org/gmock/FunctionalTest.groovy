package org.gmock

import junit.framework.AssertionFailedError

class FunctionalTest extends GMockTestCase {

    void testBasic(){
        def mockLoader = mock()
        mockLoader.load('key').andReturn('value')
        play {
            assertEquals "value", mockLoader.load('key')
        }
    }


    void testCallMethodToManyTime(){
        def mockLoader = mock()
        mockLoader.load('foo').andReturn('result1')
        mockLoader.load('bar').andReturn('result1')
        mockLoader.load('bar').andReturn('result1')
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
        def expected = "Unexpected method call 'load(bar)'\n"+
                       "  'load(foo)': expected 1, actual 1\n"+
                       "  'load(bar)': expected 2, actual 2"
        assertEquals expected, message
    }

    void testMultipleArguments(){
        def mockLoader1 = mock()
        def mockLoader2 = mock()
        mockLoader1.put("1A", "1B").andReturn("result1")
        mockLoader2.put("2A", "2B").andReturn("result2")

        play {
            assertEquals "result1", mockLoader1.put("1A", "1B")
            assertEquals "result2", mockLoader2.put("2A", "2B")
        }
    }


  void testMultipleArgumentsSameMock(){
    def mockLoader = mock()
    mockLoader.load("key1", "other1").andReturn("key1 other1")
    mockLoader.load("key2", "other2").andReturn("key2 other2")
    play {
        assertEquals "key1 other1", mockLoader.load("key1", "other1")
        assertEquals "key2 other2", mockLoader.load("key2", "other2")
    }
  }


  void testMethodNotCall(){
    def mockLoader = mock()
    mockLoader.load("load1").andReturn("result")
    mockLoader.load("load2").andReturn("result")
    mockLoader.load("load2").andReturn("result")
    def expected = "Expectation not matched on verify:\n" +
                "  'load(load1)': expected 1, actual 0\n" +
                "  'load(load2)': expected 2, actual 0"

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
        mockLoader.load("key").andReturn("result")
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
                assertEquals "Unexpected method call 'load(key)'", e.message
            }
        }
    }

    void testSameExpectation(){
        def mockLoader = mock()
        mockLoader.load('key').andReturn('first')
        mockLoader.load('key').andReturn('second')
        play {
            assertEquals "first", mockLoader.load("key")
            assertEquals "second", mockLoader.load("key")
        }
    }

    void testStubMethod(){
        def mockLoader = mock()
        mockLoader.load("key").andStubReturn("stub")
        play {
            3.times {
                assertEquals 'stub', mockLoader.load('key')
            }
        }
    }

    void testStubedMethodNotCall(){
        def mockLoader = mock()
        mockLoader.load("key").andStubReturn("stub")
        play{}
    }

    void testRaiseException(){
        def mockLoader = mock()
        mockLoader.load("key").andRaise(new IllegalArgumentException())
        play {
            shouldFail(IllegalArgumentException){
                mockLoader.load("key")
            }
        }
    }

    void testRaiseExceptionNotCalled(){
        def mockLoader = mock()
        mockLoader.load("key").andRaise(new IllegalArgumentException())
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(key)': expected 1, actual 0"

        try {
            play {}
        } catch (AssertionFailedError e){
            assertEquals expected, e.message
        }
    }

    void testMultiplePlay(){
        def mockLoader = mock()
        mockLoader.load("key1").andReturn("value1")
        play {
            assertEquals "value1", mockLoader.load("key1")
        }

        mockLoader.load("key2").andReturn("value2")
        mockLoader.put("value3")
        play {
            assertEquals "value2", mockLoader.load("key2")
            assertNull mockLoader.put("value3")
        }
    }


}

