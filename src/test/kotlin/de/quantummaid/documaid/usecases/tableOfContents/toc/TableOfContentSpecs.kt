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

package de.quantummaid.documaid.usecases.tableOfContents.toc

import de.quantummaid.documaid.config.Goal.GENERATE
import de.quantummaid.documaid.config.Goal.VALIDATE
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import java.nio.file.Paths

interface TableOfContentSpecs {

    @Test
    fun canGenerateTocForDocsDirectory(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateTocForSameDirectory(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeFromSameDirectory(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateTocForMultipleNestedDirectories(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMultipleNestedDirectories(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun tocIgnoresNotIndexedFiles(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithNotIndexedMarkdownFiles(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun tocIgnoresGeneratedOverviewFilesWithIndex0(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithGeneratedOverviewFile(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun tocGenerationFailsForRedundantIndex(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithTheSameIndexTwice(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Same TOC indices[2] used by multiple files [2_One.md, 2_Two.md] " +
                "(in path ${absPath("tocWithSameIndexTwice")})"))
    }

    @Test
    fun tocGenerationFailsForMissingIndex(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMissingIndex(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Missing index 2 for TOC in directory 'tocWithMissingIndex' " +
                "(in path ${absPath("tocWithMissingIndex")})"))
    }

    @Test
    fun tocGenerationFailsForRedundantIndexInSubDirectory(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithTheSameIndexTwiceInSubDirectory(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Same TOC indices[2] used by multiple files [02_docs/2_Double.md, 02_docs/2_SecondDocs.md] " +
                "(in path ${absPath("tocWithSameIndexTwiceInSubDirectory/02_docs")})"))
    }

    @Test
    fun tocGenerationFailsForMissingIndexInSubDirectory(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMissingIndexInSubDirectory(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Missing index 2 for TOC in directory '02_docs' " +
                "(in path ${absPath("tocWithMissingIndexInSubDirectory/02_docs")})"))
    }

    @Test
    fun tocGenerationFailsForNotExistingScanDirectory(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithNotExistingScanDirectory(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Cannot create TOC for not existing directory 'notExisting/' " +
                "(in path ${absPath("aTocTagInReadmeWithNotExistingScanDirectory/README.md")})"))
    }

    @Test
    fun tocGenerationFailsForMalformedOptions(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMalFormedOptions(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[$TOC_TAG] Could not match TOC options '(not a correct options String)' " +
                "(in path ${absPath("aTocTagInReadmeWithMalFormedOptions/README.md")})"))
    }

    @Test
    fun tocVerificationForValidToc(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithCorrectToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun tocVerificationWithMissingToc(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithMissingToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$TOC_TAG] tag with missing TOC (in path ${absPath("aTocInReadmeWithMissingToc/README.md")})"))
    }

    @Test
    fun tocVerificationFailsForIncorrectToc(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithIncorrectToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [$TOC_TAG] tag with incorrect TOC (in path ${absPath("aTocInReadmeWithIncorrectToc/README.md")})"))
    }

    @Test
    fun canGenerateTocAtTheEndOfFileWithoutNewLine(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aTocTagAtTheEndOfFileWithoutNewline(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceTocAtTheEndOfFileWithoutNewLine(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aWrongTocTagAtTheEndOfFileWithoutNewline(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    companion object {
        private val BASE_PATH = Paths.get("target/tempTestDirs/tableOfContents/")
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }
}
