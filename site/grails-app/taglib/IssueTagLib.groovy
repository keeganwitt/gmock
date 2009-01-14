

class IssueTagLib {

    def issue = { attrs, body ->
        def cssClass = attrs.done ? "class='done'" : ""
        out << "<a href='http://code.google.com/p/gmock/issues/detail?id=${attrs.number}' $cssClass>(issue ${attrs.number})</a>"
    }


}