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

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectADokuMaidExceptionCollectingTheFollowingErrors
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class LinkSpecs {

    @Test
    fun canInsertSimpleCodeLinks() {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aFileWithASingleLink(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTwoLinks() {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoLinks(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canInsertTheSameLinkTwice() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameLinksTwice(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceAWrongLink() {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongLink(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForSeveralNotExistingLinkTarget() {
        val testBasePath = absPath("aFileWithLinksToNotExistingFiles")
        given(aDokuMaid()
            .configuredWith(aFileWithLinksToNotExistingFiles(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [Link] tag to not existing file '$testBasePath/someWhere/notExistingFile.java' (in path $testBasePath/missingLinksFile.md)",
                "Found [Link] tag to not existing file '$testBasePath/differentNotExistingFile.java' (in path $testBasePath/missingLinksFile.md)"
            ))
    }

    @Test
    fun canValidateCorrectLinks() {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithTwoLinks(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForMissingLink() {
        val testBasePath = absPath("aFileWithAMissingLink")
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingLink(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag without link being set for '<!---[Link] ( source.java linkName)-->' " +
                "(in path $testBasePath/oneMissingLinkFileSampleFiles.md)"))
    }

    @Test
    fun failsForWrongLink() {
        val testBasePath = absPath("aFileWithWrongLink")
        given(aDokuMaid()
            .configuredWith(aFileWithWrongLink(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag with wrong link being set: '<!---[Link] ( source.java linkName)-->' " +
                "(in path $testBasePath/md1.md)"))
    }

    @Test
    fun failsIfLinkDoesNotPointToAExistingFileAnymore() {
        val testBasePath = absPath("aFileWithLinkANotExistingFile")
        given(aDokuMaid()
            .configuredWith(aFileWithLinkANotExistingFile(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag to not existing file '$testBasePath/someWhere/notExistingFile.java' " +
                "(in path $testBasePath/aLinkToANotExistingFile.md)"))
    }

    @Test
    fun capturesMultipleErrors() {
        val testBasePath = absPath("aFileWithMultipleLinkErrors")
        given(aDokuMaid()
            .configuredWith(aFileWithMultipleLinkErrors(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [Link] tag with wrong link being set: '<!---[Link] ( source.java linkName1)-->' " +
                    "(in path $testBasePath/multipleLinkErrors.md)",
                "Found [Link] tag without link being set for '<!---[Link] ( source.java linkName2)-->' " +
                    "(in path $testBasePath/multipleLinkErrors.md)"))
    }

    @Test
    fun canInsertCodeLinkAtTheEndOfFileWithoutNewline() {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aFileWithASingleLinkAtTheEndOfFileWithoutNewline(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceCodeLinkAtTheEndOfFileWithoutNewline() {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aFileWithWrongLinkAtTheEndOfFileWithoutNewline(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }


    companion object {
        private const val BASE_PATH = "target/tempTestDirs/link/"
    }

    private fun absPath(fileName: String): String {
        return Paths.get("", BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
