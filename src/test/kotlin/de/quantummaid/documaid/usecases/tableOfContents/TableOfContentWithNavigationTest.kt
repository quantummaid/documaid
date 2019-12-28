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

package de.quantummaid.documaid.usecases.tableOfContents

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.Goal.GENERATE
import de.quantummaid.documaid.domain.markdown.navigation.NavigationDirective.Companion.NAV_TAG
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectTheTocAndAllNavigationDirectivesToBeInserted
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aFileWithNavigationButNotFileWithToc
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aReadmeWithTocAndAFileWithMissingNav
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aReadmeWithTocAndAFileWithWrongNav
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aReadmeWithTocAndSeveralFilesWithCorrectNavigation
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aTocInReadmeWithAnIndexFileWithoutNavigation
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aTocInReadmeWithFileWithNavigationButNotIndexedByToc
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aTocTagInReadmeAndMultipleMarkdownFilesWithNavigationDirectives
import de.quantummaid.documaid.usecases.tableOfContents.NavigationSampleFilesBuilder.Companion.aTocTagInReadmeWithADeeplyNestedStructure
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class TableOfContentWithNavigationTest {

    @Test
    fun canGenerateNavigationForIndexFiles() {
        given(aDokuMaid()
                .configuredWith(aTocTagInReadmeAndMultipleMarkdownFilesWithNavigationDirectives(BASE_PATH))
                .configuredWithGoal(GENERATE))
                .`when`(theDokuIsPimped())
                .then(expectTheTocAndAllNavigationDirectivesToBeInserted())
    }

    @Test
    fun canGenerateNavigationForDeeplyNestedStructure() {
        given(aDokuMaid()
                .configuredWith(aTocTagInReadmeWithADeeplyNestedStructure(BASE_PATH))
                .configuredWithGoal(GENERATE))
                .`when`(theDokuIsPimped())
                .then(expectTheTocAndAllNavigationDirectivesToBeInserted())
    }

    @Test
    fun navigationFailsForFileWithMissingNavigation() {
        given(aDokuMaid()
                .configuredWith(aTocInReadmeWithAnIndexFileWithoutNavigation(BASE_PATH))
                .configuredWithGoal(GENERATE))
                .`when`(theDokuIsPimped())
                .then(expectAnExceptionWithMessage("Found file indexed by table of contents but without [$NAV_TAG] tag " +
                    "(in path ${absPath("fileWithoutNav/1_error.md")})"))
    }

    @Test
    fun navigationFailsForFileWithNavigationButNotIncludedInTableOfContents() {
        given(aDokuMaid()
                .configuredWith(aTocInReadmeWithFileWithNavigationButNotIndexedByToc(BASE_PATH))
                .configuredWithGoal(GENERATE))
                .`when`(theDokuIsPimped())
                .then(expectAnExceptionWithMessage("Found [$NAV_TAG] tag for file not indexed by table of contents " +
                    "(in path ${absPath("fileOutsideOfToc/1_error.md")})"))
    }

    @Test
    fun navigationFailsForFileWithNavigationWithoutATableOfContents() {
        given(aDokuMaid()
                .configuredWith(aFileWithNavigationButNotFileWithToc(BASE_PATH))
                .configuredWithGoal(GENERATE))
                .`when`(theDokuIsPimped())
                .then(expectAnExceptionWithMessage("Found [$NAV_TAG] tags without a [$TOC_TAG]"))
    }

    @Test
    fun navigationValidationSucceedsForCorrectNavigations() {
        given(aDokuMaid()
                .configuredWith(aReadmeWithTocAndSeveralFilesWithCorrectNavigation(BASE_PATH))
                .configuredWithGoal(Goal.VALIDATE))
                .`when`(theDokuIsPimped())
                .then(expectNoException())
    }

    @Test
    fun navigationValidationFailsForMissingNavigation() {
        given(aDokuMaid()
                .configuredWith(aReadmeWithTocAndAFileWithMissingNav(BASE_PATH))
                .configuredWithGoal(Goal.VALIDATE))
                .`when`(theDokuIsPimped())
                .then(expectAnExceptionWithMessage("Found [$NAV_TAG] tag with missing navigation " +
                    "(in path ${absPath("missingNav/docs/02_SomeImportantStuff.md")})"))
    }

    @Test
    fun navigationValidationFailsForWrongNavigation() {
        given(aDokuMaid()
                .configuredWith(aReadmeWithTocAndAFileWithWrongNav(BASE_PATH))
                .configuredWithGoal(Goal.VALIDATE))
                .`when`(theDokuIsPimped())
                .then(expectAnExceptionWithMessage("Found [$NAV_TAG] tag with wrong navigation (in path ${absPath("wrongNav/docs/1_Introduction.md")})"))
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }

    companion object {
        private val BASE_PATH = Paths.get("src/test/kotlin/de/quantummaid/documaid/tableOfContents/")
    }
}
