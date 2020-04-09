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

import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.TestEnvironmentProperty.SAMPLE_FILE
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.PhysicalDirectoryBuilder.Companion.aDirectory
import de.quantummaid.documaid.shared.PhysicalFileBuilder.Companion.aFile
import de.quantummaid.documaid.shared.PhysicalFileSystemStructureBuilder.Companion.createAPhysicalFileSystemStructureIn
import java.nio.file.Path

class TocSampleFilesBuilder {

    companion object {
        fun aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithDocsDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent("./docs")
                val expectedContent = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](./docs)-->
                        1. [Introduction](docs/1_Introduction.md)
                        2. [Some important stuff](docs/02_SomeImportantStuff.md)
                        3. [A different chapter](docs/003_ADifferentChapter.md)
                        4. [Final chapter](docs/04_FinalChapter.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aDirectory("docs")
                                .with(
                                    aFile("1_Introduction.md"),
                                    aFile("003_ADifferentChapter.md"),
                                    aFile("02_SomeImportantStuff.md"),
                                    aFile("04_FinalChapter.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(fileWithTocTagContent, expectedContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeFromSameDirectory(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocInSameDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                val expectedContent = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
                        1. [Introduction](1_Introduction.md)
                        2. [A a a](2_AAA.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("2_AAA.md"),
                            aFile("1_Introduction.md")
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(fileWithTocTagContent, expectedContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithMultipleNestedDirectories(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithNestedDirs"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                val expectedContent = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
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
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
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
                                            aFile("2_SecondNested.md")
                                        )
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(fileWithTocTagContent, expectedContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithNotIndexedMarkdownFiles(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithNotIndexedFiles"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                val expectedContent = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
                        1. [Introduction](1_Introduction.md)
                        2. Docs
                            1. [First docs](02_docs/1_FirstDocs.md)
                            2. [Second docs](02_docs/2_SecondDocs.md)
                        3. Legacy
                            1. [First legacy](3_legacy/1_FirstLegacy.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
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
                                    aFile("notIndexed.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(fileWithTocTagContent, expectedContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithGeneratedOverviewFile(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithGeneratedOverviewFile"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                val expectedContent = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
                        1. [Introduction](1_Introduction.md)
                        2. Docs
                            1. [First docs](02_docs/1_FirstDocs.md)
                            2. [Second docs](02_docs/2_SecondDocs.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("0_Overview.md"),
                            aFile("1_Introduction.md"),
                            aDirectory("02_docs")
                                .with(
                                    aFile("0_Overview.md"),
                                    aFile("1_FirstDocs.md"),
                                    aFile("2_SecondDocs.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(fileWithTocTagContent, expectedContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithTheSameIndexTwice(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithSameIndexTwice"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("1_Introduction.md"),
                            aFile("2_One.md"),
                            aFile("2_Two.md")
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithMissingIndex(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithMissingIndex"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("1_Introduction.md"),
                            aFile("3_Three.md")
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithTheSameIndexTwiceInSubDirectory(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithSameIndexTwiceInSubDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("1_Introduction.md"),
                            aDirectory("02_docs")
                                .with(
                                    aFile("1_FirstDocs.md"),
                                    aFile("2_SecondDocs.md"),
                                    aFile("2_Double.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithMissingIndexInSubDirectory(basePath: Path): SetupUpdate {
            val testIsolatedDir = "tocWithMissingIndexInSubDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent(".")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("1_Introduction.md"),
                            aDirectory("02_docs")
                                .with(
                                    aFile("1_FirstDocs.md"),
                                    aFile("3_Three.md"),
                                    aFile("4_Four.md"),
                                    aFile("5_Five.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithNotExistingScanDirectory(basePath: Path): SetupUpdate {
            val testIsolatedDir = "aTocTagInReadmeWithNotExistingScanDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent("notExisting/")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent)
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocTagInReadmeWithMalFormedOptions(basePath: Path): SetupUpdate {
            val testIsolatedDir = "aTocTagInReadmeWithMalFormedOptions"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val notACorrectOptionsString = "not a correct options String"
                val fileWithTocTagContent = fileWithTocTagContent(notACorrectOptionsString)
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent)
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocInReadmeWithCorrectToc(basePath: Path): SetupUpdate {
            val testIsolatedDir = "aTocInReadmeWithCorrectToc"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val content = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
                        1. [Introduction](1_Introduction.md)
                        2. Docs
                            1. [First docs](02_docs/1_FirstDocs.md)
                            2. [Second docs](02_docs/2_SecondDocs.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(content),
                            aFile("0_Overview.md"),
                            aFile("1_Introduction.md"),
                            aDirectory("02_docs")
                                .with(
                                    aFile("0_Overview.md"),
                                    aFile("1_FirstDocs.md"),
                                    aFile("2_SecondDocs.md")
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.sampleFile(content, content, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocInReadmeWithMissingToc(basePath: Path): SetupUpdate {
            val testIsolatedDir = "aTocInReadmeWithMissingToc"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val fileWithTocTagContent = fileWithTocTagContent("./")
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(fileWithTocTagContent),
                            aFile("0_Overview.md"),
                            aFile("1_Introduction.md")
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(fileWithTocTagContent, "$testIsolatedDir/README.md"))
            }
        }

        fun aTocInReadmeWithIncorrectToc(basePath: Path): SetupUpdate {
            val testIsolatedDir = "aTocInReadmeWithIncorrectToc"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val content = """
                        # Some Heading
                        with some Text
                        underneath for very very good explanation

                        <!---[TOC](.)-->
                        1. [Introduction](1_Introduction.md)
                        2. Docs
                            1. [First docs](02_docs/1_FirstDocs.md)
                            2. [Second docs](02_docs/2_SecondDocs.md)
                        <!---EndOfToc-->

                        and a little bit more text
                    """.trimIndent()
                setupSteps.add {
                    val fileStructure = createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            aFile("README.md")
                                .withContent(content),
                            aFile("0_Overview.md"),
                            aFile("1_Introduction.md")
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }

                testEnvironment.setProperty(SAMPLE_FILE, SampleFile.inputOnlySampleFile(content, "$testIsolatedDir/README.md"))
            }
        }
    }
}

fun fileWithTocTagContent(path: String): String {
    return """
                # Some Heading
                with some Text
                underneath for very very good explanation

                <!---[TOC]($path)-->

                and a little bit more text
            """.trimIndent()
}
