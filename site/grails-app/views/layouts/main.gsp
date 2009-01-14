<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title><g:layoutTitle default="Gmock" /></title>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'screen.css')}" media="screen, projection"/>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'print.css')}" media="print"/>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'main.css')}" media="screen, projection"/>
    <!--[if IE]><link rel="stylesheet" href="${createLinkTo(dir:'css',file:'ie.css')}" media="screen, projection"/><![endif]-->
</head>

<body>
<div class="header">
    <div class="container">
        <img src="/images/logo.jpg" alt="Gmock"/>
        <h1>Gmock</h1>

        <div id="play">play { ... }</div>
    </div>
</div>
<div class="menu">
    <div class="container">
        <ul>
            <li><g:link controller="home">Home</g:link></li>
            <li><g:link controller="documentation" action="${currentVersion()}">Documentation</g:link></li>
            <li><g:link controller="download">Download</g:link></li>
            <li><g:link controller="roadmap">Roadmap</g:link></li>
        </ul>
    </div>
</div>

<div class="page">
        <g:layoutBody/>
</div>
</body>

</html>
