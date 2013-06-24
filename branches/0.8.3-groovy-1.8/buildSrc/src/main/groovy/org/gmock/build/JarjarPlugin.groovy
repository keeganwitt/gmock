package org.gmock.build

import org.gradle.api.*
import org.gradle.api.tasks.bundling.Jar

class JarjarPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.configurations {
            jarjar
        }
        project.dependencies {
            jarjar project.files('lib/jarjar-1.0.jar')
        }
        project.tasks.withType(Jar) { jar ->
            jar.ext.jarjarify = false
            jar.ext.jarjarifyConfigurations = []
            jar.ext.jarjarifyRules = []
            jar.ext.jarjarifyRule = { Map rule -> jar.jarjarifyRules << rule }
            jar << {
                if (jarjarify) {
                    def tmpArchivePath = project.file("${archivePath}.tmp")
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
