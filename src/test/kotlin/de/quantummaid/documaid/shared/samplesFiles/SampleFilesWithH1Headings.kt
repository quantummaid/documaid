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

fun aMarkdownFileWithH1Heading(fileName: String, weight: String): ProcessedFile {

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

fun aMarkdownFileWithADifferentH1Heading(fileName: String, weight: String): ProcessedFile {

    val contentInput = "\n\n" +
        "#This is my Heading \n"
    val expectedContentOutput = "---\n" +
        "title: \"This is my Heading \"\n" +
        "weight: $weight\n" +
        "---\n"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

fun aMarkdownFileWithH2Heading(fileName: String): ProcessedFile {

    val contentInput = "## This is my Heading\n" +
        "someOtherText"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, "")
        .withProcessedNameAndContentInHugoFormat(fileName, contentInput)
        .build()
}

fun aMarkdownFileWithTextBeforeHeading(fileName: String, weight: String): ProcessedFile {

    val contentInput = " SomeText\n" +
        "# This is my Heading\n" +
        "someOtherText"
    val expectedContentOutput = "" +
        "---\n" +
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

fun aMarkdownFileWithExistingH1Heading(fileName: String, weight: String): ProcessedFile {

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

fun aMarkdownFileWithWrongH1Heading(fileName: String): ProcessedFile {

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
