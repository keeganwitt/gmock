<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Gmock: Documentation 0.5.0</title>
    <meta name="layout" content="main"/>
</head>


<body>

<div class="container">

    <div class="span-24 source">

        <h1><a name="Introduction">Introduction</a></h1>

        <p><a name="Introduction">Gmock is a mocking framework for the Groovy language. </a></p>

        <p><a name="Introduction">Gmock is all about simple syntax and readability of your tests so you spend less
            time learning the framework and more writing code. To use Gmock just drop the gmock jar file in your
            classpath and make sure you also have junit. </a></p>

        <p><a name="Introduction">This documention describe the version 0.5.0 of Gmock. </a></p>

        <h1><a name="Getting_Started">Getting Started</a></h1>
        <pre class="prettyprint"><a name="Getting_Started"><span class="kwd">import</span><span
                class="pln"> org</span><span class="pun">.</span><span class="pln">gmock</span><span
                class="pun">.</span><span class="typ">GMockTestCase</span><span class="pln"><br><br></span><span
                class="kwd">class</span><span class="pln"> </span><span class="typ">LoaderTest</span><span
                class="pln"> </span><span class="kwd">extends</span><span class="pln"> </span><span class="typ">GMockTestCase</span><span
                class="pln"> </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; </span><span
                class="kwd">void</span><span class="pln"> testLoader</span><span class="pun">(){</span><span
                class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span class="kwd">def</span><span class="pln"> mockLoader </span><span
                class="pun">=</span><span class="pln"> mock</span><span class="pun">()</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; mockLoader</span><span
                class="pun">.</span><span class="pln">load</span><span class="pun">(</span><span
                class="str">'key'</span><span class="pun">).</span><span class="pln">returns</span><span
                class="pun">(</span><span class="str">'value'</span><span class="pun">)</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; play </span><span
                class="pun">{</span><span
                class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; assertEquals </span><span class="str">"value"</span><span
                class="pun">,</span><span class="pln"> mockLoader</span><span class="pun">.</span><span class="pln">load</span><span
                class="pun">(</span><span class="str">'key'</span><span class="pun">)</span><span class="pln"><br>&nbsp; &nbsp; &nbsp; &nbsp; </span><span
                class="pun">}</span><span class="pln"><br>&nbsp; &nbsp; </span><span class="pun">}</span><span
                class="pln"><br></span><span class="pun">}</span><span class="pln"> &nbsp;</span></a></pre>
        <ul>
            <li><a name="Getting_Started">First extends the <tt>GMockTestCase</tt> </a></li>
            <li><a name="Getting_Started">Create mock object using the <tt>mock()</tt> method </a></li>
            <li><a name="Getting_Started">Setup expectation by calling the method you expect on your mock </a></li>
            <li><a name="Getting_Started">Run the code you are testing within the <tt>play</tt> closure </a></li>
        </ul>
        <h1><a name="Cookbook">Cookbook</a></h1>

        <h2><a name="Expectation_and_Play_mode">Expectation and Play mode</a></h2>

        <p><a name="Expectation_and_Play_mode">Mock object are created using the <tt>mock()</tt> method available in
            <tt>GMockTestCase</tt>. By default mock object will record method call and generate expectation. </a>
        </p>

        <p><a name="Expectation_and_Play_mode">The code under test should run through the <tt>play</tt> closure.
        </a></p>
        <pre class="prettyprint"><a name="Expectation_and_Play_mode"><span class="kwd">void</span><span class="pln"> testBasic</span><span
                class="pun">(){</span><span class="pln"><br>&nbsp; </span><span class="kwd">def</span><span
                class="pln"> aMock </span><span class="pun">=</span><span class="pln"> mock</span><span class="pun">()</span><span
                class="pln"><br>&nbsp; </span><span class="com">// setup expectation</span><span class="pln"><br>&nbsp; play </span><span
                class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; </span><span
                class="com">// run your code </span><span class="pln"><br>&nbsp; </span><span
                class="pun">}</span><span class="pln"><br></span><span class="pun">}</span></a></pre>
        <p><a name="Expectation_and_Play_mode">Gmock support Java strong typing. The <tt>mock()</tt> method takes an
            optional class. More in the strong typing section. </a></p>
        <pre class="prettyprint"><a name="Expectation_and_Play_mode"><span class="pln">&nbsp; </span><span
                class="typ">File</span><span class="pln"> mockFile </span><span class="pun">=</span><span
                class="pln"> mock</span><span class="pun">(</span><span class="typ">File</span><span
                class="pun">)</span></a></pre>
        <h2><a name="Mock_method_call">Mock method call</a></h2>

        <p><a name="Mock_method_call">Method call expectation are created when calling method on Mock. Return value
            can be setup using the <tt>returns</tt> keyword. </a></p>
        <pre class="prettyprint"><a name="Mock_method_call"><span class="kwd">def</span><span
                class="pln"> loader </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">()</span><span class="pln"><br>loader</span><span class="pun">.</span><span class="pln">put</span><span
                class="pun">(</span><span class="str">"fruit"</span><span class="pun">).</span><span class="pln">returns</span><span
                class="pun">(</span><span class="str">"apple"</span><span class="pun">)</span><span class="pln"><br>play </span><span
                class="pun">{</span><span class="pln"><br>&nbsp; assertEquals </span><span
                class="str">"apple"</span><span class="pun">,</span><span class="pln"> loader</span><span
                class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span
                class="str">"fruit"</span><span class="pun">)</span><span class="pln"> <br></span><span class="pun">}</span></a></pre>
        <p><a name="Mock_method_call">Exception can be setup using the <tt>raises</tt> keyword. </a></p>
        <pre class="prettyprint"><a name="Mock_method_call"><span class="kwd">def</span><span
                class="pln"> loader </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">()</span><span class="pln"><br>loader</span><span class="pun">.</span><span class="pln">put</span><span
                class="pun">(</span><span class="str">"throw exception"</span><span class="pun">).</span><span
                class="pln">raises</span><span class="pun">(</span><span class="kwd">new</span><span
                class="pln"> </span><span class="typ">RuntimeException</span><span class="pun">(</span><span
                class="str">"an exception"</span><span class="pun">))</span><span class="pln"> </span><span
                class="com">// or 'raises(RuntimeException, "an exception")'</span><span
                class="pln"><br>play </span><span class="pun">{</span><span class="pln"><br>&nbsp; </span><span
                class="kwd">def</span><span class="pln"> message </span><span class="pun">=</span><span class="pln"> shouldFail</span><span
                class="pun">(</span><span class="typ">RuntimeException</span><span class="pun">)</span><span
                class="pln"> </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; loader</span><span
                class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span class="str">"throw exception"</span><span
                class="pun">)</span><span class="pln"> <br>&nbsp; </span><span class="pun">}</span><span
                class="pln"><br>&nbsp; assertEquals </span><span class="str">"an exception"</span><span class="pun">,</span><span
                class="pln"> message<br></span><span class="pun">}</span></a></pre>
        <h2><a name="Mock_property_call">Mock property call</a></h2>

        <p><a name="Mock_property_call">Property call should be mock using the following syntax. For Setter and
            getter </a></p>
        <pre class="prettyprint"><a name="Mock_property_call"><span class="kwd">def</span><span
                class="pln"> loader </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">()</span><span class="pln"><br>loader</span><span class="pun">.</span><span class="pln">name</span><span
                class="pun">.</span><span class="kwd">set</span><span class="pun">(</span><span
                class="str">"a name"</span><span class="pun">)</span><span class="pln"><br>loader</span><span
                class="pun">.</span><span class="pln">name</span><span class="pun">.</span><span
                class="pln">returns</span><span class="pun">(</span><span class="str">"a different name"</span><span
                class="pun">)</span><span class="pln"><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; loader</span><span class="pun">.</span><span class="pln">name </span><span
                class="pun">=</span><span class="pln"> </span><span class="str">"a name"</span><span
                class="pln"><br>&nbsp; assertEquals </span><span class="str">"a different name"</span><span
                class="pun">,</span><span class="pln"> loader</span><span class="pun">.</span><span class="pln">name<br></span><span
                class="pun">}</span></a></pre>
        <p><a name="Mock_property_call">Support for exception and method stub is similar to standard method call.
            Ex: </a></p>
        <ul>
            <li><a name="Mock_property_call"><tt>loader.name.raises(RuntimeException)</tt> </a></li>
            <li><a name="Mock_property_call"><tt>loader.name.set("invalid).raises(new RuntimeException())</tt> </a>
            </li>
            <li><a name="Mock_property_call"><tt>mockLoader.name.returns('a name').stub()</tt> </a></li>
        </ul>
        <h2><a name="Mock_static_method_call">Mock static method call</a></h2>

        <p><a name="Mock_static_method_call">Mocking static method call is similar to standard method call, just add
            the static keyword: </a></p>
        <pre class="prettyprint"><a name="Mock_static_method_call"><span class="kwd">def</span><span class="pln"> mockMath </span><span
                class="pun">=</span><span class="pln"> mock</span><span class="pun">(</span><span
                class="typ">Math</span><span class="pun">)</span><span class="pln"><br>mockMath</span><span
                class="pun">.</span><span class="kwd">static</span><span class="pun">.</span><span class="pln">random</span><span
                class="pun">().</span><span class="pln">returns</span><span class="pun">(</span><span class="lit">0.5</span><span
                class="pun">)</span><span class="pln"><br><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; &nbsp;assertEquals </span><span class="lit">0.5</span><span
                class="pun">,</span><span class="pln"> </span><span class="typ">Math</span><span
                class="pun">.</span><span class="pln">random</span><span class="pun">()</span><span class="pln"><br></span><span
                class="pun">}</span></a></pre>
        <h2><a name="Mock_constructor_call">Mock constructor call</a></h2>

        <p><a name="Mock_constructor_call">Constructor call are mocked using the following syntax: </a></p>
        <pre class="prettyprint"><a name="Mock_constructor_call"><span class="kwd">def</span><span class="pln"> mockFile </span><span
                class="pun">=</span><span class="pln"> mock</span><span class="pun">(</span><span
                class="typ">File</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">constructor</span><span
                class="pun">(</span><span class="str">"/a/path/file.txt"</span><span class="pun">))</span></a></pre>
        <p><a name="Mock_constructor_call">This would match: <tt>new File("/a/path/file.txt")</tt>. The mockFile can
            then be used to setup further expectation. </a></p>

        <p><a name="Mock_constructor_call">Here is the full picture: </a></p>
        <pre class="prettyprint"><a name="Mock_constructor_call"><span class="kwd">def</span><span class="pln"> mockFile </span><span
                class="pun">=</span><span class="pln"> mock</span><span class="pun">(</span><span
                class="typ">File</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">constructor</span><span
                class="pun">(</span><span class="str">"/a/path/file.txt"</span><span class="pun">))</span><span
                class="pln"><br>mockFile</span><span class="pun">.</span><span class="pln">getName</span><span
                class="pun">().</span><span class="pln">returns</span><span class="pun">(</span><span class="str">"file.txt"</span><span
                class="pun">)</span><span class="pln"><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; </span><span class="kwd">def</span><span class="pln"> file </span><span
                class="pun">=</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span
                class="typ">File</span><span class="pun">(</span><span class="str">"/a/path/file.txt"</span><span
                class="pun">)</span><span class="pln"><br>&nbsp; assertEquals </span><span
                class="str">"file.txt"</span><span class="pun">,</span><span class="pln"> file</span><span
                class="pun">.</span><span class="pln">getName</span><span class="pun">()</span><span
                class="pln"><br></span><span class="pun">}</span></a></pre>
        <p><a name="Mock_constructor_call">You can expect an exception to be raised when a constructor call is
            matched: </a></p>
        <pre class="prettyprint"><a name="Mock_constructor_call"><span class="kwd">def</span><span class="pln"> mockFile </span><span
                class="pun">=</span><span class="pln"> mock</span><span class="pun">(</span><span
                class="typ">File</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">constructor</span><span
                class="pun">(</span><span class="str">"/a/path/file.txt"</span><span class="pun">).</span><span
                class="pln">raises</span><span class="pun">(</span><span class="typ">RuntimeException</span><span
                class="pun">))</span><span class="pln"><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; shouldFail</span><span class="pun">(</span><span
                class="typ">RuntimeException</span><span class="pun">)</span><span class="pln"> </span><span
                class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; </span><span class="kwd">new</span><span
                class="pln"> </span><span class="typ">File</span><span class="pun">(</span><span class="str">"/a/path/file.txt"</span><span
                class="pun">)</span><span class="pln"><br>&nbsp; </span><span class="pun">}</span><span class="pln"><br></span><span
                class="pun">}</span></a></pre>
        <h2><a name="Strong_typing">Strong typing</a></h2>

        <p><a name="Strong_typing">Gmock support out of the box java strong typing. You don't need to import any
            library we've worry about that for you. Mock objects can be used in a pure Java project which make Gmock
            a powerful alternative for Java testing. </a></p>
        <pre class="prettyprint"><a name="Strong_typing"><span class="typ">File</span><span
                class="pln"> mockFile </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">(</span><span class="typ">File</span><span class="pun">)</span></a></pre>
        <p><a name="Strong_typing">Strong typing works well with constructor call expectation: </a></p>
        <pre class="prettyprint"><a name="Strong_typing"><span class="typ">File</span><span
                class="pln"> mockFile </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">(</span><span class="typ">File</span><span class="pun">,</span><span
                class="pln"> </span><span class="kwd">constructor</span><span class="pun">(</span><span class="str">"/a/path/file.txt"</span><span
                class="pun">))</span><span class="pln"><br>mockFile</span><span class="pun">.</span><span
                class="pln">getName</span><span class="pun">().</span><span class="pln">returns</span><span
                class="pun">(</span><span class="str">"file.txt"</span><span class="pun">)</span><span
                class="pln"><br>play </span><span class="pun">{</span><span class="pln"><br>&nbsp; </span><span
                class="kwd">def</span><span class="pln"> file </span><span class="pun">=</span><span
                class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span
                class="typ">File</span><span class="pun">(</span><span class="str">"/a/path/file.txt"</span><span
                class="pun">)</span><span class="pln"><br>&nbsp; assertEquals </span><span
                class="str">"file.txt"</span><span class="pun">,</span><span class="pln"> file</span><span
                class="pun">.</span><span class="pln">getName</span><span class="pun">()</span><span
                class="pln"><br></span><span class="pun">}</span></a></pre>
        <p><a name="Strong_typing">Sometime you need to call the original constructor when mocking an object. Use
            the <tt>invokeConstructor</tt> for that purpose: </a></p>
        <pre class="prettyprint"><a name="Strong_typing"><span class="typ">JavaLoader</span><span
                class="pln"> mock </span><span class="pun">=</span><span class="pln"> mock</span><span
                class="pun">(</span><span class="typ">JavaLoader</span><span class="pun">,</span><span class="pln"> invokeConstructor</span><span
                class="pun">(</span><span class="str">"loader"</span><span class="pun">),</span><span
                class="pln"> </span><span class="kwd">constructor</span><span class="pun">(</span><span class="str">"name"</span><span
                class="pun">))</span></a></pre>
        <p><a name="Strong_typing">This would create a <tt>JavaLoader</tt> using its constructor with "loader"
            during the process of creation of the mock. </a></p>

        <h2><a name="Time_matching">Time matching</a></h2>

        <p><a name="Time_matching">Gmock let you specify how many times an expectation can be call. Like here: </a>
        </p>
        <pre class="prettyprint"><a name="Time_matching"><span class="pln">mockLoader</span><span
                class="pun">.</span><span class="pln">load</span><span class="pun">(</span><span
                class="lit">2</span><span class="pun">).</span><span class="pln">returns</span><span
                class="pun">(</span><span class="lit">3</span><span class="pun">).</span><span class="pln">atLeastOnce</span><span
                class="pun">()</span><span class="pln"><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; &nbsp; assertEquals </span><span class="lit">3</span><span
                class="pun">,</span><span class="pln"> mockLoader</span><span class="pun">.</span><span class="pln">load</span><span
                class="pun">(</span><span class="lit">2</span><span class="pun">)</span><span class="pln"><br>&nbsp; &nbsp; assertEquals </span><span
                class="lit">3</span><span class="pun">,</span><span class="pln"> mockLoader</span><span class="pun">.</span><span
                class="pln">load</span><span class="pun">(</span><span class="lit">2</span><span
                class="pun">)</span><span class="pln"><br></span><span class="pun">}</span></a></pre>
        <p><a name="Time_matching">The supported time matcher are: </a></p>
        <ul>
            <li><a name="Time_matching"><strong>never()</strong> the expectation should never be called </a></li>
            <li><a name="Time_matching"><strong>once()</strong> one time expectation (this is the implicit default)
            </a></li>
            <li><a name="Time_matching"><strong>atLeastOnce()</strong> one time or more </a></li>
            <li><a name="Time_matching"><strong>atMostOnce()</strong> zero or one time expectation </a></li>
            <li><a name="Time_matching"><strong>stub()</strong> the expectation can be call anytime </a></li>
            <li><a name="Time_matching"><strong>times(3)</strong> the expectation need to be call n times </a></li>
            <li><a name="Time_matching"><strong>times(2..4)</strong> the expectation need to be call n times within
                the range </a></li>
            <li><a name="Time_matching"><strong>atLeast(4)</strong> the expectation need to be call at least n time
            </a></li>
            <li><a name="Time_matching"><strong>atMost(4)</strong> the expectation need to be call at most n time
            </a></li>
        </ul>
        <h2><a name="Using_matcher">Using matcher</a></h2>

        <p><a name="Using_matcher">You can setup customised matcher in your expectation using the <tt>match</tt>
            syntax. The argument get passed to match closure and you should return true if it match. </a></p>

        <p><a name="Using_matcher">Here is the idea: </a></p>
        <pre class="prettyprint"><a name="Using_matcher"><span class="pln">mockLoader</span><span
                class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span
                class="str">"test"</span><span class="pun">,</span><span class="pln"> match </span><span
                class="pun">{</span><span class="pln"> it </span><span class="pun">&gt;</span><span
                class="pln"> </span><span class="lit">5</span><span class="pln"> </span><span class="pun">}).</span><span
                class="pln">returns</span><span class="pun">(</span><span class="str">"correct"</span><span
                class="pun">)</span><span class="pln"><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; assertEquals </span><span class="str">"correct"</span><span
                class="pun">,</span><span class="pln"> mockLoader</span><span class="pun">.</span><span class="pln">put</span><span
                class="pun">(</span><span class="str">"test"</span><span class="pun">,</span><span
                class="pln"> </span><span class="lit">10</span><span class="pun">)</span><span
                class="pln"><br></span><span class="pun">}</span></a></pre>
        <p><a name="Using_matcher">Gmock is also fully compatible with </a><a
                href="http://code.google.com/p/hamcrest/" rel="nofollow">Hamcrest</a> matcher. You will have to
            include the optional Hamcrest library in your classpath </p>

        <p>Here is an example: </p>
        <pre class="prettyprint"><span class="pln">mockLoader</span><span class="pun">.</span><span
                class="pln">put</span><span class="pun">(</span><span class="str">"test"</span><span
                class="pun">,</span><span class="pln"> </span><span class="kwd">is</span><span
                class="pun">(</span><span class="kwd">not</span><span class="pun">(</span><span
                class="pln">lessThan</span><span class="pun">(</span><span class="lit">5</span><span class="pun">)))).</span><span
                class="pln">returns</span><span class="pun">(</span><span class="str">"correct"</span><span
                class="pun">)</span><span class="pln"><br><br>play </span><span class="pun">{</span><span
                class="pln"><br>&nbsp; &nbsp; assertEquals </span><span class="str">"correct"</span><span
                class="pun">,</span><span class="pln"> mockLoader</span><span class="pun">.</span><span class="pln">put</span><span
                class="pun">(</span><span class="str">"test"</span><span class="pun">,</span><span
                class="pln"> </span><span class="lit">10</span><span class="pun">)</span><span
                class="pln"><br></span><span class="pun">}</span></pre>
        <h2><a name="Not_extending_the_GMockTestCase">Not extending the GMockTestCase</a></h2>

        <p><a name="Not_extending_the_GMockTestCase">If you don't want or can't extends the <tt>GMockTestCase</tt>
            in your test you can use the GMockController. </a></p>

        <p><a name="Not_extending_the_GMockTestCase">In the beginning of your test create a new
            <tt>GMockController</tt>. You then can use its <tt>mock()</tt> method an <tt>play</tt> closure as a
            usual Gmock test. </a></p>
        <pre class="prettyprint"><a name="Not_extending_the_GMockTestCase"><span class="kwd">void</span><span
                class="pln"> testController</span><span class="pun">(){</span><span
                class="pln"><br>&nbsp; </span><span class="kwd">def</span><span class="pln"> gmc </span><span
                class="pun">=</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span
                class="typ">GMockController</span><span class="pun">()</span><span
                class="pln"><br>&nbsp; </span><span class="kwd">def</span><span class="pln"> mockLoader </span><span
                class="pun">=</span><span class="pln"> gmc</span><span class="pun">.</span><span
                class="pln">mock</span><span class="pun">()</span><span
                class="pln"><br>&nbsp; mockLoader</span><span class="pun">.</span><span class="pln">load</span><span
                class="pun">(</span><span class="str">'key'</span><span class="pun">).</span><span class="pln">returns</span><span
                class="pun">(</span><span class="str">'value'</span><span class="pun">)</span><span class="pln"><br>&nbsp; gmc</span><span
                class="pun">.</span><span class="pln">play </span><span class="pun">{</span><span class="pln"><br>&nbsp; &nbsp; assertEquals </span><span
                class="str">"value"</span><span class="pun">,</span><span class="pln"> mockLoader</span><span
                class="pun">.</span><span class="pln">load</span><span class="pun">(</span><span
                class="str">'key'</span><span class="pun">)</span><span class="pln"><br>&nbsp; </span><span
                class="pun">}</span><span class="pln"><br></span><span class="pun">}</span></a></pre>

        <a name="Not_extending_the_GMockTestCase"> </a>

    </div>

    
</div>

</body>
</html>