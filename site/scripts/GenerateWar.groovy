import org.codehaus.groovy.grails.commons.GrailsApplication

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

includeTargets << new File ( "${grailsHome}/scripts/War.groovy" )

target ('default': "Run's a Grails application's WAR in Jetty") {
        depends( checkVersion, configureProxy, war )
}

