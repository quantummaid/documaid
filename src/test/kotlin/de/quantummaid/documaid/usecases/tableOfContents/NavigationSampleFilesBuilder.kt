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
import de.quantummaid.documaid.givenWhenThen.SampleFiles
import de.quantummaid.documaid.givenWhenThen.TestEnvironmentProperty
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.PhysicalDirectoryBuilder
import de.quantummaid.documaid.shared.PhysicalDirectoryBuilder.Companion.aDirectory
import de.quantummaid.documaid.shared.PhysicalFileBuilder
import de.quantummaid.documaid.shared.PhysicalFileSystemStructureBuilder
import java.nio.file.Path

class NavigationSampleFilesBuilder {

    companion object {
        fun aTocTagInReadmeAndMultipleMarkdownFilesWithNavigationDirectives(basePath: Path): SetupUpdate {
            val testIsolatedDir = "navWithDocsDirectory"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithTocDirective("README.md", "./docs", """
                        1. [Introduction](docs/1_Introduction.md)
                        2. [Some important stuff](docs/02_SomeImportantStuff.md)
                        3. [A different chapter](docs/003_ADifferentChapter.md)""".trimIndent())
                val file1 = fileWithNavigationDirective("./docs/1_Introduction.md",
                    "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)")
                val file2 = fileWithNavigationDirective("./docs/02_SomeImportantStuff.md",
                    "[&larr;](1_Introduction.md)$S[Overview](../README.md)$S[&rarr;](003_ADifferentChapter.md)")
                val file3 = fileWithNavigationDirective("./docs/003_ADifferentChapter.md",
                    "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            PhysicalDirectoryBuilder.aDirectory("docs")
                                .with(
                                    file1.asBuilder(),
                                    file3.asBuilder(),
                                    file2.asBuilder()
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
                val sampleFiles = SampleFiles(
                    readmeFileWithToc,
                    file1,
                    file2,
                    file3
                )
                testEnvironment.setProperty(TestEnvironmentProperty.SAMPLE_FILES, sampleFiles)
            }
        }

        fun aTocTagInReadmeWithADeeplyNestedStructure(basePath: Path): SetupUpdate {
            val testIsolatedDir = "deeplyNestedNavStructure"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithTocDirective("README.md", ".", """
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
                                1. [K](6_nested/2_nested/1_K.md)""".trimIndent())
                val fileA = fileWithNavigationDirective("./1_docs/1_A.md",
                    "[Overview](../README.md)$S[&rarr;](2_B.md)")
                val fileB = fileWithNavigationDirective("./1_docs/2_B.md",
                    "[&larr;](1_A.md)$S[Overview](../README.md)$S[&rarr;](3_docsLittle/1_C.md)")
                val fileC = fileWithNavigationDirective("1_docs/3_docsLittle/1_C.md",
                    "[&larr;](../2_B.md)$S[Overview](../../README.md)$S[&rarr;](../../2_D.md)")
                val fileD = fileWithNavigationDirective("./2_D.md",
                    "[&larr;](1_docs/3_docsLittle/1_C.md)$S[Overview](./README.md)$S[&rarr;](3_E.md)")
                val fileE = fileWithNavigationDirective("./3_E.md",
                    "[&larr;](2_D.md)$S[Overview](./README.md)$S[&rarr;](./4_legacy/1_deep/1_deep_2/1_F.md)")
                val fileF = fileWithNavigationDirective("./4_legacy/1_deep/1_deep_2/1_F.md",
                    "[&larr;](../../../3_E.md)$S[Overview](../../../README.md)$S[&rarr;](../../2_G.md)")
                val fileG = fileWithNavigationDirective("./4_legacy/2_G.md",
                    "[&larr;](1_deep/1_deep_2/1_F.md)$S[Overview](../README.md)$S[&rarr;](3_deep/1_H.md)")
                val fileH = fileWithNavigationDirective("./4_legacy/3_deep/1_H.md",
                    "[&larr;](../2_G.md)$S[Overview](../../README.md)$S[&rarr;](../../5_I.md)")
                val fileI = fileWithNavigationDirective("./5_I.md",
                    "[&larr;](4_legacy/3_deep/1_H.md)$S[Overview](./README.md)$S[&rarr;](6_nested/1_nested/1_J.md)")
                val fileJ = fileWithNavigationDirective("./6_nested/1_nested/1_J.md",
                    "[&larr;](../../5_I.md)$S[Overview](../../README.md)$S[&rarr;](../2_nested/1_K.md)")
                val fileK = fileWithNavigationDirective("./6_nested/2_nested/1_K.md",
                    "[&larr;](../1_nested/1_J.md)$S[Overview](../../README.md)")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            aDirectory("1_docs").with(
                                fileA.asBuilder(),
                                fileB.asBuilder(),
                                aDirectory("3_docsLittle").with(
                                    fileC.asBuilder())),
                            fileD.asBuilder(),
                            fileE.asBuilder(),
                            aDirectory("4_legacy").with(
                                aDirectory("1_deep").with(
                                    aDirectory("1_deep_2").with(
                                        fileF.asBuilder())),
                                fileG.asBuilder(),
                                aDirectory("3_deep").with(
                                    fileH.asBuilder())),
                            fileI.asBuilder(),
                            aDirectory("6_nested").with(
                                aDirectory("1_nested").with(
                                    fileJ.asBuilder()),
                                aDirectory("2_nested").with(
                                    fileK.asBuilder())))
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
                val sampleFiles = SampleFiles(
                    readmeFileWithToc,
                    fileA,
                    fileB,
                    fileC
                )
                testEnvironment.setProperty(TestEnvironmentProperty.SAMPLE_FILES, sampleFiles)
            }
        }

