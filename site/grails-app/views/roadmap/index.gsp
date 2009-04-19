<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Gmock: Roadmap</title>
    <meta name="layout" content="main"/>
</head>


<body>

<div class="container">

    <div class="span-24">
        <h1>Roadmap</h1>

        <h2>Version 1.0.0</h2>
        <ul>
            <li>Nice,Strict mock</li>
        </ul>

        <h2>Version 0.9.0</h2>
        <ul>
            <li>Closure mocking <g:issue number="54"/></li>
            <li>Support for chain method call <g:issue number="77"/></li>
            <li>Support for 'and' and 'or' method chaining <g:issue number="82"/></li>
        </ul>

        <h2 class="done here">Version 0.8.0</h2>
        <ul>
            <li>Support for Groovy 1.6.0 <g:issue number="74" done="true"/></li>
            <li>Ast transform @WithGmock <g:issue number="71" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.7.0</h2>
        <ul>
            <li>Strict ordering <g:issue number="50" done="true"/></li>
            <li>Mock out single method on concrete object <g:issue number="52" done="true"/></li>
            <li>Regex method matching <g:issue number="70" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.6.0</h2>
        <ul>
            <li>Static property mocking <g:issue number="23" done="true"/></li>
            <li>Setup expectation in mock closure <g:issue number="35" done="true"/></li>
        </ul>

        <h2  class="done">Version 0.5.0</h2>
        <ul>
            <li>Strong typing support <g:issue number="14" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.4.0</h2>
        <ul>
            <li>Closure matcher <g:issue number="12" done="true"/></li>
            <li>Time matching <g:issue number="13" done="true"/></li>
            <li>Hamcrest style of matcher <g:issue number="20" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.3.0</h2>
        <ul>
            <li>Property mocking <g:issue number="7" done="true"/></li>
            <li>Static method mocking <g:issue number="8" done="true"/></li>
            <li>Improvement of the raise method <g:issue number="10" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.2.0</h2>
        <ul>
            <li>Rename raises and returns <g:issue number="3" done="true"/></li>
            <li>Refactor constructor mocking <g:issue number="6" done="true"/></li>
            <li>Introduce GMockController <g:issue number="5" done="true"/></li>
            <li>Refactor stub return <g:issue number="11" done="true"/></li>
        </ul>

        <h2 class="done">Version 0.1.0</h2>
        <ul>
            <li>Basic mocking functionnality</li>
        </ul>


    </div>




</div>

</body>
</html>