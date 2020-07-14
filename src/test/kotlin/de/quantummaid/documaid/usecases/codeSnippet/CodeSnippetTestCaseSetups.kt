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
package de.quantummaid.documaid.usecases.codeSnippet

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithACommentsInSnippet.Companion.aJavaFileWithACommentsInSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithADifferentSnippet.Companion.aJavaFileWithADifferentSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithFullClassSnippet.Companion.aJavaFileWithOneFullSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithSnippetWithMultipleSuppressWarning.Companion.sampleJavaFileWithSnippetWithTwoSuppressWarning
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithSnippetWithNoSonarComments.Companion.sampleJavaFileWithSnippetWithNoSonarComments
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithSnippetWithSuppressWarning.Companion.sampleJavaFileWithSnippetWithSuppressWarning
import de.quantummaid.documaid.shared.samplesFiles.SampleJavaFileWithTwoSnippets.Companion.aJavaFileWithTwoSnippets
import de.quantummaid.documaid.shared.samplesFiles.SampleKotlinFileWithOneSnippet.Companion.aKotlinFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.SampleXmlFileWithOneSnippet.Companion.aXmlFileWithOneSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAlreadyGeneratedSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAlreadyGeneratedSnippetAndASecondNotGeneratedSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithFullClassSnippetDirective
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithSnippet
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithSnippetDirectiveAtTheEnd
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTwoSnippets
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTwoWrongSnippets
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongGeneratedSnippetAtEndOfFile
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongSnippet

fun aFileWithASingleCodeSnippet(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithASingleCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippet("md1.md", "testSnippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithATwoCodeSnippets(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithATwoCodeSnippets")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                javaFileWithADifferentSnippet,
                aMarkdownFileWithTwoSnippets("md1.md",
                    "snippet1", javaFileWithSnippet.snippet,
                    "snippet2", javaFileWithADifferentSnippet.snippet)
            )
    }
}

fun aFileWithTheSameCodeSnippetTwice(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTheSameCodeSnippetTwice")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithTwoSnippets("md1.md",
                    "snippet", javaFileWithSnippet.snippet,
                    "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun filesWithTheSameCodeSnippet(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTheSameCodeSnippetTwice")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippet("md1.md", "snippet", javaFileWithSnippet.snippet),
                aMarkdownFileWithSnippet("md2.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithTheSameCodeSnippetTwiceInDifferentFiles(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTheSameCodeSnippetTwiceInDifferentFiles")

    val javaFileWithSnippet = aJavaFileWithADifferentSnippet("source.java", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippet("md1.md", "snippet", javaFileWithSnippet.snippet),
                aMarkdownFileWithSnippet("md2.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithACodeSnippetFromAnXmlFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithACodeSnippetFromANonJavaFile")

    val xmlFileWithSnippet = aXmlFileWithOneSnippet("source.xml", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                xmlFileWithSnippet,
                aMarkdownFileWithSnippet("md1.md", "snippet", xmlFileWithSnippet.snippet, "xml")
            )
    }
}

fun aFileWithACodeSnippetFromAKotlinFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithACodeSnippetFromAKotlinFile")

    val kotlinFileWithSnippet = aKotlinFileWithOneSnippet("source.kt", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
                .with(
                        kotlinFileWithSnippet,
                        aMarkdownFileWithSnippet("md1.md", "snippet", kotlinFileWithSnippet.snippet, "kotlin")
                )
    }
}

fun aFileWithCommentsInCodeSnippet(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithCommentsInCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithACommentsInSnippet("source.java", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippet("md1.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithAFullClassSnippet(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAFullClassSnippet")

    val javaFile = aJavaFileWithOneFullSnippet("FullClass.java")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithFullClassSnippetDirective("md1.md", javaFile.fileName, javaFile.snippet)
            )
    }
}

