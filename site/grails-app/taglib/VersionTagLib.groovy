class VersionTagLib {

    def grailsApplication


    def currentVersion = { attrs ->
        out << grailsApplication.config.gmock.version
    }



}