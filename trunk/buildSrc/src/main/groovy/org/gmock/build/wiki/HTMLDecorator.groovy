package org.gmock.build.wiki

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml

class HTMLDecorator {

    def plain = { String content ->
        escapeHtml(content)
    }

    def head = { i, content ->
        "<h$i><a name=\"${replaceSpaces(content)}\">${content.trim()}</a></h$i>"
    }

    def listBlock = { content ->
        "<ul>$content</ul>"
    }

    def listItem = { content ->
        "<li>${content.trim()}</li>"
    }

    def bold = { content ->
        "<strong>$content</strong>"
    }

    def codeBlock = { content ->
        "<pre class=\"prettyprint\">${content.trim()}</pre>"
    }

    def inlineCode = { content ->
        "<tt>${content.trim()}</tt>"
    }

    def paragraph = { content ->
        "<p>${content.trim()}</p>"
    }

    def urlLink = { url, text = url ->
        "<a rel=\"nofollow\" href=\"$url\">${text.trim()}</a>"
    }

    private codeWithClass = { className, code ->
        "<span class=\"$className\">$code</span>"
    }

    def code = [
        comment: codeWithClass.curry('com'),
        string: codeWithClass.curry('str'),
        keyword: codeWithClass.curry('kwd'),
        type: codeWithClass.curry('typ'),
        literal: codeWithClass.curry('lit'),
        annotation: codeWithClass.curry('atn'),
        punctuation: codeWithClass.curry('pun'),
        plain: codeWithClass.curry('pln')
    ]

    def catalog = [
        list: { "<dl>$it</dl>" },
        subList: { "<dd>$it</dd>" },
        listItem: { "<dt><a href=\"#${replaceSpaces(it)}\">${it.trim()}</a></dt>" }
    ]

    private static replaceSpaces(String text) {
        text.trim().replaceAll(/\s/, '_')
    }

}
