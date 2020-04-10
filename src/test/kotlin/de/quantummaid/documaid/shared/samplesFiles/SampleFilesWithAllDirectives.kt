package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.ProcessedFile
import de.quantummaid.documaid.shared.ProcessedFileBuilder

fun aMarkdownFileWithAllDirectives(fileName: String,
                                   tocPath: String, toc: String,
                                   linkPath: String, linkName: String,
                                   snippetName: String, snippet: String): ProcessedFile {
    val groupId = "local"
    val artifactId = "art"
    val version = "1.0.0"
    val goal = "generate"
    val phase = "verify"
    val scope = "test"
    val contentInput = contentInput(groupId, artifactId, version, goal, phase, scope,
        tocPath, linkPath, linkName, snippetName, snippet)
    val expectedContentOutput = expectedContent(groupId, artifactId, version, goal, phase, scope,
        tocPath, toc, linkPath, linkName, snippetName, snippet)

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithAllDirectivesAlreadyGenerated(fileName: String,
                                   tocPath: String, toc: String,
                                   linkPath: String, linkName: String,
                                   snippetName: String, snippet: String): ProcessedFile {
    val groupId = "local"
    val artifactId = "art"
    val version = "1.0.0"
    val goal = "generate"
    val phase = "verify"
    val scope = "test"
    val expectedContentOutput = expectedContent(groupId, artifactId, version, goal, phase, scope,
        tocPath, toc, linkPath, linkName, snippetName, snippet)

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

private fun contentInput(groupId: String, artifactId: String, version: String, goal: String, phase: String, scope: String,
                         tocPath: String, linkPath: String, linkName: String, snippetName: String, snippet: String): String {
    val pluginDirective = pluginDirective(groupId, artifactId, version, goal, phase)
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope)

    val contentInput = "Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "\n" +
        "and a little bit more text\n" +
        "<!---[Link] ( $linkPath $linkName)-->" +
        "----" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```java\n" +
        snippet + "\n" +
        "```\n" +
        "----" +
        pluginDirective +
        "----\n" +
        dependencyDirective +
        "----"
    return contentInput
}

private fun expectedContent(groupId: String, artifactId: String, version: String, goal: String, phase: String, scope: String,
                            tocPath: String, toc: String, linkPath: String, linkName: String, snippetName: String, snippet: String): String {
    val pluginMarkdown = createMarkdownPlugin(groupId, artifactId, version, goal, phase)
    val dependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope)
    val pluginDirective = pluginDirective(groupId, artifactId, version, goal, phase)
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope)
    val expectedContentOutput = "Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        toc +
        "\n<!---EndOfToc-->\n" +
        "\n" +
        "and a little bit more text\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)" +
        "----" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```java\n" +
        snippet + "\n" +
        "```\n" +
        "----" +
        pluginDirective +
        pluginMarkdown +
        "----\n" +
        dependencyDirective +
        dependencyMarkdown +
        "----"
    return expectedContentOutput
}
