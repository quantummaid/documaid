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

package de.quantummaid.documaid.usecases.maven.dependency

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.dependency.DependencyDirective
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import java.nio.file.Paths

interface DependencySpecs {

    @Test
    fun canGenerateFullyDefinedDependencyInMarkdownFile(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleFullyDefinedDependency(BASE_PATH))
            .withPlatformConfiguration(platformConfiguration)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canGenerateDependencyWithPropertiesObtainedFromMavenProject() {
        given(aDokuMaid()
            .configuredWith(aFileWithADependencyWithoutAnythingDefined(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceWrongDependency() {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongGeneratedDependency(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForUnparsableOptionsString() {
        given(aDokuMaid()
            .configuredWith(aFileWithUnparsableDependencyOptionsString(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Cannot parse options for [${DependencyDirective.DEPENDENCY_TAG.value}]: ${"(groupId=not correct artifactId version )"} " +
                "(in path ${absPath("aFileWithUnparsableDependencyOptionsString/dependency.md")})"))
    }

    @Test
    fun succeedsForCorrectGeneratedDependency() {
        given(aDokuMaid()
            .configuredWith(aFileWithACorrectlyGeneratedDependency(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsForIncorrectlyGeneratedCode() {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongGeneratedDependency(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [${DependencyDirective.DEPENDENCY_TAG.value}] tag with incorrect dependency code for " +
                "'<!---[Dependency](groupId=local artifactId version=1.0.0 )-->' (in path ${absPath("aFileWithAWrongGeneratedDependency/dependency.md")})"))
    }

    @Test
    fun failsForMissingDependency() {
        given(aDokuMaid()
            .configuredWith(aFileWithDependencyWithMissingCode(BASE_PATH))
            .configuredWithGoal(Goal.VALIDATE)
            .configuredWithMavenCoordinates())
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [${DependencyDirective.DEPENDENCY_TAG.value}] tag with missing dependency code for " +
                "'<!---[Dependency](groupId artifactId=test version=1 scope=compile )-->' (in path ${absPath("aFileWithDependencyWithMissingCode/dependency.md")})"))
    }

    @Test
    fun canInsertDependencyAtTheEndOfFileWithoutNewline() {
        given(aDokuMaid()
            .configuredWith(aFileWithASingleDependencyAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceDependencyAtTheEndOfFileWithoutNewline() {
        given(aDokuMaid()
            .configuredWith(aFileWithAWrongDependencyAtTheEndOfFileWithoutNewLine(BASE_PATH))
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/dependency/"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
