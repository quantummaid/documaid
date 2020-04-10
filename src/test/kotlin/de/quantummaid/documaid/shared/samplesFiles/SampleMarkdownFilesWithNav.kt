package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.ProcessedFile
import de.quantummaid.documaid.shared.ProcessedFileBuilder
import de.quantummaid.documaid.usecases.tableOfContents.S

fun aMarkdownFileWithNav(fileName: String, navigationString: String): ProcessedFile {
    val contentInput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        navigationString+"\n"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithAlreadyGeneratedNav(fileName: String, navigationString: String): ProcessedFile {
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        navigationString+"\n"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithAWrongNav(fileName: String, navigationString: String): ProcessedFile {
    val contentInput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        "[&larr;](1_Wrong1.md)$S[Overview](../README.md)$S[&rarr;](3_Wrong2.md)\n"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        navigationString+"\n"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithNavAtEndOfLineWithoutNewLine(fileName: String, navigationString: String): ProcessedFile {
    val contentInput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        navigationString

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}
fun aMarkdownFileWithAWrongNavAtEndOfFile(fileName: String, navigationString: String): ProcessedFile {
    val contentInput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        "[&larr;](1_Wrong1.md)$S[Overview](../README.md)$S[&rarr;](3_Wrong2.md)"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"+
        navigationString+""

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}