/**
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder
import de.quantummaid.documaid.shared.samplesFiles.HUGO_LINK_TARGET.GITHUB
import de.quantummaid.documaid.shared.samplesFiles.HUGO_LINK_TARGET.WITHIN_DOCU

enum class HUGO_LINK_TARGET {
    GITHUB,
    WITHIN_DOCU
}

fun aMarkdownFileWithALinkDirective(
    fileName: String,
    linkPath: String,
    linkName: String,
    hugoLinkTarget: HUGO_LINK_TARGET = GITHUB
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->" +
        "someOtherText"
    val expectedContentOutput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)" +
        "someOtherText"
    val expectedContentOutputHugo: String = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName](${createLinkTarget(linkPath, hugoLinkTarget)})" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithALinkAtTheEndOfFileWithoutNewLine(
    fileName: String,
    linkPath: String,
    linkName: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!--- [Link]($linkPath  $linkName ) -->"

    val expectedContentOutput = "Test File\n" +
        "<!--- [Link]($linkPath  $linkName ) -->\n" +
        "[$linkName]($linkPath)"

    val expectedContentOutputHugo = "Test File\n" +
        "<!--- [Link]($linkPath  $linkName ) -->\n" +
        "[$linkName](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath)"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithTwoLinkDirectives(
    fileName: String,
    linkPath1: String,
    linkName1: String,
    linkPath2: String,
    linkName2: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n"
    val expectedContentOutput = "Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "[$linkName1]($linkPath1)" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n" +
        "[$linkName2]($linkPath2)"
    val expectedContentOutputHugo = "Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "[$linkName1](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath1)" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n" +
        "[$linkName2](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath2)"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithAnInlineLinkDirective(fileName: String, linkPath: String, linkName: String): ProcessedFile {
    val contentInput = "This is a sentence referencing<!---[Link] ( $linkPath $linkName)-->\n" +
        ", and then continuing on on a second line."
    val expectedContentOutput = "This is a sentence referencing<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath), and then continuing on on a second line."
    val expectedContentOutputHugo = "This is a sentence referencing<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath), " +
        "and then continuing on on a second line."
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithWrongLinkInserted(
    fileName: String,
    linkPath: String,
    linkName: String,
    hugoLinkTarget: HUGO_LINK_TARGET = GITHUB
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[somethingDifferent](./someDifferentFile)\n" +
        "someOtherText"
    val expectedContentOutput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)\n" +
        "someOtherText"
    val expectedContentOutputHugo = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName](${createLinkTarget(linkPath, hugoLinkTarget)})\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithWrongLinkInsertedInlineStyle(
    fileName: String,
    linkPath: String,
    linkName: String,
    hugoLinkTarget: HUGO_LINK_TARGET = GITHUB
): ProcessedFile {
    val contentInput = "Test File" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[somethingDifferent](./someDifferentFile)\n" +
        "someOtherText"
    val expectedContentOutput = "Test File" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)\n" +
        "someOtherText"
    val expectedContentOutputHugo = "Test File" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName](${createLinkTarget(linkPath, hugoLinkTarget)})\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithWrongLinkAtEndOfFileWithoutNewline(
    fileName: String,
    linkPath: String,
    linkName: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[somethingDifferent](./someDifferentFile)"
    val expectedContentOutput = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName]($linkPath)"
    val expectedContentOutputHugo = "Test File\n" +
        "<!---[Link] ( $linkPath $linkName)-->\n" +
        "[$linkName](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath)"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithTwoAlreadyGeneratedLinks(
    fileName: String,
    linkPath1: String,
    linkName1: String,
    linkPath2: String,
    linkName2: String
): ProcessedFile {
    val expectedContentOutput = "Test File\n" +
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

fun aMarkdownFileWithTwoAlreadyGeneratedLinksForHugo(
    fileName: String,
    linkPath1: String,
    linkName1: String,
    linkPath2: String,
    linkName2: String
): ProcessedFile {
    val expectedContentOutput = "Test File\n" +
        "<!--- [Link]($linkPath1  $linkName1 ) -->\n" +
        "[$linkName1](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath1)\n" +
        "text with the link <!---[Link] ($linkPath2 \"$linkName2\")-->\n" +
        "[$linkName2](${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath2)\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithWrongLinkInsertedAndAMissingLink(
    fileName: String,
    linkPath1: String,
    linkName1: String,
    linkPath2: String,
    linkName2: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[Link] ( $linkPath1 $linkName1)-->" +
        "[somethingDifferent](./someDifferentFile)" +
        "someOtherText" +
        "<!---[Link] ( $linkPath2 $linkName2)-->\n"
    val expectedContentOutput = "Test File\n" +
        "<!---[Link] ( $linkPath1 $linkName1)-->\n" +
        "[$linkName1]($linkPath1)" +
        "someOtherText" +
        "<!---[Link] ( $linkPath2 $linkName2)-->\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

private fun createLinkTarget(linkPath: String, hugoLinkTarget: HUGO_LINK_TARGET): String {
    return when (hugoLinkTarget) {
        GITHUB -> "${SampleGithubRepositoryProperties.EXPECTED_URL_PREFIX}/$linkPath"
        WITHIN_DOCU -> "{{< ref \"$linkPath\" >}}"
    }
}
