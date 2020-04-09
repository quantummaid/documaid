package de.quantummaid.documaid.shared

class SampleXmlFileWithOneSnippet(javaFileBuilder: PhysicalFileBuilder, val snippet: String) : NotProcessedSourceFile(javaFileBuilder) {
    companion object {
        fun aXmlFileWithOneSnippet(fileName: String, snippetId: String): SampleXmlFileWithOneSnippet {
            val snippet =   "<configuration>\n" +
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
            return SampleXmlFileWithOneSnippet(fileBuilder, snippet)
        }
    }
}
