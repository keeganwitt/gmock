package org.gmock.utils


class Echo {
    def output

    def echo(msgs) {
        output.print(msgs as String[])
    }
}