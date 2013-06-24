package org.gmock.build

import groovy.text.SimpleTemplateEngine

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gmock.build.wiki.GoogleCodeWikiConverter

class WikiPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.task('wiki', type: Download)
        project.task('docs', type: WikiDocs, dependsOn: project.tasks.wiki) {
            group = 'documentation'
        }
    }

}

class Download extends DefaultTask {

    String url

    @OutputDirectory
    File dir

    @OutputFile
    File file

    @TaskAction
    def download() {
        ant.get src: url, dest: file, usetimestamp: true
    }

}

class WikiDocs extends DefaultTask {

    @InputFile
    File wikiFile

    @InputFile
    File templateFile

    @InputDirectory
    File templateDir

    @OutputFile
    File docsFile

    @OutputDirectory
    File docsDir

    @TaskAction
    def generate() {
        project.copy {
            from templateDir, {
                exclude '**/*template*'
            }
            into docsDir
        }
        def wikiText = wikiFile.text
        def converter = new GoogleCodeWikiConverter()
        def binding = [content: converter.convert(wikiText), catalog: converter.catalog, version: project.version]
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(templateFile).make(binding)
        docsFile.withPrintWriter { template.writeTo(it) }
    }

}
