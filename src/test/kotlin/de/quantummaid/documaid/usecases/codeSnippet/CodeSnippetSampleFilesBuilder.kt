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

package de.quantummaid.documaid.usecases.codeSnippet

import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.SampleFilesBuilder
import de.quantummaid.documaid.shared.SampleXmlFileWithOneSnippet.Companion.aXmlFileWithOneSnippet
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.SutDirectory
import de.quantummaid.documaid.shared.TemporaryTestDirectory
import de.quantummaid.documaid.shared.aMarkdownFileWithAlreadyGeneratedSnippet
import de.quantummaid.documaid.shared.aMarkdownFileWithAlreadyGeneratedSnippetAndASecondNotGeneratedSnippet
import de.quantummaid.documaid.shared.aMarkdownFileWithFillClassSnippetDirective
import de.quantummaid.documaid.shared.aMarkdownFileWithSnippetDirective
import de.quantummaid.documaid.shared.aMarkdownFileWithTwoSnippets
import de.quantummaid.documaid.shared.aMarkdownFileWithTwoWrongSnippets
import de.quantummaid.documaid.shared.aMarkdownFileWithWrongSnippet
import de.quantummaid.documaid.usecases.codeSnippet.SampleJavaFileWithACommentsInSnippet.Companion.aJavaFileWithACommentsInSnippet
import de.quantummaid.documaid.usecases.codeSnippet.SampleJavaFileWithADifferentSnippet.Companion.aJavaFileWithADifferentSnippet
import de.quantummaid.documaid.usecases.codeSnippet.SampleJavaFileWithOneSnippet.Companion.aJavaFileWithOneSnippet

class CodeSnippetSampleFilesBuilder internal constructor(private val sampleFile: SampleFile) : SampleFilesBuilder {
    override fun build(): SampleFile {
        return sampleFile
    }
}

fun aFileWithASingleCodeSnippet(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithASingleCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippetDirective("md1.md", "testSnippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithATwoCodeSnippets(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithATwoCodeSnippets")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, _, sutFileStructure, _, _) ->
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
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithTheSameCodeSnippetTwice")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithTwoSnippets("md1.md",
                    "snippet", javaFileWithSnippet.snippet,
                    "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithTheSameCodeSnippetTwiceInDifferentFiles(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithTheSameCodeSnippetTwiceInDifferentFiles")

    val javaFileWithSnippet = aJavaFileWithADifferentSnippet("source.java", "snippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippetDirective("md1.md", "snippet", javaFileWithSnippet.snippet),
                aMarkdownFileWithSnippetDirective("md2.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithACodeSnippetFromANonJavaFile(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithACodeSnippetFromANonJavaFile")

    val xmlFileWithSnippet = aXmlFileWithOneSnippet("source.xml", "snippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                xmlFileWithSnippet,
                aMarkdownFileWithSnippetDirective("md1.md", "snippet", xmlFileWithSnippet.snippet, "xml")
            )
    }
}

fun aFileWithCommentsInCodeSnippet(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithCommentsInCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithACommentsInSnippet("source.java", "snippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithSnippetDirective("md1.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithAFullClassSnippet(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithAFullClassSnippet")

    val expectedSnippetContent = "package de.quantummaid.documaid.usecases.codeSnippet;\n" +
        "\n" +
        "public class FullClassCodeSnippet {\n" +
        "\n" +
        "    public static void main(String[] args) {\n" +
        "        Object o = new Object();\n" +
        "        log(o);\n" +
        "    }\n" +
        "\n" +
        "    private static void log(Object o) {\n" +
        "\n" +
        "    }\n" +
        "}\n"
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithFillClassSnippetDirective("md1.md", "../FullClassCodeSnippet.java", expectedSnippetContent)
            )
    }
}

fun aFileWithWrongCodeSnippetPresent(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithWrongCodeSnippetPresent")

    val javaFileWithSnippet = aJavaFileWithADifferentSnippet("source.java", "snippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithWrongSnippet("wrongCodeSnippet.md", "snippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithTwoOutdatedCodeSnippets(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithTwoOutdatedCodeSnippets")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, _, sutFileStructure, _, _) ->
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
    val testDir = TemporaryTestDirectory.createWithName(basePath, "duplicateSnippetsDirectory")

    return { (_, _, sutFileStructure, _, _) ->
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
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aCorrectlyGeneratedFileWithOneCodeSnippet")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("source.java", "testSnippet")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                aMarkdownFileWithAlreadyGeneratedSnippet("md1.md", "testSnippet", javaFileWithSnippet.snippet)
            )
    }
}

fun aFileWithAMissingCodeSnippet(basePath: String): SetupUpdate {
    return aFileWithASingleCodeSnippet(basePath)
}

fun aFileWithMultipleCodeSnippetErrors(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.createWithName(basePath, "aFileWithMultipleCodeSnippetErrors")

    val javaFileWithSnippet = aJavaFileWithOneSnippet("sourceA.java", "snippet1")
    val javaFileWithADifferentSnippet = aJavaFileWithADifferentSnippet("sourceB.java", "snippet2")
    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                javaFileWithSnippet,
                javaFileWithADifferentSnippet,
                aMarkdownFileWithAlreadyGeneratedSnippetAndASecondNotGeneratedSnippet("md1.md",
                    "snippet1", javaFileWithSnippet.snippet,
                    "snippet2", javaFileWithADifferentSnippet.snippet)
            )
    }
}

