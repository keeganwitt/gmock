package org.gmock.build.wiki

class CatalogHolderTest extends GroovyTestCase {

    CatalogHolder ch = new CatalogHolder()

    void testAdd() {
        ch.add(1, "title 1")
        ch.add(2, "title 2")
        ch.add(3, "title 3")
        ch.add(1, "title 4")
        assertEquals([[level: 1, title: 'title 1'], [level: 2, title: 'title 2'], [level: 3, title: 'title 3'], [level: 1, title: 'title 4']],
                     ch.contents)
    }

    void testToString1() {
        ch.add(1, "title 1")
        ch.add(2, "title 2")
        ch.add(3, "title 3")
        ch.add(1, "title 4")
        def expected = '<dl><dt><a href="#title_1">title 1</a></dt><dd><dl><dt><a href="#title_2">title 2</a></dt><dd><dl><dt><a href="#title_3">title 3</a></dt></dl></dd></dl></dd><dt><a href="#title_4">title 4</a></dt></dl>'
        assertEquals expected, ch.toString()
    }

    void testToString2() {
        ch.add(1, "title 1")
        ch.add(2, "title 2")
        ch.add(3, "title 3")
        ch.add(2, "title 4")
        ch.add(3, "title 5")
        ch.add(3, "title 6")
        ch.add(1, "title 7")
        ch.add(2, "title 8")
        ch.add(2, "title 9")
        ch.add(1, "title 10")
        ch.add(3, "title 11")
        def expected = '<dl><dt><a href="#title_1">title 1</a></dt><dd><dl><dt><a href="#title_2">title 2</a></dt><dd><dl><dt><a href="#title_3">title 3</a></dt></dl></dd><dt><a href="#title_4">title 4</a></dt><dd><dl><dt><a href="#title_5">title 5</a></dt><dt><a href="#title_6">title 6</a></dt></dl></dd></dl></dd><dt><a href="#title_7">title 7</a></dt><dd><dl><dt><a href="#title_8">title 8</a></dt><dt><a href="#title_9">title 9</a></dt></dl></dd><dt><a href="#title_10">title 10</a></dt><dd><dl><dd><dl><dt><a href="#title_11">title 11</a></dt></dl></dd></dl></dd></dl>'
        assertEquals expected, ch.toString()
    }

}
