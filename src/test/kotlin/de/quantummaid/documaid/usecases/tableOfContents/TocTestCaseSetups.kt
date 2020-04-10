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

package de.quantummaid.documaid.usecases.tableOfContents

import de.quantummaid.documaid.shared.EmptySutFile.Companion.aFile
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.SutDirectory.Companion.aDirectory
import de.quantummaid.documaid.shared.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAWrongToc
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAWrongTocAtEndOfFile
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithToc
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTocAlreadyGenerated
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTocAtTheEndOfFileWithoutNewLine
import java.nio.file.Path

fun aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory")

    return { (_, _, sutFileStructure, _, _) ->

        val expectedToc = """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
                     4. [Final chapter](docs/04_FinalChapter.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", expectedToc),
                aDirectory("docs")
                    .with(
                        aFile("1_Introduction.md"),
                        aFile("003_ADifferentChapter.md"),
                        aFile("02_SomeImportantStuff.md"),
                        aFile("04_FinalChapter.md")))
    }
}

fun aTocTagInReadmeFromSameDirectory(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeFromSameDirectory")

    return { (_, _, sutFileStructure, _, _) ->

        val expectedToc = """
                     1. [Introduction](1_Introduction.md)
                     2. [A a a](2_AAA.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                aFile("2_AAA.md"),
                aFile("1_Introduction.md"))
    }
}

fun aTocTagInReadmeWithMultipleNestedDirectories(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithMultipleNestedDirectories")

    return { (_, _, sutFileStructure, _, _) ->
        val expectedToc = """
                     1. [Introduction](1_Introduction.md)
                     2. Docs
                         1. [First docs](02_docs/1_FirstDocs.md)
                         2. [Second docs](02_docs/2_SecondDocs.md)
                         3. [Third docs](02_docs/3_ThirdDocs.md)
                     3. [A chapter](3_aChapter.md)
                     4. Legacy
                         1. [First legacy](4_legacy/1_FirstLegacy.md)
                         2. Nested
                             1. [First nested](4_legacy/002_nested/1_FirstNested.md)
                             2. [Second nested](4_legacy/002_nested/2_SecondNested.md)
                             3. [Third nested](4_legacy/002_nested/3_ThirdNested.md)
                         3. [Third legacy](4_legacy/3_ThirdLegacy.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                aFile("1_Introduction.md"),
                aFile("3_aChapter.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("1_FirstDocs.md"),
                        aFile("3_ThirdDocs.md"),
                        aFile("2_SecondDocs.md")
                    ),
                aDirectory("4_legacy")
                    .with(
                        aFile("1_FirstLegacy.md"),
                        aFile("3_ThirdLegacy.md"),
                        aDirectory("002_nested")
                            .with(
                                aFile("1_FirstNested.md"),
                                aFile("3_ThirdNested.md"),
                                aFile("2_SecondNested.md"))))
    }
}

fun aTocTagInReadmeWithNotIndexedMarkdownFiles(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithNotIndexedMarkdownFiles")

    return { (_, _, sutFileStructure, _, _) ->
        val expectedToc = """
                     1. [Introduction](1_Introduction.md)
                     2. Docs
                         1. [First docs](02_docs/1_FirstDocs.md)
                         2. [Second docs](02_docs/2_SecondDocs.md)
                     3. Legacy
                         1. [First legacy](3_legacy/1_FirstLegacy.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                aFile("1_Introduction.md"),
                aFile("notIndexed.md"),
                aFile("xml.xml"),
                aDirectory("02_docs")
                    .with(
                        aFile("1_FirstDocs.md"),
                        aFile("notIndexed.md"),
                        aFile("2_SecondDocs.md")
                    ),
                aDirectory("3_legacy")
                    .with(
                        aFile("1_FirstLegacy.md"),
                        aFile("notIndexed.md")))
    }
}

