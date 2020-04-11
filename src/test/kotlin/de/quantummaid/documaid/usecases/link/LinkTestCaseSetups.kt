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

package de.quantummaid.documaid.usecases.link

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory.Companion.aDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleXmlFileWithOneSnippet.Companion.aXmlFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithALinkDirective
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithALinkDirectiveAtTheEndOfFileWithoutNewLine
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTwoAlreadyGeneratedLinks
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTwoAlreadyGeneratedLinksForHugo
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTwoLinkDirectives
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongLinkInserted
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongLinkInsertedAndAMissingLink
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongLinkInsertedAtEndOfFileWithoutNewline

fun aFileWithASingleLink(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithASingleLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkDirective("md1.md", javaFile.fileName, "sampleLinkName"))
    }
}

fun aFileWithTwoLinks(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aDirectory("subDir")
                    .with(
                        aMarkdownFileWithTwoLinkDirectives("md1.md",
                            "../${javaFile.fileName}", "sampleLinkName",
                            "subSubDir/${xmlFile.fileName}", "complex !/@_ filename"),
                        aDirectory("subSubDir")
                            .with(
                                xmlFile)))
    }
}

fun aFileWithTheSameLinksTwice(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithTheSameLinksTwice")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aJavaFileWithOneSnippet("source.java", "testSnippet"),
                aMarkdownFileWithTwoLinkDirectives("md1.md",
                    "source.java", "name1",
                    "source.java", "name2"))
    }
}

fun aFileWithWrongLink(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithWrongLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkInserted("md1.md", javaFile.fileName, "linkName"))
    }
}

fun aFileWithLinksToNotExistingFiles(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithLinksToNotExistingFiles")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTwoLinkDirectives("missingLinksFile.md",
                    "someWhere/notExistingFile.java", "linkName1",
                    "differentNotExistingFile.java", "linkName2"))
    }
}

fun aCorrectlyGeneratedFileWithTwoLinks(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aCorrectlyGeneratedFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                xmlFile,
                aMarkdownFileWithTwoAlreadyGeneratedLinks("md1.md",
                    javaFile.fileName, "sampleLinkName",
                    xmlFile.fileName, "complex filename"))
    }
}

fun aCorrectlyGeneratedFileWithTwoLinksForHugo(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aCorrectlyGeneratedFileWithTwoLinks")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    val xmlFile = aXmlFileWithOneSnippet("config.xml", "differentSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                xmlFile,
                aMarkdownFileWithTwoAlreadyGeneratedLinksForHugo("md1.md",
                    javaFile.fileName, "sampleLinkName",
                    xmlFile.fileName, "complex filename"))
    }
}

fun aFileWithAMissingLink(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithAMissingLink")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkDirective("oneMissingLinkFileSampleFiles.md", javaFile.fileName, "linkName"))
    }
}

fun aFileWithLinkANotExistingFile(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithLinkANotExistingFile")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithALinkDirective("aLinkToANotExistingFile.md", "someWhere/notExistingFile.java", "linkName"))
    }
}

fun aFileWithMultipleLinkErrors(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithMultipleLinkErrors")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkInsertedAndAMissingLink("multipleLinkErrors.md",
                    javaFile.fileName, "linkName1",
                    javaFile.fileName, "linkName2"))
    }
}

fun aFileWithASingleLinkAtTheEndOfFileWithoutNewline(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithASingleLinkAtEndOfFile")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithALinkDirectiveAtTheEndOfFileWithoutNewLine("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}

fun aFileWithWrongLinkAtTheEndOfFileWithoutNewline(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithAWrongLinkAtEndOfFile")

    val javaFile = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithWrongLinkInsertedAtEndOfFileWithoutNewline("md1.md", javaFile.fileName, "sampleLinkName")
            )
    }
}