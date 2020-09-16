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
package de.quantummaid.documaid.usecases.link

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory.Companion.aDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.*
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleXmlFileWithOneSnippet.Companion.aXmlFileWithOneSnippet

fun aFileWithASingleLink(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithASingleLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkDirective("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}

fun aFileWithTwoLinks(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aDirectory("subDir")
                    .with(
                        aMarkdownFileWithTwoLinkDirectives(
                            "md1.md",
                            "../${javaFile.fileName}",
                            "sampleLinkName",
                            "subSubDir/${xmlFile.fileName}",
                            "complex !/@_ filename"
                        ),
                        aDirectory("subSubDir")
                            .with(
                                xmlFile
                            )
                    )
            )
    }
}

fun aFileWithTheSameLinksTwice(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTheSameLinksTwice")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aJavaFileWithOneSnippet("source.java", "testSnippet"),
                aMarkdownFileWithTwoLinkDirectives(
                    "md1.md",
                    "source.java",
                    "name1",
                    "source.java",
                    "name2"
                )
            )
    }
}

fun aFileWithWrongLink(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithWrongLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkInserted("md1.md", javaFile.fileName, "linkName")
            )
    }
}

fun aFileWithAnInlineLink(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAnInlineLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithAnInlineLinkDirective("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}

fun aFileWithLinksToNotExistingFiles(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithLinksToNotExistingFiles")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTwoLinkDirectives(
                    "missingLinksFile.md",
                    "someWhere/notExistingFile.java",
                    "linkName1",
                    "differentNotExistingFile.java",
                    "linkName2"
                )
            )
    }
}

fun aCorrectlyGeneratedFileWithTwoLinks(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aCorrectlyGeneratedFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                xmlFile,
                aMarkdownFileWithTwoAlreadyGeneratedLinks(
                    "md1.md",
                    javaFile.fileName,
                    "sampleLinkName",
                    xmlFile.fileName,
                    "complex filename"
                )
            )
    }
}

fun aCorrectlyGeneratedFileWithTwoLinksForHugo(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aCorrectlyGeneratedFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                xmlFile,
                aMarkdownFileWithTwoAlreadyGeneratedLinksForHugo(
                    "md1.md",
                    javaFile.fileName,
                    "sampleLinkName",
                    xmlFile.fileName,
                    "complex filename"
                )
            )
    }
}

fun aFileWithAMissingLink(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAMissingLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkDirective("oneMissingLinkFileSampleFiles.md", javaFile.fileName, "linkName")
            )
    }
}

fun aFileWithLinkANotExistingFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithLinkANotExistingFile")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithALinkDirective("aLinkToANotExistingFile.md", "notExistingFile.java", "linkName")
            )
    }
}

fun aFileWithMultipleLinkErrors(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithMultipleLinkErrors")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkInsertedAndAMissingLink(
                    "multipleLinkErrors.md",
                    javaFile.fileName,
                    "linkName1",
                    javaFile.fileName,
                    "linkName2"
                )
            )
    }
}

fun aFileWithASingleLinkAtTheEndOfFileWithoutNewline(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithASingleLinkAtEndOfFile")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkAtTheEndOfFileWithoutNewLine("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}

fun aFileWithWrongLinkAtTheEndOfFileWithoutNewline(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAWrongLinkAtEndOfFile")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkAtEndOfFileWithoutNewline("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}

fun aFileWithALinkToADocumentationFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithALinkToADocumentationFile")

    val targetMarkdownFile = aRawMarkdownFile("02_SomeChapter.md", "blabla")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aDirectory("documentation")
                    .with(
                        targetMarkdownFile,
                        aRawMarkdownFile("_index.md", ""),
                        aMarkdownFileWithALinkDirective(
                            "1_Introduction.md",
                            "02_SomeChapter.md",
                            "linkName",
                            hugoLinkTarget = HUGO_LINK_TARGET.WITHIN_DOCU
                        )
                    )
            )
    }
}

fun aFileWithAWrongLinkToADocumentationFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAWrongLinkToADocumentationFile")

    val targetMarkdownFile = aRawMarkdownFile("02_SomeChapter.md", "blabla")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aDirectory("documentation")
                    .with(
                        aDirectory("2_Something")
                            .with(
                                targetMarkdownFile,
                                aRawMarkdownFile("_index.md", "")
                            ),
                        aRawMarkdownFile("_index.md", ""),
                        aMarkdownFileWithWrongLinkInsertedInlineStyle(
                            "1_Introduction.md",
                            "2_Something/02_SomeChapter.md",
                            "linkName",
                            hugoLinkTarget = HUGO_LINK_TARGET.WITHIN_DOCU
                        )
                    )
            )
    }
}
