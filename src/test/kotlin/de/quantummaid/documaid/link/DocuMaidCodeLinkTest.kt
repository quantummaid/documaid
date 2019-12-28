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

package de.quantummaid.documaid.link

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectADokuMaidExceptionCollectingTheFollowingErrors
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllLinksToBeInserted
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectTheLinkToBeInserted
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aCorrectlyGeneratedFileWithTwoLinks
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithALink
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithALinkToANotExistingFile
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithAMissingLink
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithAWrongLink
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithMultipleLinkErrors
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithTheLinksToMissingFiles
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithTheSameLinksTwice
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithTwoLinks
import de.quantummaid.documaid.link.LinkSampleFilesBuilder.Companion.aFileWithWrongLink
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class DocuMaidCodeLinkTest {

    @Test
    fun canInsertSimpleCodeLinks() {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aFileWithALink())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheLinkToBeInserted())
    }

    @Test
    fun canInsertTwoLinks() {
        given(aDokuMaid()
            .configuredWith(aFileWithTwoLinks())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllLinksToBeInserted())
    }

    @Test
    fun canInsertTheSameLinkTwice() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheSameLinksTwice())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllLinksToBeInserted())
    }

    @Test
    fun canReplaceAWrongLink() {
        given(aDokuMaid()
            .configuredWith(aFileWithWrongLink())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAllLinksToBeInserted())
    }

    @Test
    fun failsForSeveralNotExistingLinkTarget() {
        given(aDokuMaid()
            .configuredWith(aFileWithTheLinksToMissingFiles())
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [Link] tag to not existing file '" + ABS_BASE_PATH + "someWhere/notExistingFile.java' (in path ${absPath("missingLinksFile.md")})",
                "Found [Link] tag to not existing file '" + ABS_BASE_PATH + "differentNotExistingFile.java' (in path ${absPath("missingLinksFile.md")})"
            ))
    }

    @Test
    fun canValidateCorrectLinks() {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithTwoLinks())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun failsForMissingLink() {
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingLink())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag without link being set for '<!---[Link] ( ./ReferencedCodeFile.java name)-->' " +
                "(in path ${absPath("oneMissingLinkFileSampleFiles.md")})"))
    }

    @Test
    fun failsForWrongLink() {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongLink())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag with wrong link being set: '<!---[Link] ( ./ReferencedCodeFile.java \"name\")-->' " +
                "(in path ${absPath("wrongLinkFileSampleFiles.md")})"))
    }

    @Test
    fun failsIfLinkDoesNotPointToAExistingFileAnymore() {
        given(aDokuMaid()
            .configuredWith(aFileWithALinkToANotExistingFile())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [Link] tag to not existing file '" + ABS_BASE_PATH + "someWhere/notExistingFile.java' " +
                "(in path ${absPath("aLinkToANotExistingFile.md")})"))
    }

    @Test
    fun capturesMultipleErrors() {
        given(aDokuMaid()
            .configuredWith(aFileWithMultipleLinkErrors())
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectADokuMaidExceptionCollectingTheFollowingErrors(
                "Found [Link] tag with wrong link being set: '<!---[Link] ( ./ReferencedCodeFile.java name)-->' " +
                    "(in path ${absPath("multipleLinkErrors.md")})",
                "Found [Link] tag without link being set for '<!---[Link] ( ./ReferencedCodeFile.java name)-->' " +
                    "(in path ${absPath("multipleLinkErrors.md")})"))
    }

    companion object {
        private const val BASE_PATH = "src/test/kotlin/de/quantummaid/documaid/link/"
        private val ABS_BASE_PATH = Paths.get("", BASE_PATH).toAbsolutePath().toString() + "/"
    }

    private fun absPath(fileName: String): String {
        return Paths.get("", BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
