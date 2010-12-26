package org.gmock.build

import org.gradle.api.*
import org.gradle.api.tasks.bundling.Jar

class JarjarPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.repositories {
            mavenRepo name: 'JbossRepo', urls: ['http://repository.jboss.org/maven2/']
        }
        project.configurations {
            jarjar
        }
        project.dependencies {
            jarjar 'com.google.code:jarjar:1.0'
        }
        project.tasks.withType(Jar).allTasks { jar ->
            jar.jarjarify = false
            jar.jarjarifyConfigurations = []
            jar.jarjarifyRules = []
            jar.jarjarifyRule = { Map rule -> jar.jarjarifyRules << rule }
            jar << {
                if (jarjarify) {
                    tmpArchivePath = project.file("${archivePath}.tmp")
                    project.ant {
                        move file: archivePath, tofile: tmpArchivePath
                        taskdef name: 'jarjar', classname: 'com.tonicsystems.jarjar.JarJarTask', classpath: project.configurations.jarjar.asPath
                        jarjar jarfile: archivePath, {
                            zipfileset src: tmpArchivePath
                            jarjarifyConfigurations*.each {
                                zipfileset src: it, {
                                    include name: '**/*.class'
                                }
                            }
                            jarjarifyRules.each {
                                rule it
                            }
                        }
                        delete file: tmpArchivePath
                    }
                }
            }
        }
    }

}
