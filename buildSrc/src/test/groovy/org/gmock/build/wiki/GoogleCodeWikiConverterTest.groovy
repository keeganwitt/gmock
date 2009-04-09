package org.gmock.build.wiki

class GoogleCodeWikiConverterTest extends GroovyTestCase {

    GoogleCodeWikiConverter gcwc

    void setUp() {
        gcwc = new GoogleCodeWikiConverter()
    }

    void testConvertH1() {
        assertEquals '<h1><a name="head_1">head 1</a></h1>', gcwc.convert('= head 1 =')
    }

    void testConvertH1WithMultipleLines() {
        def wiki = '''\
= head 1 =
= head 2 =
'''
        def expected = '''\
<h1><a name="head_1">head 1</a></h1>
<h1><a name="head_2">head 2</a></h1>
'''
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertH2() {
        assertEquals '<h2><a name="head_2">head 2</a></h2>', gcwc.convert('== head 2 ==')
    }

    void testConvertH1AndH2WithMultipleLines() {
        def wiki = '''
= head 1 =
== head 2 ==
'''
        def expected = '''
<h1><a name="head_1">head 1</a></h1>
<h2><a name="head_2">head 2</a></h2>
'''
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertH1ToH6() {
        def wiki = (1..6).collect { "${'=' * it} head $it ${'=' * it}" }.join('\n')
        def expected = (1..6).collect { "<h$it><a name=\"head_$it\">head $it</a></h$it>" }.join('\n')
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertList() {
        def wiki = '''\
  * list 1
  * list 2
'''
        def expected = '<ul><li>list 1</li><li>list 2</li></ul>'
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertBold() {
        assertEquals '<p><strong>bold</strong></p>', gcwc.convert('*bold*')
    }

    void testConvertListWithBold() {
        def wiki = '''\
  * *bold 1* list 1
  * *bold 2* list 2
'''
        def expected = '<ul><li><strong>bold 1</strong> list 1</li><li><strong>bold 2</strong> list 2</li></ul>'
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertCodeBlock() {
        def wiki = '''\
{{{
abc
}}}'''
        def expected = '<pre class="prettyprint"><span class="pln">abc</span></pre>'
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertMultipleCodeBlocks() {
        def wiki = '''\
{{{
abc
}}}
{{{
ghi
}}}'''
        def expected = '''\
<pre class="prettyprint"><span class="pln">abc</span></pre>
<pre class="prettyprint"><span class="pln">ghi</span></pre>'''
        assertEquals expected, gcwc.convert(wiki)
    }

    void testConvertInlineCode() {
        assertEquals '<p><tt>def</tt></p>', gcwc.convert('` def `')
    }

    void testConvertMultipleInlineCodes() {
        assertEquals '<p><tt>abc</tt> <tt>def</tt></p>', gcwc.convert('`abc` `def`')
    }

    void testConvertMetaData() {
        def wiki = '#test true\n#debug false'
        assertEquals '', gcwc.convert(wiki)
        assertEquals([test: 'true', debug: 'false'], gcwc.converter.metaDatas)
    }

    void testConvertParagraph() {
        assertEquals '<p>test 1</p>\n\n<p>test2</p>\n<p>test3</p>', gcwc.convert(' test 1 \n\ntest2\ntest3')
    }

    void testConvertUrlLink() {
        assertEquals '<p><a rel="nofollow" href="http://code.google.com/p/hamcrest/">Hamcrest</a></p>',
                     gcwc.convert('[http://code.google.com/p/hamcrest/  Hamcrest]')
    }

    void testConvertCatalog() {
        def wiki = '''\
= head 1 =
== head 2 ==
=== head 3 ===
== head 4 ==
= head 5 =
'''
        def expected = '<dl><dt><a href="#head_1">head 1</a></dt><dd><dl><dt><a href="#head_2">head 2</a></dt><dd><dl><dt><a href="#head_3">head 3</a></dt></dl></dd><dt><a href="#head_4">head 4</a></dt></dl></dd><dt><a href="#head_5">head 5</a></dt></dl>'
        gcwc.convert(wiki)
        assertEquals expected, gcwc.catalog.toString()
    }

}