        fun aTocInReadmeWithAnIndexFileWithoutNavigation(basePath: Path): SetupUpdate {
            val testIsolatedDir = "fileWithoutNav"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithTocDirective("README.md", ".", "")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            PhysicalFileBuilder.aFile("1_error.md")
                                .withContent(""),
                            fileWithNavigationDirective("2_success.md", "").asBuilder()
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }

        fun aTocInReadmeWithFileWithNavigationButNotIndexedByToc(basePath: Path): SetupUpdate {
            val testIsolatedDir = "fileOutsideOfToc"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithTocDirective("README.md", "./docs", "")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            fileWithNavigationDirective("1_error.md", "").asBuilder(),
                            PhysicalDirectoryBuilder.aDirectory("docs")
                                .with(
                                    fileWithNavigationDirective("1_success.md", "").asBuilder()
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }

        fun aFileWithNavigationButNotFileWithToc(basePath: Path): SetupUpdate {
            val testIsolatedDir = "navWithoutToc"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->
                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            fileWithNavigationDirective("1_error.md", "").asBuilder()
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }

        fun aReadmeWithTocAndSeveralFilesWithCorrectNavigation(basePath: Path): SetupUpdate {
            val testIsolatedDir = "correctNav"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithToc("README.md", "./docs", """
                        1. [Introduction](docs/1_Introduction.md)
                        2. [Some important stuff](docs/02_SomeImportantStuff.md)
                        3. [A different chapter](docs/003_ADifferentChapter.md)""".trimIndent())
                val file1 = fileWithNavigation("./docs/1_Introduction.md",
                    "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)")
                val file2 = fileWithNavigation("./docs/02_SomeImportantStuff.md",
                    "[&larr;](1_Introduction.md)$S[Overview](../README.md)$S[&rarr;](003_ADifferentChapter.md)")
                val file3 = fileWithNavigation("./docs/003_ADifferentChapter.md",
                    "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            PhysicalDirectoryBuilder.aDirectory("docs")
                                .with(
                                    file1.asBuilder(),
                                    file3.asBuilder(),
                                    file2.asBuilder()
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }

        fun aReadmeWithTocAndAFileWithMissingNav(basePath: Path): SetupUpdate {
            val testIsolatedDir = "missingNav"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithToc("README.md", "./docs", """
                        1. [Introduction](docs/1_Introduction.md)
                        2. [Some important stuff](docs/02_SomeImportantStuff.md)
                        3. [A different chapter](docs/003_ADifferentChapter.md)""".trimIndent())
                val file1 = fileWithNavigation("./docs/1_Introduction.md",
                    "[Overview](../README.md)$S[&rarr;](02_SomeImportantStuff.md)")
                val file2 = fileWithNavigationDirective("./docs/02_SomeImportantStuff.md", "")
                val file3 = fileWithNavigation("./docs/003_ADifferentChapter.md",
                    "[&larr;](02_SomeImportantStuff.md)$S[Overview](../README.md)")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            PhysicalDirectoryBuilder.aDirectory("docs")
                                .with(
                                    file1.asBuilder(),
                                    file3.asBuilder(),
                                    file2.asBuilder()
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }

        fun aReadmeWithTocAndAFileWithWrongNav(basePath: Path): SetupUpdate {
            val testIsolatedDir = "wrongNav"
            val baseDir = basePath.resolve(testIsolatedDir)

            return { (testEnvironment, configurationBuilder, _, setupSteps, cleanupSteps) ->

                val readmeFileWithToc = fileWithToc("README.md", "./docs", """
                        1. [Introduction](docs/1_Introduction.md)""".trimIndent())
                val file1 = fileWithNavigation("./docs/1_Introduction.md",
                    "[Overview](../README_DIFFERENT.md)")

                configurationBuilder.withBasePath(baseDir)
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, baseDir.toString())

                setupSteps.add {
                    val fileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(baseDir)
                        .with(
                            readmeFileWithToc.asBuilder(),
                            PhysicalDirectoryBuilder.aDirectory("docs")
                                .with(
                                    file1.asBuilder()
                                )
                        )
                        .build()
                    cleanupSteps.add { fileStructure.cleanUp() }
                }
            }
        }
    }
}

fun fileWithTocDirective(fileName: String, tocPath: String, tocString: String): SampleFile {
    val content = """
                # Some Heading
                with some Text
                underneath for very very good explanation

                <!---[TOC]($tocPath)-->

                and a little bit more text
            """.trimIndent()
    val expectedContentOutput = content.replace("<!---[TOC]($tocPath)-->", "<!---[TOC]($tocPath)-->\n$tocString\n<!---EndOfToc-->")
    return SampleFile.sampleFile(content, expectedContentOutput, fileName)
}

fun fileWithToc(fileName: String, tocPath: String, tocString: String): SampleFile {
    val content = """
                # Some Heading
                with some Text
                underneath for very very good explanation

                <!---[TOC]($tocPath)-->""" + "\n$tocString\n<!---EndOfToc-->" +
        """
                and a little bit more text
            """.trimIndent()
    return SampleFile.sampleFile(content, content, fileName)
}

fun fileWithNavigationDirective(baseDirRelativePath: String, navigationString: String): SampleFile {
    val content = """
        # A file type A

        with some Text
        and a navigation at the bottom

        <!---[Nav]-->
    """.trimIndent()
    val expectedContentOutput = content.replace("<!---[Nav]-->", "<!---[Nav]-->\n$navigationString")
    return SampleFile.sampleFileInDirectory(content, expectedContentOutput, baseDirRelativePath)
}

fun fileWithNavigation(baseDirRelativePath: String, navigationString: String): SampleFile {
    val content = """
        # A file type A

        with some Text
        and a navigation at the bottom

        <!---[Nav]-->${"\n" + navigationString}

        some other Text
    """.trimIndent()
    return SampleFile.sampleFileInDirectory(content, "", baseDirRelativePath)
}

const val S = "&nbsp;&nbsp;&nbsp;"
