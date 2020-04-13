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

package de.quantummaid.documaid

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAllDirectives
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAllDirectivesAlreadyGenerated
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAlreadyGeneratedNav
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithNav
import de.quantummaid.documaid.usecases.tableOfContents.nav.S

fun severalFilesWithLinksAndSnippets(basePath: String): SetupUpdate {

    val testDir = aTemporyTestDirectory(basePath, "severalFilesWithLinksAndSnippets")

    return { (_, sutFileStructure) ->

        val expectedToc = """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
                """.trimIndent()
        val javaFile = aJavaFileWithOneSnippet("source.java", "snippet")
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithAllDirectives("README.md", "./docs", expectedToc,
                    javaFile.fileName, "linkedFile",
                    "snippet", javaFile.snippet),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav("1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"),
                        aMarkdownFileWithNav("02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)$S[Overview](../README.md)$S[&rarr;](003_ADifferentChapter.md)"),
                        aMarkdownFileWithNav("003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)")))
    }
}

fun aCorrectlyGeneratedFileWithLinksAndSnippets(basePath: String): SetupUpdate {

    val testDir = aTemporyTestDirectory(basePath, "aCorrectlyGeneratedFileWithLinksAndSnippets")

    return { (_, sutFileStructure) ->

        val expectedToc = """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
                """.trimIndent()
        val javaFile = aJavaFileWithOneSnippet("source.java", "snippet")
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithAllDirectivesAlreadyGenerated("README.md", "./docs", expectedToc,
                    javaFile.fileName, "linkedFile",
                    "snippet", javaFile.snippet),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAlreadyGeneratedNav("1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"),
                        aMarkdownFileWithAlreadyGeneratedNav("02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)$S[Overview](../README.md)$S[&rarr;](003_ADifferentChapter.md)"),
                        aMarkdownFileWithAlreadyGeneratedNav("003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)")))
    }
}
