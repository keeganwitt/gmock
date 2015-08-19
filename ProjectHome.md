# Gmock #

**Gmock is a mocking framework for the Groovy language.**

Gmock is all about simple syntax and readability of your tests so you spend less time learning the framework and more writing code. To use Gmock just drop the gmock jar file in your classpath.

The current version is gmock-0.8.3 and is packed with tons of feature.

Checkout the full [documentation](http://code.google.com/p/gmock/wiki/Documentation_0_8) to get the full picture.

**Any contribution is welcome, including, but not limited to, raising an issue, submitting a patch, improving the document. If you want to become a member of the project, please contact [me](mailto:johnnyjianhy@gmail.com).**

## News ##

  * **23/6/2013** release of Gmock 0.8.3. This is a maintenance release with a few bugs fixes
  * **13/12/2011** release of Gmock 0.8.2. This is a maintenance release with a few bugs fixes and the upgrade to Groovy 1.8.4 and compatible with the latest Grails 2.0

## Getting Started ##

First extend the `org.gmock.GMockTestCase`. Create mock object using the `mock()` method. You setup expectation simply by calling method on your mock.
```
def mockLoader = mock()
mockLoader.load("fruit").returns("apple")
```

The code you are testing should be executed within the `play` closure.
```
void testBasic(){
  // create mock and setup expectation
  play {
    // run your code
  }
}
```


## In A Nutshell ##

  * Method mocking: `mockLoader.load("fruit").returns("apple")`
  * Exception mocking: `mockLoader.load("unknown").raises(new RuntimeException())`
  * Stub mocking: `mockLoader.load("fruit").returns("apple").stub()`
  * Static method mocking: `mockMath.static.random().returns(0.5)`
  * Property mocking: `mockLoader.name.returns("loader")`
  * Constructor mocking: `def mockFile = mock(File, constructor('/a/path/file.txt'))`
  * Partial mocking: `mock(controller).params.returns([id: 3])`
  * Times expectation: `mockLoader.load("fruit").returns("apple").atLeastOnce()`
  * Custom matcher: `mockLoader.load(match{ it.startsWith("fru") })`
  * Strict ordering: `ordered {  ... }`
  * Optional support for [Hamcrest](http://code.google.com/p/hamcrest/) matcher: `mockLoader.put("test", is(not(lessThan(5))))`
  * GMockController if you can't extend GMockTestCase in your test


Here is a full example:
```
import org.gmock.GMockTestCase

class LoaderTest extends GMockTestCase {
  void testLoader(){
    def mockLoader = mock()
    mockLoader.load('key').returns('value')
    play {
      assertEquals "value", mockLoader.load('key')
    }
  }
}  
```