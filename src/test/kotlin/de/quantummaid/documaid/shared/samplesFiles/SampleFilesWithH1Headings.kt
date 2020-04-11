package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder

fun aMarkdownFileWithH1Heading(fileName: String, weight:String)
    : ProcessedFile {

    val contentInput = "# This is my Heading\n" +
        "someOtherText"
    val expectedContentOutput = "---\n" +
        "title: \"This is my Heading\"\n" +
        "weight: $weight\n" +
        "---\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithADifferentH1Heading(fileName: String, weight:String)
    : ProcessedFile {

    val contentInput = "\n\n" +
        "#This is my Heading \n"
    val expectedContentOutput = "\n\n---\n" +
        "title: \"This is my Heading \"\n" +
        "weight: $weight\n" +
        "---\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithH2Heading(fileName: String)
    : ProcessedFile {

    val contentInput = "## This is my Heading\n" +
        "someOtherText"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithTextBeforeHeading(fileName: String)
    : ProcessedFile {

    val contentInput = " SomeText" +
        "# This is my Heading\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithExistingH1Heading(fileName: String, weight:String)
    : ProcessedFile {

    val expectedContentOutput = "---\n" +
        "title: \"This is my Heading\"\n" +
        "weight: $weight\n" +
        "---\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithWrongH1Heading(fileName: String)
    : ProcessedFile {

    val contentInput = "---\n" +
        "title: \"This is my Heading123\"\n" +
        "weight: $123\n" +
        "---\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, "")
        .build()
}