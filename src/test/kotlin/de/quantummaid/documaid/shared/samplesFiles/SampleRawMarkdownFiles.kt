package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder

fun aRawMarkdownFile(fileName: String, content: String): ProcessedFile {
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, content)
        .withProcessedNameAndContent(fileName, content)
        .withProcessedNameAndContentInHugoFormat(fileName, content)
        .build()
}
