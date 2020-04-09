package de.quantummaid.documaid.shared

//TODO: move
fun aMarkdownFileWithSnippet(fileName: String, snippetId: String): PhysicalFileBuilder {
    val content = "#Test File\n" +
        "<!---[CodeSnippet] ("+snippetId+")-->\n" +
        "someOtherText\n" +
        "<!---[CodeSnippet] ("+snippetId+")-->\n" +
        "\n" +
        "adsadsad\n"
    return PhysicalFileBuilder.aFile(fileName)
        .withContent(content)
}