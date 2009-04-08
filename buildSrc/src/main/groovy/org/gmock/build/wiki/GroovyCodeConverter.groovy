package org.gmock.build.wiki

class GroovyCodeConverter {

    private static final GROOVY_KEYWORDS = ["as", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "def", "default", "double", "else", "enum", "extends", "false", "finally", "float", "for", "if", "implements", "import", "in", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "threadsafe", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"]
    private static final GROOVY_KEYWORDS_REGEX = GROOVY_KEYWORDS.collect { "\\b$it\\b" }.join('|')

    def converter
    def decorator

    GroovyCodeConverter(converter = new WikiConverter(), decorator = new HTMLDecorator()) {
        this.converter = converter
        this.decorator = decorator
    }

    String convert(String code) {
        converter.convert(code, formats, decorator.plain)
    }

    def getFormats() {
        [
            // comment
            [regex: /(\/\/.*$|(?s)\/\*.*?\*\/(?-s))/, handler: decorator.code.comment, converter: plainConverter],
            // string
            [regex: /(''|""|'.*?[^\\]'|".*?[^\\]"|\/.*?[^\\]\/)/, handler: decorator.code.string, converter: plainConverter],
            // keyword
            [regex: "($GROOVY_KEYWORDS_REGEX)", handler: decorator.code.keyword, converter: plainConverter],
            // type
            [regex: /(\b[A-Z]\w*\b)/, handler: decorator.code.type, converter: plainConverter],
            // literal
            [regex: /(\d+\.\d*|\d*\.\d+|\d+)/, handler: decorator.code.literal, converter: plainConverter],
            // punctuation
            [regex: /([^\w\s'"\/\.]+|\.)/, handler: decorator.code.punctuation, converter: plainConverter],
            // plain
            [regex: /(\w+)/, handler: decorator.code.plain, converter: plainConverter]
        ]
    }

    def plainConverter = { decorator.plain(it) }

}
