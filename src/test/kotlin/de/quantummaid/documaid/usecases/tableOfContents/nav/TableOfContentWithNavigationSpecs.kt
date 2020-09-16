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
package de.quantummaid.documaid.usecases.tableOfContents.nav

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.Goal.GENERATE
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.NavigationDirective.Companion.NAV_TAG
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import java.nio.file.Paths

interface TableOfContentWithNavigationSpecs {

    @Test
    fun canGenerateNavigationForIndexFiles(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aTocTagInReadmeAndMultipleMarkdownFilesWithNavigationDirectives(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateNavigationForDeeplyNestedStructure(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aTocTagInReadmeWithADeeplyNestedStructure(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun navigationFailsForFileWithMissingNavigation(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aTocInReadmeWithAnIndexFileWithoutNavigation(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(
                expectAnExceptionWithMessage(
                    "Found file indexed by table of contents but without [$NAV_TAG] tag " +
                        "(in path ${absPath("fileWithoutNav/1_error.md")})"
                )
            )
    }

    @Test
    fun navigationFailsForFileWithNavigationButNotIncludedInTableOfContents(
        platformConfiguration: PlatformConfiguration
    ) {
        given(
            aDokuMaid()
                .configuredWith(aTocInReadmeWithFileWithNavigationButNotIndexedByToc(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(
                expectAnExceptionWithMessage(
                    "Found [$NAV_TAG] tag for file not indexed by table of contents " +
                        "(in path ${absPath("fileOutsideOfToc/1_error.md")})"
                )
            )
    }

    @Test
    fun navigationFailsForFileWithNavigationWithoutATableOfContents(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aFileWithNavigationButNoFileWithToc(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$NAV_TAG] tags without a [$TOC_TAG]"))
    }

    @Test
    fun navigationCanGenerateCorrectNavOverWrongNavigation(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aReadmeWithTocAndAFileWithWrongNav(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(Goal.GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun navigationValidationWithCorrectNavigations(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aReadmeWithTocAndSeveralFilesWithCorrectNavigation(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(Goal.VALIDATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun navigationValidationForMissingNavigation(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aReadmeWithTocAndAFileWithMissingNav(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(Goal.VALIDATE)
        )
            .`when`(theDokuIsPimped())
            .then(
                expectAnExceptionWithMessage(
                    "Found [$NAV_TAG] tag with missing navigation " +
                        "(in path ${absPath("missingNav/docs/02_SomeImportantStuff.md")})"
                )
            )
    }

    @Test
    fun navigationValidationForWrongNavigation(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aReadmeWithTocAndAFileWithWrongNav(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(Goal.VALIDATE)
        )
            .`when`(theDokuIsPimped())
            .then(
                expectAnExceptionWithMessage(
                    "Found [$NAV_TAG] tag with wrong navigation " +
                        "(in path ${absPath("wrongNav/docs/003_ADifferentChapter.md")})"
                )
            )
    }

    @Test
    fun canGenerateNavigationAtTheEndOfFileWithoutNewLine(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aTocTagInReadmeAndNavigationTagAtEndOfFileWithoutNewLine(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceNavigationAtTheEndOfFileWithoutNewLine(platformConfiguration: PlatformConfiguration) {
        given(
            aDokuMaid()
                .configuredWith(aTocTagInReadmeAndWrongNavigationTagAtEndOfFileWithoutNewLine(BASE_PATH))
                .configuredwith(platformConfiguration)
                .configuredWithGoal(GENERATE)
        )
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }

    companion object {
        private val BASE_PATH = Paths.get("target/tempTestDirs/nav/")
    }
}
