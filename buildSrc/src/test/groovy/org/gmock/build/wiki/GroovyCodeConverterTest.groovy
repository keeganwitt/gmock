package org.gmock.build.wiki

class GroovyCodeConverterTest extends GroovyTestCase {

    GroovyCodeConverter gcc

    void setUp() {
        gcc = new GroovyCodeConverter()
    }

    void testConvertComments() {
        assertEquals '''<span class="kwd">class</span> <span class="com">// &quot;abc&quot;</span>
 <span class="kwd">def</span> <span class="com">/* def
*/</span>''',
                     gcc.convert('class // "abc"\n def /* def\n*/')
    }

    void testConvertString() {
        assertEquals '<span class="str">\'abc\'</span> <span class="str">&quot;def&quot;</span> <span class="str">/ghi/</span>',
                     gcc.convert('\'abc\' "def" /ghi/')
        assertEquals '<span class="str">\'\'</span> <span class="str">&quot;&quot;</span>',
                     gcc.convert('\'\' ""')
        assertEquals "<span class=\"str\">'ab\\'c'</span>", gcc.convert("'ab\\'c'")
        assertEquals '<span class="str">&quot;ab\\&quot;c&quot;</span>', gcc.convert('"ab\\"c"')
        assertEquals '<span class="str">/ab\\/c/</span>', gcc.convert('/ab\\/c/')
    }

    void testConvertKeywords() {
        assertEquals '<span class="kwd">def</span> <span class="kwd">class</span> <span class="kwd">import</span> <span class="pln">define</span>',
                     gcc.convert('def class import define')
    }

    void testConvertType() {
        assertEquals '<span class="kwd">class</span> <span class="typ">ClassA</span>', gcc.convert('class ClassA')
    }

    void testConvertLiteral() {
        assertEquals '<span class="lit">1</span> <span class="lit">1.2</span> <span class="lit">1.</span> <span class="lit">.2</span>',
                     gcc.convert('1 1.2 1. .2')
    }

    void testConvertAnnotation() {
        assertEquals '<span class="atn">@WithGMock</span>', gcc.convert('@WithGMock')
    }

    void testConvertPunctuation() {
        assertEquals '<span class="pun">{}</span> <span class="pun">()</span> <span class="pun">.</span> <span class="pun">+</span>',
                     gcc.convert('{} () . +')
    }

    void testConvertPlain() {
        assertEquals '<span class="kwd">def</span> <span class="pln">loader</span>', gcc.convert('def loader')
    }

    void testConvertCode() {
        def code = '''\
def loader = mock()
loader.put("throw exception").raises(new RuntimeException("an exception")) // or 'raises(RuntimeException, "an exception")'
play {
  def message = shouldFail(RuntimeException) {
    loader.put("throw exception")
  }
  assertEquals "an exception", message
}

def mock = mock()
mock./set.*/(1).returns(.2)
play {
  assertEquals .2, mock.setSomething(1)
}'''
        def expected = '''\
<span class="kwd">def</span> <span class="pln">loader</span> <span class="pun">=</span> <span class="pln">mock</span><span class="pun">()</span>
<span class="pln">loader</span><span class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span class="str">&quot;throw exception&quot;</span><span class="pun">)</span><span class="pun">.</span><span class="pln">raises</span><span class="pun">(</span><span class="kwd">new</span> <span class="typ">RuntimeException</span><span class="pun">(</span><span class="str">&quot;an exception&quot;</span><span class="pun">))</span> <span class="com">// or 'raises(RuntimeException, &quot;an exception&quot;)'</span>
<span class="pln">play</span> <span class="pun">{</span>
  <span class="kwd">def</span> <span class="pln">message</span> <span class="pun">=</span> <span class="pln">shouldFail</span><span class="pun">(</span><span class="typ">RuntimeException</span><span class="pun">)</span> <span class="pun">{</span>
    <span class="pln">loader</span><span class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span class="str">&quot;throw exception&quot;</span><span class="pun">)</span>
  <span class="pun">}</span>
  <span class="pln">assertEquals</span> <span class="str">&quot;an exception&quot;</span><span class="pun">,</span> <span class="pln">message</span>
<span class="pun">}</span>

<span class="kwd">def</span> <span class="pln">mock</span> <span class="pun">=</span> <span class="pln">mock</span><span class="pun">()</span>
<span class="pln">mock</span><span class="pun">.</span><span class="str">/set.*/</span><span class="pun">(</span><span class="lit">1</span><span class="pun">)</span><span class="pun">.</span><span class="pln">returns</span><span class="pun">(</span><span class="lit">.2</span><span class="pun">)</span>
<span class="pln">play</span> <span class="pun">{</span>
  <span class="pln">assertEquals</span> <span class="lit">.2</span><span class="pun">,</span> <span class="pln">mock</span><span class="pun">.</span><span class="pln">setSomething</span><span class="pun">(</span><span class="lit">1</span><span class="pun">)</span>
<span class="pun">}</span>'''
        assertEquals expected, gcc.convert(code)
    }

}
