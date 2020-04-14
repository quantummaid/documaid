/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

fun aMarkdownFileWithSnippet(
    fileName: String,
    snippetName: String,
    snippet: String,
    snippetLanguage: String = "java"
): ProcessedFile {

    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "someOtherText"
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```$snippetLanguage\n" +
        snippet + "\n" +
        "```\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithSnippetDirectiveAtTheEnd(
    fileName: String,
    snippetName: String,
    snippet: String,
    snippetLanguage: String = "java"
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->"

    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```$snippetLanguage\n" +
        snippet + "\n" +
        "```"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithTwoSnippets(
    fileName: String,
    firstSnippetName: String,
    firstSnippet: String,
    secondSnippetName: String,
    secondSnippet: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + firstSnippetName + ")-->\n" +
        "someOtherText\n" +
        "<!---[CodeSnippet] (" + secondSnippetName + ")-->\n" +
        "\n" +
        "adsadsad\n"

    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + firstSnippetName + ")-->\n" +
        "```java\n" +
        firstSnippet + "\n" +
        "```\n" +
        "someOtherText\n" +
        "<!---[CodeSnippet] (" + secondSnippetName + ")-->\n" +
        "```java\n" +
        secondSnippet + "\n" +
        "```\n" +
        "\n" +
        "adsadsad\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithFullClassSnippetDirective(
    fileName: String,
    relativeClassPath: String,
    snippet: String,
    snippetLanguage: String = "java"
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (file=" + relativeClassPath + ")-->\n" +
        "someOtherText"
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (file=" + relativeClassPath + ")-->\n" +
        "```$snippetLanguage\n" +
        snippet + "\n" +
        "```\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithWrongSnippet(
    fileName: String,
    snippetName: String,
    snippet: String,
    snippetLanguage: String = "java"
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```$snippetLanguage\n" +
        "something completely different" +
        "something completely different" +
        "something completely different" +
        "```\n" +
        "someOtherText"
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```$snippetLanguage\n" +
        snippet + "\n" +
        "```\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithTwoWrongSnippets(
    fileName: String,
    snippetName1: String,
    snippet1: String,
    snippetName2: String,
    snippet2: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName1 + ")-->\n" +
        "```java\n" +
        "something completely different" +
        "something completely different" +
        "something completely different" +
        "```\n" +
        "someOtherText" +
        "<!---[CodeSnippet] (" + snippetName2 + ")-->\n" +
        "```java\n" +
        "someOtherText\n" +
        "someOtherText\n" +
        "someOtherText\n" +
        "someOtherText\n" +
        "someOtherText\n" +
        "```\n"
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName1 + ")-->\n" +
        "```java\n" +
        snippet1 + "\n" +
        "```\n" +
        "someOtherText" +
        "<!---[CodeSnippet] (" + snippetName2 + ")-->\n" +
        "```java\n" +
        snippet2 + "\n" +
        "```\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithAlreadyGeneratedSnippet(
    fileName: String,
    snippetName: String,
    snippet: String,
    snippetLanguage: String = "java"
): ProcessedFile {
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```$snippetLanguage\n" +
        snippet + "\n" +
        "```\n" +
        "someOtherText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithWrongGeneratedSnippetAtEndOfFile(
    fileName: String,
    snippetName: String,
    snippet: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```java\n" +
        "something completely different" +
        "something completely different" +
        "something completely different" +
        "```"
    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName + ")-->\n" +
        "```java\n" +
        snippet + "\n" +
        "```"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithAlreadyGeneratedSnippetAndASecondNotGeneratedSnippet(
    fileName: String,
    snippetName1: String,
    snippet1: String,
    snippetName2: String,
    snippet2: String
): ProcessedFile {
    val contentInput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName1 + ")-->\n" +
        "```java\n" +
        "something completely different" +
        "something completely different" +
        "something completely different" +
        "```\n" +
        "someOtherText" +
        "<!---[CodeSnippet] (" + snippetName2 + ")-->\n" +
        "adasdsadsad"

    val expectedContentOutput = "Test File\n" +
        "<!---[CodeSnippet] (" + snippetName1 + ")-->\n" +
        "```java\n" +
        snippet1 + "\n" +
        "```\n" +
        "someOtherText" +
        "<!---[CodeSnippet] (" + snippetName2 + ")-->\n" +
        "```java\n" +
        snippet2 + "\n" +
        "```\n" +
        "adasdsadsad"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}
