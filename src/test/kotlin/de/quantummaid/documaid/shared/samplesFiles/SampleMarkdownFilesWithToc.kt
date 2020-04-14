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
        "1. [Different](1_Different.md)" +
        "2. [EvenMoreDifferent](2_EventMoreDifferent.md)" +
        "3. [WouldNeverChooseThis](3_Different3.md)" +
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
    val expectedContentOutputHugo = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation" +
        "\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "\n" +
        "and a little bit more text\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
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
        "underneath for very very good explanation\n" +
        "<!---[TOC]($tocPath)-->\n" +
        "1. [Different](1_Different.md)" +
        "2. [EvenMoreDifferent](2_EventMoreDifferent.md)" +
        "3. [WouldNeverChooseThis](3_Different3.md)" +
        "\n<!---EndOfToc-->"
    val expectedContentOutput = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation\n" +
        "<!---[TOC]($tocPath)-->\n" +
        expectedToc +
        "\n<!---EndOfToc-->"
    val expectedContentOutputHugo = " Some Heading" +
        "with some Text" +
        "underneath for very very good explanation\n" +
        "<!---[TOC]($tocPath)-->"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}
