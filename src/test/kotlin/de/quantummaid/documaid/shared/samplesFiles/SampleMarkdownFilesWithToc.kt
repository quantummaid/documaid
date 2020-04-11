package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder

fun aMarkdownFileWithToc(fileName: String, tocPath: String, toc: String): ProcessedFile {
    val contentInput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "\n" +
        "and a little bit more text\n"
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        toc +
        "\n<!---EndOfToc-->\n" +
        "\n" +
        "and a little bit more text\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithTocAlreadyGenerated(fileName: String, tocPath: String, toc: String): ProcessedFile {
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        toc +
        "\n<!---EndOfToc-->\n" +
        "\n" +
        "and a little bit more text\n"
    val expectedContentOutputHugo = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "\n" +
        "and a little bit more text\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}


fun aMarkdownFileWithAWrongToc(fileName: String, tocPath: String, expectedToc: String): ProcessedFile {
    val contentInput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "1. [Different](1_Different.md)"+
        "2. [EvenMoreDifferent](2_EventMoreDifferent.md)"+
        "3. [WouldNeverChooseThis](3_Different3.md)"+
        "\n<!---EndOfToc-->\n" +
        "\n" +
        "and a little bit more text\n"
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        expectedToc +
        "\n<!---EndOfToc-->\n" +
        "\n" +
        "and a little bit more text\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithTocAtTheEndOfFileWithoutNewLine(fileName: String, tocPath: String, toc: String): ProcessedFile {
    val contentInput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->"
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        toc +
        "\n<!---EndOfToc-->"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithAWrongTocAtEndOfFile(fileName: String, tocPath: String, expectedToc: String): ProcessedFile {
    val contentInput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "1. [Different](1_Different.md)"+
        "2. [EvenMoreDifferent](2_EventMoreDifferent.md)"+
        "3. [WouldNeverChooseThis](3_Different3.md)"+
        "\n<!---EndOfToc-->"
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        expectedToc +
        "\n<!---EndOfToc-->"
    val expectedContentOutputHugo = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}