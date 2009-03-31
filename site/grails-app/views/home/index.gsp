<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Gmock: Mocking Framework for Groovy</title>
    <meta name="layout" content="main"/>
</head>


<body>

<div class="container">
    <div class="span-13" id="disclaimer">
        <p>Gmock is a mocking framework for Groovy focused on test readability and conciseness</p>

        <p>Spend more time writing code and less writing test </p>
    </div>
    <div class="span-4">&nbsp;</div>
    <div class="span-7 last" id="download">
        <a href="http://gmock.googlecode.com/files/gmock-<g:currentVersion/>.jar">
            <h3>Download</h3>

            <div>Gmock release <g:currentVersion/></div>
        </a>

    </div>

    <div class="span-24">
        <h1>Gmock</h1>

        <p>
            Gmock is all about simple syntax and readability of your tests so you spend less time learning the
            framework and more writing code. To use Gmock just drop the gmock jar file in your classpath.
        </p>

        <p>Gmock support Java strong typing and make it perfectly suitable to test an full Java project.</p>

        <p>The current version is gmock-<g:currentVersion/> and is already packed with tons of feature.</p>

        <p>Checkout our documentation to get the full picture and visit our roadmap to see what's coming next.</p>

        <h1>In A Nutshell</h1>
        <ul>
            <li>Method mocking: <tt>mockLoader.load("fruit").returns("apple")</tt></li>
            <li>Exception mocking: <tt>mockLoader.load("unknown").raises(new RuntimeException())</tt></li>
            <li>Stub mocking: <tt>mockLoader.load("fruit").returns("apple").stub()</tt></li>
            <li>Static method mocking: <tt>mockMath.static.random().returns(0.5)</tt></li>
            <li>Property mocking: <tt>mockLoader.name.returns("loader")</tt></li>
            <li>Constructor mocking: <tt>def mockFile = mock(File, constructor('/a/path/file.txt'))</tt></li>
            <li>Partial mocking: <tt>mock(controller).params.returns([id: 3])</tt></li>
            <li>Times expectation: <tt>mockLoader.load("fruit").returns("apple").atLeastOnce()</tt></li>
            <li>Custom matcher: <tt>mockLoader.load(match{ it.startsWith("fru") })</tt></li>
            <li>Strict ordering:  <tt>ordered {  ... }</tt></li>
            <li>Optional support for <a href="http://code.google.com/p/hamcrest/">Hamcrest matcher:</a>
                <tt>mockLoader.put("test", is(not(lessThan(5))))</tt>
            </li>
            <li>GMockController if you can't extend GMockTestCase in your test</li>
        </ul>

        <h1>Getting Started</h1>

        <p>
            First extend the
            <tt>org.gmock.GMockTestCase</tt>
            . Create mock object using the
            <tt>mock()</tt>
            method. You setup expectation simply by calling method on your mock.
        </p>
        <pre class="prettyprint">
def mockLoader = mock()
mockLoader.load("fruit").returns("apple")</pre>

        <p>The code you are testing should be executed within the <tt>play</tt> closure.</p>
        <pre class="prettyprint">
void testBasic(){
  // create mock and setup expectation
  play {
// run your code
  }
}</pre>

    </div>

</div>

</body>
</html>
