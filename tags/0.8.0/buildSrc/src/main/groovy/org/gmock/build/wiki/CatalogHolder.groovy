package org.gmock.build.wiki

class CatalogHolder {

    def contents = []
    def decorator

    CatalogHolder(decorator = new HTMLDecorator()) {
        this.decorator = decorator
    }

    def add(int level, String title) {
        contents << [level: level, title: title]
    }

    String convert(int level = 1, int start = 0, int end = contents.size()) {
        def result = new StringBuilder()
        for (def i = start; i < end; ++i) {
            if (contents[i].level == level) {
                result << decorator.catalog.listItem(contents[i].title)
            } else {
                def subStart = i, subEnd = i + 1
                while (subEnd < end && contents[subEnd].level != level) ++subEnd
                result << decorator.catalog.subList(convert(level + 1, subStart, subEnd))
                i = subEnd - 1
            }
        }
        decorator.catalog.list(result.toString())
    }

    String toString() {
        convert()
    }

}