fun aFileWithWrongCodeSnippetPresent(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithWrongCodeSnippetPresent")

    val javaFileWithSnippet = aJavaFileWithADifferentSnippet("source.java", "snippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithWrongSnippet("wrongCodeSnippet.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithTwoOutdatedCodeSnippets(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTwoOutdatedCodeSnippets")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                javaFileWithADifferentSnippet,
                aMarkdownFileWithTwoWrongSnippets("md1.md",
                    "snippet1", javaFileWithSnippet.snippet,
                    "snippet2", javaFileWithADifferentSnippet.snippet)
            )
    }
}

fun filesWithDuplicateCodeSnippets(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "duplicateSnippetsDirectory")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aJavaFileWithOneSnippet("snippetDuplicate1.java", "doubleDuplicate"),
                aXmlFileWithOneSnippet("snippetDuplicate2.xml", "doubleDuplicate"),
                aJavaFileWithADifferentSnippet("snippetTriplicate1.java", "tripleDuplicate"),
                SutDirectory.aDirectory("subDir")
                    .with(
                        aJavaFileWithOneSnippet("snippetTriplicate2.java", "tripleDuplicate"),
                        aXmlFileWithOneSnippet("snippetTriplicate3.xml", "tripleDuplicate")
                    )
            )
    }
}

fun aCorrectlyGeneratedFileWithOneCodeSnippet(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aCorrectlyGeneratedCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithAlreadyGeneratedSnippet("md1.md", "testSnippet", javaFileWithSnippet.snippet))
    }
}

fun aFileWithAMissingCodeSnippet(basePath: String): SetupUpdate {
    return aFileWithASingleCodeSnippet(basePath)
}

fun aFileWithMultipleCodeSnippetErrors(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithMultipleCodeSnippetErrors")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                javaFileWithADifferentSnippet,
                aMarkdownFileWithAlreadyGeneratedSnippetAndASecondNotGeneratedSnippet("md1.md",
                    "snippet1", javaFileWithSnippet.snippet,
                    "snippet2", javaFileWithADifferentSnippet.snippet))
    }
}

fun aFileWithASingleCodeSnippetAtTheEndOfFileWithoutNewLine(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "SnippetAtTheEndOfFileWithoutNewLine")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippetDirectiveAtTheEnd("md1.md", "testSnippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithAlreadyExistingCodeSnippetAtTheEndOfFileWithoutNewLine(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "ExistingSnippetAtTheEndOfFileWithoutNewLine")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithWrongGeneratedSnippetAtEndOfFile("md1.md", "testSnippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithTwoSnippetsWhereTheFirstOnesIdIsAPrefixForTheSecond(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTwoSnippetsWhereTheFirstOnesIdIsAPrefixForTheSecond")

    val javaFile = aJavaFileWithTwoSnippets("source1.java", "testSnippet", "testSnippet1")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithTwoSnippets("md1.md",
                    "testSnippet", javaFile.snippet1,
                    "testSnippet1", javaFile.snippet2)
            )
    }
}

fun aJavaFileWithSnippetWithSuppressWarning(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aJavaFileWithSnippetWithSuppressWarning")

    val javaFile = sampleJavaFileWithSnippetWithSuppressWarning("source1.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithSnippet("md1.md",
                    "testSnippet", javaFile.expectedStrippedSnippet)
            )
    }
}

fun aJavaFileWithSnippetWithAMultipleSuppressWarning(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aJavaFileWithSnippetWithAMultipleSuppressWarning")

    val javaFile = sampleJavaFileWithSnippetWithTwoSuppressWarning("source1.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithSnippet("md1.md",
                    "testSnippet", javaFile.expectedStrippedSnippet)
            )
    }
}

fun aJavaFileWithSnippetWithNoSonarComments(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aJavaFileWithSnippetWithNoSonarComments")

    val javaFile = sampleJavaFileWithSnippetWithNoSonarComments("source1.java", "testSnippet")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFile,
                aMarkdownFileWithSnippet("md1.md",
                    "testSnippet", javaFile.expectedStrippedSnippet)
            )
    }
}
