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

package de.quantummaid.documaid.tableOfContents

import de.quantummaid.documaid.config.Goal.GENERATE
import de.quantummaid.documaid.config.Goal.VALIDATE
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectTheTocToBeGenerated
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocInReadmeWithCorrectToc
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocInReadmeWithIncorrectToc
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocInReadmeWithMissingToc
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeFromSameDirectory
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithGeneratedOverviewFile
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithMalFormedOptions
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithMissingIndex
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithMissingIndexInSubDirectory
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithMultipleNestedDirectories
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithNotExistingScanDirectory
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithNotIndexedMarkdownFiles
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithTheSameIndexTwice
import de.quantummaid.documaid.tableOfContents.TocSampleFilesBuilder.Companion.aTocTagInReadmeWithTheSameIndexTwiceInSubDirectory
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class TableOfContentTest {

    @Test
    fun canGenerateTocForDocsDirectory() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeAndMultipleMarkdownFilesInDocsDirectory(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheTocToBeGenerated())
    }

    @Test
    fun canGenerateTocForSameDirectory() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeFromSameDirectory(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheTocToBeGenerated())
    }

    @Test
    fun canGenerateTocForMultipleNestedDirectories() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMultipleNestedDirectories(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheTocToBeGenerated())
    }

    @Test
    fun tocIgnoresNotIndexedFiles() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithNotIndexedMarkdownFiles(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheTocToBeGenerated())
    }

    @Test
    fun tocIgnoresGeneratedOverviewFilesWithIndex0() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithGeneratedOverviewFile(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectTheTocToBeGenerated())
    }

    @Test
    fun tocGenerationFailsForRedundantIndex() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithTheSameIndexTwice(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Same TOC indices[2] used by multiple files [2_One.md, 2_Two.md] " +
                "(in path ${absPath("tocWithSameIndexTwice")})"))
    }

    @Test
    fun tocGenerationFailsForMissingIndex() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMissingIndex(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Missing index 2 for TOC in directory 'tocWithMissingIndex' " +
                "(in path ${absPath("tocWithMissingIndex")})"))
    }

    @Test
    fun tocGenerationFailsForRedundantIndexInSubDirectory() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithTheSameIndexTwiceInSubDirectory(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Same TOC indices[2] used by multiple files [02_docs/2_Double.md, 02_docs/2_SecondDocs.md] " +
                "(in path ${absPath("tocWithSameIndexTwiceInSubDirectory/02_docs")})"))
    }

    @Test
    fun tocGenerationFailsForMissingIndexInSubDirectory() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMissingIndexInSubDirectory(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Missing index 2 for TOC in directory '02_docs' " +
                "(in path ${absPath("tocWithMissingIndexInSubDirectory/02_docs")})"))
    }

    @Test
    fun tocGenerationFailsForNotExistingScanDirectory() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithNotExistingScanDirectory(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Cannot create TOC for not existing directory 'notExisting/' " +
                "(in path ${absPath("aTocTagInReadmeWithNotExistingScanDirectory/README.md")})"))
    }

    @Test
    fun tocGenerationFailsForMalformedOptions() {
        given(aDokuMaid()
            .configuredWith(aTocTagInReadmeWithMalFormedOptions(BASE_PATH))
            .configuredWithGoal(GENERATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("[TOC] Could not match TOC options '(not a correct options String)' " +
                "(in path ${absPath("aTocTagInReadmeWithMalFormedOptions/README.md")})"))
    }

    @Test
    fun tocVerificationSucceedsForValidToc() {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithCorrectToc(BASE_PATH))
            .configuredWithGoal(VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun tocVerificationFailsForMissingToc() {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithMissingToc(BASE_PATH))
            .configuredWithGoal(VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [TOC] tag with missing TOC (in path ${absPath("aTocInReadmeWithMissingToc/README.md")})"))
    }

    @Test
    fun tocVerificationFailsForIncorrectToc() {
        given(aDokuMaid()
            .configuredWith(aTocInReadmeWithIncorrectToc(BASE_PATH))
            .configuredWithGoal(VALIDATE)
            .configuredWithBasePath(BASE_PATH))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [TOC] tag with incorrect TOC (in path ${absPath("aTocInReadmeWithIncorrectToc/README.md")})"))
    }

    companion object {
        private val BASE_PATH = Paths.get("src/test/kotlin/de/quantummaid/documaid/tableOfContents/")
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }
}
