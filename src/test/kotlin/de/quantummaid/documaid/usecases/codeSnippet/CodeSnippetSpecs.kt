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

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectADokuMaidExceptionCollectingTheFollowingErrors
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class CodeSnippetSpecs {

    @Test
    fun canInsertSimpleCodeSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleCodeSnippet(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTwoSnippets() {
        given(aDokuMaid()
            .configuredWith(aFileWithATwoCodeSnippets(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsTwice() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameCodeSnippetTwice(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsInMultipleFiles() {
        given(aDokuMaid()
            .configuredWith(filesWithTheSameCodeSnippet(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsTwiceInDifferentFiles() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameCodeSnippetTwiceInDifferentFiles(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetFromANonJavaFile() {
        given(aDokuMaid()
            .configuredWith(aFileWithACodeSnippetFromANonJavaFile(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetWithComments() {
        given(aDokuMaid()
            .configuredWith(aFileWithCommentsInCodeSnippet(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetForAFullClass() {
        given(aDokuMaid()
            .configuredWith(aFileWithAFullClassSnippet(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceAWrongCodeSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongCodeSnippetPresent(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceTwoSnippets() {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoOutdatedCodeSnippets(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForDuplicateSnippet() {
        given(aDokuMaid()
            .configuredWith(filesWithDuplicateCodeSnippets(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'doubleDuplicate': " +
                    "${absPath("duplicateSnippetsDirectory/snippetDuplicate1.java")}, ${absPath("duplicateSnippetsDirectory/snippetDuplicate2.xml")}",
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'tripleDuplicate': " +
                    "${absPath("duplicateSnippetsDirectory/snippetTriplicate1.java")}, ${absPath("duplicateSnippetsDirectory/subDir/snippetTriplicate2.java")}, " +
                    absPath("duplicateSnippetsDirectory/subDir/snippetTriplicate3.xml")
            ))
    }

    @Test
    fun canValidateCorrectSnippets() {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithOneCodeSnippet(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForWrongSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongCodeSnippetPresent(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$CODE_SNIPPET_TAG] tag with incorrect code for '<!---[$CODE_SNIPPET_TAG] (snippet)-->' " +
                "(in path ${absPath("aFileWithWrongCodeSnippetPresent/wrongCodeSnippet.md")})"))
    }

    @Test
    fun failsForMissingSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingCodeSnippet(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '<!---[$CODE_SNIPPET_TAG] (testSnippet)-->' " +
                "(in path ${absPath("aFileWithASingleCodeSnippet/md1.md")})"))
    }

    @Test
    fun canCaptureMultipleErrors() {
        given(aDokuMaid()
            .configuredWith(aFileWithMultipleCodeSnippetErrors(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [CodeSnippet] tag with incorrect code for '<!---[CodeSnippet] (snippet1)-->' (in path ${absPath("aFileWithMultipleCodeSnippetErrors/md1.md")})",
                "Found [CodeSnippet] tag with missing snippet for '<!---[CodeSnippet] (snippet2)-->' (in path ${absPath("aFileWithMultipleCodeSnippetErrors/md1.md")})"
            ))
    }

    @Test
    fun canReplaceSimpleCodeSnippetAtTheEndOfFileWithoutNewline() {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleCodeSnippetAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceCodeSnippetAtTheEndOfFileWithoutNewline() {
        given(aDokuMaid()
            .configuredWith(aFileWithAlreadyExistingCodeSnippetAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/codeSnippet/"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
