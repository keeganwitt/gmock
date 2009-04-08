package org.gmock.build.wiki

class GoogleCodeWikiConverter {

    def converter
    def decorator
    def codeConverter

    GoogleCodeWikiConverter(converter = new WikiConverter(), decorator = new HTMLDecorator(), codeConverter = new GroovyCodeConverter()) {
        this.converter = converter
        this.decorator = decorator
        this.codeConverter = codeConverter
    }

    String convert(String wiki) {
        converter.convert(wiki, formats, decorator.plain)
    }

    def getFormats() {
        blockFormats + inlineFormats
    }

    def getBlockFormats() {
        [
            // meta data
            [regex: /^#((?:\S+) (?:.+))$\n?/, handler: metaDataHandler, converter: noChangeConverter],
            // head
            *((1..6).collect { [regex: "^={$it}([^=]+)={$it}", handler: decorator.head.curry(it), converter: plainConverter] }),
            // list
            [regex: /((?:^ +\*.*$\n?)+)/, handler: decorator.listBlock, converter: listConverter],
            // code block
            [regex: /\{\{\{(?s)(.*?)(?-s)\}\}\}/, handler: decorator.codeBlock, converter: codeConverter],
            // paragraph
            [regex: /^(.+)$/, handler: decorator.paragraph, converter: inlineConverter]
        ]
    }

    def getInlineFormats() {
        [
            // inline code
            [regex: /`(.*?)`/, handler: decorator.inlineCode, converter: plainConverter],
            // bold
            [regex: /\*(.*?)\*/, handler: decorator.bold, converter: inlineConverter],
            // url
            [regex: /\[((?:\S+) (?:.*?))\]/, handler: urlLinkHandler, converter: noChangeConverter]
        ]
    }

    def noChangeConverter = { it }

    def plainConverter = { decorator.plain(it) }

    def inlineConverter = { converter.convert(it, inlineFormats, decorator.plain) }

    def listConverter = { content ->
        def result = new StringBuilder()
        def matcher = (content =~ /(?m)^ +\*(.*)$/)
        while (matcher.find()) {
            def item = converter.convert(matcher.group(1), inlineFormats, decorator.plain)
            result << decorator.listItem(item)
        }
        result.toString()
    }

    def metaDataHandler = { content ->
        def matcher = content =~ /^(\S+) (.+)$/
        converter.addMetaData(matcher[0][1], matcher[0][2])
        return ''
    }

    def urlLinkHandler = { content ->
        def matcher = content =~ /(\S+) (.*)/
        decorator.urlLink(matcher[0][1], matcher[0][2])
    }

}
