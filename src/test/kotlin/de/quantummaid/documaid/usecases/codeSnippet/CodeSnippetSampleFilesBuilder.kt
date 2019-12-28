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

import de.quantummaid.documaid.Configurator
import de.quantummaid.documaid.config.DocuMaidConfigurationBuilder
import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.SampleFilesBuilder
import de.quantummaid.documaid.givenWhenThen.TestEnvironment
import de.quantummaid.documaid.shared.TestDirectoryBuilder
import de.quantummaid.documaid.shared.TestFileBuilder
import de.quantummaid.documaid.shared.TestStructureBuilder
import java.nio.file.Paths

class CodeSnippetSampleFilesBuilder internal constructor(private val sampleFile: SampleFile) : SampleFilesBuilder {
    override fun build(): SampleFile {
        return sampleFile
    }
}

fun aFileWithASingleCodeSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(singleCodeSnippetSampleFiles())
}

fun aFileWithATwoCodeSnippets(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(twoCodeSnippetsSampleFiles())
}

fun aFileWithTheSameCodeSnippetTwice(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(sameCodeSnippetTwice())
}

fun aFileWithACodeSnippetFromANonJavaFile(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(nonJavaCodeSnippet())
}

fun aFileWithCommentsInCodeSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(snippetWithComments())
}

fun aFileWithAFullClassSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(fullClassSnippet())
}

fun aFileWithIncorrectlyGeneratedCode(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(incorrectlyGeneratedCodeSnippet())
}

fun aCorrectlyGeneratedFileWithOneCodeSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(correctlyGeneratedFileWithOneCodeSnippet())
}

fun aFileWithTwoOutdatedCodeSnippets(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(aFileWithTwoOutdatedCodeSnippet())
}

fun aFileWithTheWrongCodeSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(wrongCodeSnippet())
}

fun aFileWithAMissingCodeSnippet(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(missingCodeSnippet())
}

fun aFileWithMultipleCodeSnippetErrors(): CodeSnippetSampleFilesBuilder {
    return CodeSnippetSampleFilesBuilder(multipleCodeSnippetErrors())
}

fun filesWithDuplicateCodeSnippets(basePath: String): Configurator {
    val testIsolatedDir = "duplicateSnippetsDirectory"
    val baseDir = Paths.get(basePath, testIsolatedDir)

    return object : Configurator {
        override fun invoke(
            testEnvironment: TestEnvironment,
            configurationBuilder: DocuMaidConfigurationBuilder,
            setupSteps: MutableCollection<() -> Unit>,
            cleanupSteps: MutableCollection<() -> Unit>
        ) {
            setupSteps.add {
                val fileStructure = TestStructureBuilder.aTestStructureIn(baseDir)
                    .with(
                        aJavaFileWithSnippetId("snippetDuplicate1.java", "doubleDuplicate"),
                        aXmlFileWithSnippetId("snippetDuplicate2.xml", "doubleDuplicate"),
                        aJavaFileWithSnippetId("snippetTriplicate1.java", "tripleDuplicate"),
                        TestDirectoryBuilder.aDirectory("subDir")
                            .with(
                                aJavaFileWithSnippetId("snippetTriplicate2.java", "tripleDuplicate"),
                                aXmlFileWithSnippetId("snippetTriplicate3.xml", "tripleDuplicate")
                            )
                    )
                    .build()
                cleanupSteps.add { fileStructure.cleanUp() }
            }
        }
    }
}

fun aJavaFileWithSnippetId(fileName: String, snippetId: String): TestFileBuilder {
    return TestFileBuilder
        .aFile(fileName)
        .withContent("""
            //Showcase start $snippetId
            public class SampleCodeSnippets {}
            //Showcase end $snippetId

            """.trimIndent())
}

fun aXmlFileWithSnippetId(fileName: String, snippetId: String): TestFileBuilder {
    return TestFileBuilder
        .aFile(fileName)
        .withContent("""
            <!-- Showcase start $snippetId -->
            <value>SomeValue</value>
            <!-- Showcase end $snippetId -->

            """.trimIndent())
}

fun generalJavaSampleFile(): TestFileBuilder {
    return TestFileBuilder
        .aFile("sampleCodeSnippets.java")
        .withContent("""
public class SampleCodeSnippets {

    public void firstSnippet() {
        //Showcase start first
        final List<String> strings = new ArrayList<>();
        strings.add("A");
        strings.add("B");
        strings.remove(1);
        //Showcase end first
    }

    public void secondSnippet() {
        //Showcase start second
        if (Math.random() % 2 == 0) {
            System.out.println("Success");
        } else {
            System.out.println("Nope");
        }
        //Showcase end second
    }

    public void thirdSnippet() {
        //Showcase start third
        final Object o = new Object();//our first object

        // we create a     second    object
        final Object o2 = new Object();
        /*
        and no we check on equality
         */
        o.equals(o2);
        //Showcase end third
    }
}
        """.trimIndent())
}
