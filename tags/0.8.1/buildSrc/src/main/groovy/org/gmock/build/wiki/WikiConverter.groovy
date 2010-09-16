package org.gmock.build.wiki

import static java.util.regex.Pattern.*

class WikiConverter {

    def metaDatas = [:]

    String convert(String wiki, List formats, Closure plainConverter) {
        def result = new StringBuilder()
        def start = 0

        def regex = formats.collect { "(?:$it.regex)" }.join('|')
        def pattern = compile(regex, MULTILINE)
        def matcher = pattern.matcher(wiki)

        while (matcher.find(start)) {
            for (i in 1..matcher.groupCount()) {
                def content = matcher.group(i)
                if (content != null) {
                    result << plainConverter(wiki.substring(start, matcher.start()))
                    start = matcher.end()

                    def format = formats[i - 1]
                    def converter = format.converter
                    content = converter."${converter instanceof Closure ? 'call' : 'convert'}"(content)

                    result << format.handler(content)
                    break
                }
            }
        }

        result << plainConverter(wiki.substring(start, wiki.size()))
        result.toString()
    }

    def addMetaData(String key, String value) {
        metaDatas.put(key, value)
    }

}
