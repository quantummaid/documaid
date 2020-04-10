package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.ProcessedFile
import de.quantummaid.documaid.shared.ProcessedFileBuilder

fun aMarkdownFileWithALinkDirective(fileName: String, linkPath: String, linkName: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->" +
        "someOtherText"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithALinkDirectiveAtTheEndOfFileWithoutNewLine(fileName: String, linkPath: String, linkName: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!--- [Link]($linkPath  $linkName ) -->"

    val expectedContentOutput = "#Test File\n" +
        "<!--- [Link]($linkPath  $linkName ) -->\n" +
        "[$linkName]($linkPath)"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithTwoLinkDirectives(fileName: String, linkPath1: String, linkName1: String, linkPath2: String, linkName2: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n"
    //TODO: text hinter link
    val expectedContentOutput = "#Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "[$linkName1]($linkPath1)\n" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n" +
        "[$linkName2]($linkPath2)\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithWrongLinkInserted(fileName: String, linkPath: String, linkName: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[somethingDifferent](./someDifferentFile)\n" +
        "someOtherText"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}


fun aMarkdownFileWithWrongLinkInsertedAtEndOfFileWithoutNewline(fileName: String, linkPath: String, linkName: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[somethingDifferent](./someDifferentFile)"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithTwoAlreadyGeneratedLinks(fileName: String, linkPath1: String, linkName1: String, linkPath2: String, linkName2: String): ProcessedFile {
    val expectedContentOutput = "#Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "[$linkName1]($linkPath1)\n" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n" +
        "[$linkName2]($linkPath2)\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}
fun aMarkdownFileWithWrongLinkInsertedAndAMissingLink(fileName: String, linkPath1: String, linkName1: String, linkPath2: String, linkName2: String): ProcessedFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( $linkPath1 $linkName1)-->" +
        "[somethingDifferent](./someDifferentFile)" +
        "someOtherText"+
        "<!---[Link] ( $linkPath2 $linkName2)-->\n"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( $linkPath1 $linkName1)-->\n" +
        "[$linkName1]($linkPath1)" +
        "someOtherText"+
        "<!---[Link] ( $linkPath2 $linkName2)-->\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}