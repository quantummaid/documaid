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

package de.quantummaid.documaid.codeSnippet

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectADokuMaidExceptionCollectingTheFollowingErrors
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllCodeSnippetsToBeInserted
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectTheCodeSnippetToBeInserted
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectTheCorrectFileToBeGenerated
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class DocuMaidCodeSnippetTest {

    @Test
    fun canInsertSimpleCodeSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleCodeSnippet())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheCodeSnippetToBeInserted())
    }

    @Test
    fun canInsertTwoSnippets() {
        given(aDokuMaid()
            .configuredWith(aFileWithATwoCodeSnippets())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllCodeSnippetsToBeInserted())
    }

    @Test
    fun canInsertTheSameSnippetsTwice() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameCodeSnippetTwice())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllCodeSnippetsToBeInserted())
    }

    @Test
    fun canInsertACodeSnippetFromANonJavaFile() {
        given(aDokuMaid()
            .configuredWith(aFileWithACodeSnippetFromANonJavaFile())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheCodeSnippetToBeInserted())
    }

    @Test
    fun canInsertACodeSnippetWithComments() {
        given(aDokuMaid()
            .configuredWith(aFileWithCommentsInCodeSnippet())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheCodeSnippetToBeInserted())
    }

    @Test
    fun canInsertACodeSnippetForAFullClass() {
        given(aDokuMaid()
            .configuredWith(aFileWithAFullClassSnippet())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheCodeSnippetToBeInserted())
    }

    @Test
    fun canReplaceAWrongCodeSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithIncorrectlyGeneratedCode())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllCodeSnippetsToBeInserted())
    }

    @Test
    fun canReplaceTwoSnippets() {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoOutdatedCodeSnippets())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheCorrectFileToBeGenerated())
    }

    @Test
    fun failsForDuplicateSnippet() {
        given(aDokuMaid()
            .configuredWith(filesWithDuplicateCodeSnippets(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH + "duplicateSnippetsDirectory"))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'doubleDuplicate': " +
                    "${absBasePath()}/duplicateSnippetsDirectory/snippetDuplicate1.java, ${absBasePath()}/duplicateSnippetsDirectory/snippetDuplicate2.xml",
                "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet 'tripleDuplicate': " +
                    "${absBasePath()}/duplicateSnippetsDirectory/snippetTriplicate1.java, ${absBasePath()}/duplicateSnippetsDirectory/subDir/snippetTriplicate2.java, " +
                    "${absBasePath()}/duplicateSnippetsDirectory/subDir/snippetTriplicate3.xml"
            ))
    }

    @Test
    fun canValidateCorrectSnippets() {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithOneCodeSnippet())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun failsForWrongSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheWrongCodeSnippet())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$CODE_SNIPPET_TAG] tag with incorrect code for '<!---[$CODE_SNIPPET_TAG] (first)-->'"))
    }

    @Test
    fun failsForMissingSnippet() {
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingCodeSnippet())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '<!---[$CODE_SNIPPET_TAG] (notExisting)-->'"))
    }

    @Test
    fun canCaptureMultipleErrors() {
        given(aDokuMaid()
            .configuredWith(aFileWithMultipleCodeSnippetErrors())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [CodeSnippet] tag with incorrect code for '<!---[CodeSnippet] (first)-->'",
                "Found [CodeSnippet] tag with missing snippet for '<!---[CodeSnippet] (first)-->'"
            ))
    }

    companion object {
        private const val BASE_PATH = "src/test/kotlin/de/quantummaid/documaid/codeSnippet/"
    }

    fun absBasePath(): String {
        return Paths.get(BASE_PATH).toAbsolutePath().toString()
    }
}
