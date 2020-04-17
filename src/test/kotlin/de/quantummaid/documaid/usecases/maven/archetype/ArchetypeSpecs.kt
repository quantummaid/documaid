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
package de.quantummaid.documaid.usecases.maven.archetype

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.tagBased.archetype.ArchetypeDirective.Companion.ARCHETYPE_TAG
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DocuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import java.nio.file.Paths

interface ArchetypeSpecs {

    @Test
    fun canGenerateFullyDefinedArchetypeInMarkdownFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleFullyDefinedArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateMinimalDefinedArchetypeInMarkdownFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleMinimalDefinedArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateArchetypeAlsoForWindows(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAnArchetypeForWindows(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateFullyDefinedArchetypeAtEndOfMarkdownFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAnArchetypeAtEndOfFile(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateOverWrongArchetype(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateOverWrongArchetypeAtEndOfMarkdownFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongArchetypeAtEndOfFile(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canValidateCorrectArchetype(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAlreadyGeneratedArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canValidateCorrectArchetypeAtEndOfFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAlreadyGeneratedArchetypeAtEndOfFile(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForMissingArchetype(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAMissingArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage(
                "Found [${ARCHETYPE_TAG.value}] tag with missing archetype code for " +
                    "'<!---[Archetype](archetypeGroupId archetypeArtifactId archetypeVersion " +
                    "groupId=local artifactId=test version=1.0.0 packaging=java )-->' " +
                    "(in path ${absPath("aFileWithAMissingArchetype/archetype.md")})"))
    }

    @Test
    fun failsForIncorrectArchetype(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongArchetype(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithMavenCoordinates()
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage(
                "Found [${ARCHETYPE_TAG.value}] tag with incorrect archetype code for " +
                    "'<!---[Archetype](archetypeGroupId archetypeArtifactId=test archetypeVersion " +
                    "groupId=local artifactId=test version=1.0.0 packaging=java )-->' " +
                    "(in path ${absPath("aFileWithAWrongArchetype/archetype.md")})"))
    }


    companion object {
        private const val BASE_PATH = "target/tempTestDirs/archetype/"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
