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
package de.quantummaid.documaid.usecases.tableOfContents.nav

import de.quantummaid.documaid.shared.filesystem.EmptySutFile
import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.*
import java.nio.file.Path

const val S = "&nbsp;&nbsp;&nbsp;"

fun aTocTagInReadmeAndMultipleMarkdownFilesWithNavigationDirectives(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeAndMultipleMarkdownFilesWithNavigation")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithNav(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithNav(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aTocTagInReadmeWithADeeplyNestedStructure(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aTocTagInReadmeWithADeeplyNestedStructure")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. Docs
                         1. [A](1_docs/1_A.md)
                         2. [B](1_docs/2_B.md)
                         3. Docs little
                             1. [C](1_docs/3_docsLittle/1_C.md)
                     2. [D](2_D.md)
                     3. [E](3_E.md)
                     4. Legacy
                         1. Deep
                             1. Deep_2
                                 1. [F](4_legacy/1_deep/1_deep_2/1_F.md)
                         2. [G](4_legacy/2_G.md)
                         3. Deep
                             1. [H](4_legacy/3_deep/1_H.md)
                     5. [I](5_I.md)
                     6. Nested
                         1. Nested
                             1. [J](6_nested/1_nested/1_J.md)
                         2. Nested
                             1. [K](6_nested/2_nested/1_K.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                SutDirectory.aDirectory("1_docs").with(
                    aMarkdownFileWithNav(
                        "1_A.md",
                        "[Overview](../README.md)$S[&rarr;](2_B.md)"
                    ),
                    aMarkdownFileWithNav(
                        "2_B.md",
                        "[&larr;](1_A.md)$S[Overview](../README.md)$S[&rarr;](3_docsLittle/1_C.md)"
                    ),
                    SutDirectory.aDirectory("3_docsLittle").with(
                        aMarkdownFileWithNav(
                            "1_C.md",
                            "[&larr;](../2_B.md)$S[Overview](../../README.md)$S[&rarr;](../../2_D.md)"
                        )
                    )
                ),
                aMarkdownFileWithNav(
                    "2_D.md",
                    "[&larr;](1_docs/3_docsLittle/1_C.md)$S[Overview](README.md)$S[&rarr;](3_E.md)"
                ),
                aMarkdownFileWithNav(
                    "3_E.md",
                    "[&larr;](2_D.md)$S[Overview](README.md)$S[&rarr;](4_legacy/1_deep/1_deep_2/1_F.md)"
                ),
                SutDirectory.aDirectory("4_legacy").with(
                    SutDirectory.aDirectory("1_deep").with(
                        SutDirectory.aDirectory("1_deep_2").with(
                            aMarkdownFileWithNav(
                                "1_F.md",
                                "[&larr;](../../../3_E.md)$S[Overview](../../../README.md)$S[&rarr;](../../2_G.md)"
                            )
                        )
                    ),
                    aMarkdownFileWithNav(
                        "2_G.md",
                        "[&larr;](1_deep/1_deep_2/1_F.md)$S[Overview](../README.md)$S[&rarr;](3_deep/1_H.md)"
                    ),
                    SutDirectory.aDirectory("3_deep").with(
                        aMarkdownFileWithNav(
                            "1_H.md",
                            "[&larr;](../2_G.md)$S[Overview](../../README.md)$S[&rarr;](../../5_I.md)"
                        )
                    )
                ),
                aMarkdownFileWithNav(
                    "5_I.md",
                    "[&larr;](4_legacy/3_deep/1_H.md)$S[Overview](README.md)$S[&rarr;](6_nested/1_nested/1_J.md)"
                ),
                SutDirectory.aDirectory("6_nested").with(
                    SutDirectory.aDirectory("1_nested").with(
                        aMarkdownFileWithNav(
                            "1_J.md",
                            "[&larr;](../../5_I.md)$S[Overview](../../README.md)$S[&rarr;](../2_nested/1_K.md)"
                        )
                    ),
                    SutDirectory.aDirectory("2_nested").with(
                        aMarkdownFileWithNav(
                            "1_K.md",
                            "[&larr;](../1_nested/1_J.md)$S[Overview](../../README.md)"
                        )
                    )
                )
            )
    }
}

fun aTocInReadmeWithAnIndexFileWithoutNavigation(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "fileWithoutNav")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", ".", expectedToc),
                EmptySutFile.aFile("1_error.md"),
                aMarkdownFileWithNav("02_SomeImportantStuff.md", "")
            )
    }
}

fun aTocInReadmeWithFileWithNavigationButNotIndexedByToc(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "fileOutsideOfToc")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", expectedToc),
                aMarkdownFileWithNav("1_error.md", ""),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav("1_Introduction.md", "")
                    )
            )
    }
}

fun aFileWithNavigationButNoFileWithToc(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "navWithoutToc")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithNav("1_error.md", "")
            )
    }
}

fun aReadmeWithTocAndSeveralFilesWithCorrectNavigation(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "correctNav")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAlreadyGenerated("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aReadmeWithTocAndAFileWithMissingNav(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "missingNav")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAlreadyGenerated("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithNav(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aReadmeWithTocAndAFileWithWrongNav(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "wrongNav")

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAlreadyGenerated("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithAWrongNav(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aTocTagInReadmeAndNavigationTagAtEndOfFileWithoutNewLine(basePath: Path): SetupUpdate {
    val testDirectoryName = "aTocTagInReadmeAndNavigationTagAtEndOfFileWithoutNewLine"
    val testDir = aTemporyTestDirectory(basePath, testDirectoryName)

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithNavAtEndOfLineWithoutNewLine(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithNavAtEndOfLineWithoutNewLine(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aTocTagInReadmeAndWrongNavigationTagAtEndOfFileWithoutNewLine(basePath: Path): SetupUpdate {
    val testDirectoryName = "aTocTagInReadmeAndNavigationTagAtEndOfFileWithoutNewLine"
    val testDir = aTemporyTestDirectory(basePath, testDirectoryName)

    return { (_, sutFileStructure) ->

        val expectedToc =
            """
                     1. [Introduction](docs/1_Introduction.md)
                     2. [Some important stuff](docs/02_SomeImportantStuff.md)
                     3. [A different chapter](docs/003_ADifferentChapter.md)
            """.trimIndent()

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", expectedToc),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        ),
                        aMarkdownFileWithAWrongNavAtEndOfFile(
                            "02_SomeImportantStuff.md",
                            "[&larr;](1_Introduction.md)" +
                                "$S[Overview](../README.md)$S" +
                                "[&rarr;](003_ADifferentChapter.md)"
                        ),
                        aMarkdownFileWithNavAtEndOfLineWithoutNewLine(
                            "003_ADifferentChapter.md",
                            "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)"
                        )
                    )
            )
    }
}

fun aReadmeWithAMissingTocAndASingleWithCorrectNavigationForHugo(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "correctNav")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", ""),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAlreadyGeneratedNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        )
                    )
            )
    }
}

fun aReadmeWithTocAndAFileWithASingleWrongNavForHugo(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "wrongNav")

    return { (_, sutFileStructure) ->

        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithToc("README.md", "./docs", ""),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithAWrongNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        )
                    )
            )
    }
}

fun aReadmeWithTocAndAFileWithASingleMissingNavForHugo(basePath: Path): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "missingNav")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTocAlreadyGenerated("README.md", "./docs", ""),
                SutDirectory.aDirectory("docs")
                    .with(
                        aMarkdownFileWithNav(
                            "1_Introduction.md",
                            "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)"
                        )
                    )
            )
    }
}