fun aTocTagInReadmeWithGeneratedOverviewFile(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithGeneratedOverviewFile")

    return { (_, _, sutFileStructure, _, _) ->
        val expectedToc = """
                     1. [Introduction](1_Introduction.md)
                     2. Docs
                         1. [First docs](02_docs/1_FirstDocs.md)
                         2. [Second docs](02_docs/2_SecondDocs.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                aFile("0_Overview.md"),
                aFile("1_Introduction.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("0_Overview.md"),
                        aFile("1_FirstDocs.md"),
                        aFile("2_SecondDocs.md")
                    )
            )
    }
}

fun aTocTagInReadmeWithTheSameIndexTwice(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "tocWithSameIndexTwice")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", ""),
                aFile("1_Introduction.md"),
                aFile("2_One.md"),
                aFile("2_Two.md"))
    }
}

fun aTocTagInReadmeWithMissingIndex(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "tocWithMissingIndex")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", ""),
                aFile("1_Introduction.md"),
                aFile("3_Three.md"))
    }
}

fun aTocTagInReadmeWithTheSameIndexTwiceInSubDirectory(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "tocWithSameIndexTwiceInSubDirectory")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", ""),
                aFile("1_Introduction.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("1_FirstDocs.md"),
                        aFile("2_SecondDocs.md"),
                        aFile("2_Double.md")))
    }
}

fun aTocTagInReadmeWithMissingIndexInSubDirectory(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "tocWithMissingIndexInSubDirectory")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", ""),
                aFile("1_Introduction.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("1_FirstDocs.md"),
                        aFile("3_Three.md"),
                        aFile("4_Four.md"),
                        aFile("5_Five.md")))
    }
}

fun aTocTagInReadmeWithNotExistingScanDirectory(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithNotExistingScanDirectory")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "notExisting/", ""))
    }
}

fun aTocTagInReadmeWithMalFormedOptions(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithMalFormedOptions")

    val notACorrectOptionsString = "not a correct options String"
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", notACorrectOptionsString, ""))
    }
}

fun aTocInReadmeWithCorrectToc(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocInReadmeWithCorrectToc")

    val expectedToc = """
                     1. [Introduction](1_Introduction.md)
                     2. Docs
                         1. [First docs](02_docs/1_FirstDocs.md)
                         2. [Second docs](02_docs/2_SecondDocs.md)
                """.trimIndent()
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAlreadyGenerated("README.md", expectedToc, ""),
                aFile("0_Overview.md"),
                aFile("1_Introduction.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("0_Overview.md"),
                        aFile("1_FirstDocs.md"),
                        aFile("2_SecondDocs.md")))
    }
}

fun aTocInReadmeWithMissingToc(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocInReadmeWithMissingToc")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", ""))
    }
}

fun aTocInReadmeWithIncorrectToc(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocInReadmeWithIncorrectToc")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithAWrongToc("README.md", ".", ""),
                aFile("0_Overview.md"),
                aFile("1_Introduction.md"),
                aDirectory("02_docs")
                    .with(
                        aFile("0_Overview.md"),
                        aFile("1_FirstDocs.md"),
                        aFile("2_SecondDocs.md")))
    }
}

fun aTocTagAtTheEndOfFileWithoutNewline(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagAtTheEndOfFileWithoutNewline")

    return { (_, _, sutFileStructure, _, _) ->

        val expectedToc = """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
                     4. [Final chapter](docs/04_FinalChapter.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAtTheEndOfFileWithoutNewLine("README.md", "./docs", expectedToc),
                aDirectory("docs")
                    .with(
                        aFile("1_Introduction.md"),
                        aFile("003_ADifferentChapter.md"),
                        aFile("02_SomeImportantStuff.md"),
                        aFile("04_FinalChapter.md")))
    }
}

fun aWrongTocTagAtTheEndOfFileWithoutNewline(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aWrongTocTagAtTheEndOfFileWithoutNewline")

    return { (_, _, sutFileStructure, _, _) ->

        val expectedToc = """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
                     4. [Final chapter](docs/04_FinalChapter.md)
                """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithAWrongTocAtEndOfFile("README.md", "./docs", expectedToc),
                aDirectory("docs")
                    .with(
                        aFile("1_Introduction.md"),
                        aFile("003_ADifferentChapter.md"),
                        aFile("02_SomeImportantStuff.md"),
                        aFile("04_FinalChapter.md")))
    }
}