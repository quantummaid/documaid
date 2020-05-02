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
package de.quantummaid.documaid.usecases.hugo.flavor

import de.quantummaid.documaid.shared.filesystem.EmptySutFile
import de.quantummaid.documaid.shared.filesystem.PhysicalDirectoryBuilder
import de.quantummaid.documaid.shared.filesystem.PhysicalFileBuilder
import de.quantummaid.documaid.shared.filesystem.PhysicalFileSystemStructureBuilder
import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory.Companion.aDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithH1Heading
import de.quantummaid.documaid.shared.samplesFiles.aRawMarkdownFile

fun aTypicalQuantumMaidProjectStructure(basePath: String, hugoOutputPath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aTypicalQuantumMaidProjectStructure")

    val introductionMarkdownFile = aMarkdownFileWithH1Heading("1_Introduction.md", "10201001")
    val gettingStartedMarkdownFile = aMarkdownFileWithH1Heading("2_GettingStarted.md", "10202001")
    val usecasesMarkdownFile1 = aMarkdownFileWithH1Heading("1_Usecases1.md", "10203011")
    val usecasesMarkdownFile2 = aMarkdownFileWithH1Heading("2_Usecases2.md", "10203021")
    val encodingMarkdownFile = aMarkdownFileWithH1Heading("4_Encoding.md", "10204001")
    val endingMarkdownFile = aMarkdownFileWithH1Heading("5_Ending.md", "10205001")
    val legacy1MarkdownFile = aMarkdownFileWithH1Heading("1_Legacy1.md", "0")
    val legacy2MarkdownFile = aMarkdownFileWithH1Heading("2_Legacy2.md", "0")

    val readme = aMarkdownFileWithH1Heading("README.md", "20")
    val java10Readme = EmptySutFile.aFile("README.Java10.md")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aDirectory("documentation")
                    .with(
                        introductionMarkdownFile,
                        gettingStartedMarkdownFile,
                        aDirectory("3_Usecases")
                            .with(
                                usecasesMarkdownFile1,
                                usecasesMarkdownFile2
                            ),
                        encodingMarkdownFile,
                        endingMarkdownFile,
                        aDirectory("legacy")
                            .with(
                                legacy1MarkdownFile,
                                legacy2MarkdownFile
                            ),
                        aDirectory("src").with(
                            aDirectory("de").with(
                                aDirectory("documentation").with(
                                    aJavaFileWithOneSnippet("source.java", "snippet")
                                )))),
                aDirectory("something")
                    .with(
                        aMarkdownFileWithH1Heading("README.md", "10"),
                        aMarkdownFileWithH1Heading("1_Introduction", "10")
                    ),
                readme,
                java10Readme,
                aMarkdownFileWithH1Heading("CHANGELOG.md", "10")
            )
        val expectedOutputPath = testDir.path.resolve(hugoOutputPath).toString()
        sutFileStructure.overrideExpectedFileStructure(
            PhysicalFileSystemStructureBuilder.aPhysicalFileStructureIn(expectedOutputPath)
                .with(
                    introductionMarkdownFile.processedFileInHugoFormat(),
                    gettingStartedMarkdownFile.processedFileInHugoFormat(),
                    PhysicalDirectoryBuilder.aDirectory("3_Usecases")
                        .with(
                            usecasesMarkdownFile1.processedFileInHugoFormat(),
                            usecasesMarkdownFile2.processedFileInHugoFormat(),
                            PhysicalFileBuilder.aFile("_index.md").withContent(
                                "---\n" +
                                    "title: \"Usecases\"\n" +
                                    "weight: 10203001\n" +
                                    "skip: \"true\"\n" +
                                    "---\n"
                            )
                        ),
                    encodingMarkdownFile.processedFileInHugoFormat(),
                    endingMarkdownFile.processedFileInHugoFormat(),
                    PhysicalFileBuilder.aFile("_index.md").withContent(
                        "---\n" +
                            "title: \"TestMaid\"\n" +
                            "weight: 10200001\n" +
                            "skip: \"true\"\n" +
                            "---\n"
                    )
                )
                .construct()
        )
    }
}

