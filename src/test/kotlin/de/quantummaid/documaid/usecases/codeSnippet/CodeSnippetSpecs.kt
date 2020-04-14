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

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.tagBased.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectADocuMaidExceptionCollectingTheFollowingErrors
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import java.nio.file.Paths

interface CodeSnippetSpecs {

    @Test
    fun canInsertSimpleCodeSnippet(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleCodeSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTwoSnippets(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithATwoCodeSnippets(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsTwice(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameCodeSnippetTwice(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsInMultipleFiles(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(filesWithTheSameCodeSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameSnippetsTwiceInDifferentFiles(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameCodeSnippetTwiceInDifferentFiles(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetFromANonJavaFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithACodeSnippetFromANonJavaFile(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetWithComments(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithCommentsInCodeSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertACodeSnippetForAFullClass(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAFullClassSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceAWrongCodeSnippet(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongCodeSnippetPresent(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceTwoSnippets(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoOutdatedCodeSnippets(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForDuplicateSnippet(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(filesWithDuplicateCodeSnippets(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectADocuMaidExceptionCollectingTheFollowingErrors(
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'doubleDuplicate': " +
                    "${absPath("duplicateSnippetsDirectory/snippetDuplicate1.java")}, " +
                    absPath("duplicateSnippetsDirectory/snippetDuplicate2.xml"),
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'tripleDuplicate': " +
                    "${absPath("duplicateSnippetsDirectory/snippetTriplicate1.java")}, " +
                    "${absPath("duplicateSnippetsDirectory/subDir/snippetTriplicate2.java")}, " +
                    absPath("duplicateSnippetsDirectory/subDir/snippetTriplicate3.xml")
            ))
    }

    @Test
    fun canValidateCorrectSnippets(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithOneCodeSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun failsForWrongSnippet(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongCodeSnippetPresent(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage(
                "Found [$CODE_SNIPPET_TAG] tag with incorrect code for " +
                    "'<!---[$CODE_SNIPPET_TAG] (snippet)-->' " +
                    "(in path ${absPath("aFileWithWrongCodeSnippetPresent/wrongCodeSnippet.md")})"))
    }

    @Test
    fun failsForMissingSnippet(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingCodeSnippet(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage(
                "Found [$CODE_SNIPPET_TAG] tag with missing snippet for " +
                    "'<!---[$CODE_SNIPPET_TAG] (testSnippet)-->' " +
                "(in path ${absPath("aFileWithASingleCodeSnippet/md1.md")})"))
    }

    @Test
    fun canCaptureMultipleErrors(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithMultipleCodeSnippetErrors(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectADocuMaidExceptionCollectingTheFollowingErrors(
                "Found [CodeSnippet] tag with incorrect code for '<!---[CodeSnippet] (snippet1)-->'" +
                    " (in path ${absPath("aFileWithMultipleCodeSnippetErrors/md1.md")})",
                "Found [CodeSnippet] tag with missing snippet for '<!---[CodeSnippet] (snippet2)-->' " +
                    "(in path ${absPath("aFileWithMultipleCodeSnippetErrors/md1.md")})"
            ))
    }

    @Test
    fun canReplaceSimpleCodeSnippetAtTheEndOfFileWithoutNewline(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleCodeSnippetAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceCodeSnippetAtTheEndOfFileWithoutNewline(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAlreadyExistingCodeSnippetAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun testThatASnippetDoesNotSwallowSubsequentSnippetsWithAnIdTheFirstIdIsAPrefix(
        platformConfiguration: PlatformConfiguration
    ) {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoSnippetsWhereTheFirstOnesIdIsAPrefixForTheSecond(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
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
