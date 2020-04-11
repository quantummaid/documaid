package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.NotProcessedSourceFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileBuilder

class SampleXmlFileWithOneSnippet private constructor(val fileName: String,
                                                      val snippet: String,
                                                      javaFileBuilder: PhysicalFileBuilder)
    : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aXmlFileWithOneSnippet(fileName: String, snippetId: String): SampleXmlFileWithOneSnippet {
            val snippet = "<configuration>\n" +
                "    <propA>A</propA>\n" +
                "    <propB>\n" +
                "        <internal>internal</internal>\n" +
                "    </propB>\n" +
                "</configuration>\n"
            val content = "<!-- Showcase start $snippetId -->\n" +
                snippet +
                "\n<!-- Showcase end $snippetId -->\n\n"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleXmlFileWithOneSnippet(fileName, snippet, fileBuilder)
        }
    }
}