fun aTypicalQuantumMaidProjectStructureWithIndex(basePath: String, hugoOutputPath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aTypicalQuantumMaidProjectStructure")

    val introductionMarkdownFile = aMarkdownFileWithH1Heading("1_Introduction.md", "1020100001")
    val gettingStartedMarkdownFile = aMarkdownFileWithH1Heading("2_GettingStarted.md", "1020200001")
    val usecasesMarkdownFile1 = aMarkdownFileWithH1Heading("1_Usecases1.md", "1020301001")
    val furtherBelowFile1 = aMarkdownFileWithH1Heading("1_FurtherBelow.md", "1020312011")
    val encodingMarkdownFile = aMarkdownFileWithH1Heading("4_Encoding.md", "1020400001")
    val endingMarkdownFile = aMarkdownFileWithH1Heading("22_Ending.md", "1022200001")

    val readme = aMarkdownFileWithH1Heading("README.md", "20")
    val java10Readme = EmptySutFile.aFile("README.Java10.md")
    val rawMarkdownFile = aRawMarkdownFile("_index.md",
        "---\n" +
            "title: \"Teeest\"\n" +
            "weight: 12\n" +
            "---\n"
    )

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aDirectory("documentation")
                    .with(
                        introductionMarkdownFile,
                        gettingStartedMarkdownFile,
                        aDirectory("3_Usecases")
                            .with(
                                usecasesMarkdownFile1,
                                aDirectory("12_FurtherBelow")
                                    .with(
                                        furtherBelowFile1
                                    )
                            ),
                        encodingMarkdownFile,
                        endingMarkdownFile,
                        rawMarkdownFile
                    ),
                aDirectory("something")
                    .with(
                        aMarkdownFileWithH1Heading("README.md", "10"),
                        aMarkdownFileWithH1Heading("1_Introduction", "10")
                    ),
                readme,
                java10Readme,
                aMarkdownFileWithH1Heading("CHANGELOG.md", "10")
            )
        val expectedOutputPath = testDir.path.resolve(hugoOutputPath).toString()
        sutFileStructure.overrideExpectedFileStructure(
            PhysicalFileSystemStructureBuilder.aPhysicalFileStructureIn(expectedOutputPath)
                .with(
                    introductionMarkdownFile.processedFileInHugoFormat(),
                    gettingStartedMarkdownFile.processedFileInHugoFormat(),
                    PhysicalDirectoryBuilder.aDirectory("3_Usecases")
                        .with(
                            usecasesMarkdownFile1.processedFileInHugoFormat(),
                            PhysicalFileBuilder.aFile("_index.md").withContent(
                                "---\n" +
                                    "title: \"Usecases\"\n" +
                                    "weight: 1020300001\n" +
                                    "skip: \"true\"\n" +
                                    "---\n"
                            ),
                            PhysicalDirectoryBuilder.aDirectory("12_FurtherBelow")
                                .with(
                                    furtherBelowFile1.processedFileInHugoFormat(),
                                    PhysicalFileBuilder.aFile("_index.md").withContent(
                                        "---\n" +
                                            "title: \"FurtherBelow\"\n" +
                                            "weight: 1020312001\n" +
                                            "skip: \"true\"\n" +
                                            "---\n"
                                    )
                                )
                        ),
                    encodingMarkdownFile.processedFileInHugoFormat(),
                    endingMarkdownFile.processedFileInHugoFormat(),
                    rawMarkdownFile.processedFileInHugoFormat()
                )
                .construct()
        )
    }
}

fun aDocumentationStructureWithATooBigIndex(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aTypicalQuantumMaidProjectStructure")

    val markdownFile = aMarkdownFileWithH1Heading("123_Introduction.md", "1020100001")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aDirectory("documentation")
                    .with(
                        markdownFile
                    )
            )
    }
}
