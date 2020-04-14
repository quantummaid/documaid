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
import de.quantummaid.documaid.usecases.tableOfContents.nav.S

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
        "<!---[Nav]-->\n" +
        navigationString + "\n"

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
        "<!---[Nav]-->\n" +
        navigationString + "\n"
    val expectedContentOutputHugo = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}

fun aMarkdownFileWithAWrongNav(fileName: String, navigationString: String): ProcessedFile {
    val contentInput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n" +
        "[&larr;](1_Wrong1.md)$S[Overview](../README.md)$S[&rarr;](3_Wrong2.md)\n"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n" +
        navigationString + "\n"
    val expectedContentOutputHugo = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
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
        "<!---[Nav]-->\n" +
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
        "<!---[Nav]-->\n" +
        "[&larr;](1_Wrong1.md)$S[Overview](../README.md)$S[&rarr;](3_Wrong2.md)"
    val expectedContentOutput = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->\n" +
        navigationString
    val expectedContentOutputHugo = " A file type A\n" +
        "\n" +
        "with some Text\n" +
        "and a navigation at the bottom\n" +
        "\n" +
        "<!---[Nav]-->"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutputHugo)
        .build()
}
