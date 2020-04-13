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

package de.quantummaid.documaid.usecases.hugo.heading

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class HugoHeadingSpecs {

    @Test
    fun canGenerateH1Heading() {
        given(aDokuMaid()
            .configuredWith(aFileWithH1Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canIgnoreOtherHeadings() {
        given(aDokuMaid()
            .configuredWith(aFileWithH2Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceHeadingsInMultipleFiles() {
        given(aDokuMaid()
            .configuredWith(multipleFilesWithHeadings(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun ignoresHeadingWhenTextOccursBefore() {
        given(aDokuMaid()
            .configuredWith(aFileWithTextBeforeHeading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsIfFileHasNoIndex() {
        given(aDokuMaid()
            .configuredWith(aFileWithNoIndex(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Cannot extract index from file ${absPath("aFileWithNoIndex/Introduction.md")}"))
    }

    @Test
    fun succeedsForValidH1Heading() {
        given(aDokuMaid()
            .configuredWith(aFileWithExistingH1Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun succeedsForNoHeading() {
        given(aDokuMaid()
            .configuredWith(aFileWithH2Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/hugoHeading/"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
