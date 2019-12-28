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

package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.Configurator
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.givenWhenThen.TestEnvironment.Companion.emptyTestEnvironment
import de.quantummaid.documaid.shared.SampleMavenProjectProperties
import de.quantummaid.documaid.shared.createFileWithContent
import de.quantummaid.documaid.shared.deleteFileIfExisting
import java.nio.file.Path

class DokuMaidTestBuilder private constructor() {
    private val testEnvironment = emptyTestEnvironment()
    private val dokuMaidConfigurationBuilder = DocuMaidConfiguration.aDocuMaidConfiguration()
    private val setupSteps: MutableCollection<() -> Unit> = ArrayList()
    private val cleanupSteps: MutableCollection<() -> Unit> = ArrayList()

    fun configuredWithGoal(goal: Goal): DokuMaidTestBuilder {
        dokuMaidConfigurationBuilder.forGoal(goal)
        return this
    }

    fun configuredWithBasePath(basePath: Path): DokuMaidTestBuilder {
        return configuredWithBasePath(basePath.toString())
    }

    fun configuredWithBasePath(basePath: String): DokuMaidTestBuilder {
        dokuMaidConfigurationBuilder.withBasePath(basePath)
        testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, basePath)
        return this
    }

    fun configuredWith(sampleFilesBuilder: SampleFilesBuilder): DokuMaidTestBuilder {
        val sampleFiles = sampleFilesBuilder.build()
        testEnvironment.setProperty(TestEnvironmentProperty.SAMPLE_FILE, sampleFiles)
        val fileName = sampleFiles.fileName
        setupSteps.add {
            val basePath = testEnvironment.getPropertyAsType<String>(TestEnvironmentProperty.BASE_PATH)
            val contentInput = sampleFiles.contentInput
            createFileWithContent(basePath, fileName, contentInput)
        }
        cleanupSteps.add {
            val basePath = testEnvironment.getPropertyAsType<String>(TestEnvironmentProperty.BASE_PATH)
            deleteFileIfExisting(basePath, fileName)
        }
        return this
    }

    fun configuredWith(configurator: Configurator): DokuMaidTestBuilder {
        configurator.invoke(testEnvironment, dokuMaidConfigurationBuilder, setupSteps, cleanupSteps)
        return this
    }

    fun configuredWithMavenCoordinates(): DokuMaidTestBuilder {
        val groupId = GroupId.create(SampleMavenProjectProperties.SAMPLE_GROUP_ID)
        val artifactId = ArtifactId.create(SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID)
        val version = Version.create(SampleMavenProjectProperties.SAMPLE_VERSION_ID)
        val mavenConfiguration = MavenConfiguration(groupId, artifactId, version)
        dokuMaidConfigurationBuilder.withMavenConfiguration(mavenConfiguration)
        return this
    }

    fun build(): TestEnvironment {
        val dokuMaidConfiguration = dokuMaidConfigurationBuilder
            .withLogger(NoopTestLogger.noopTestLogger())
            .build()
        val dokuMaid = de.quantummaid.documaid.DocuMaid.dokuMaid(dokuMaidConfiguration)
        testEnvironment.setProperty(TestEnvironmentProperty.DOKU_MAID_INSTANCE, dokuMaid)
        testEnvironment.setProperty(TestEnvironmentProperty.SETUP_STEPS, setupSteps)
        testEnvironment.setProperty(TestEnvironmentProperty.CLEAN_UP_STEPS, cleanupSteps)
        return testEnvironment
    }

    companion object {
        fun aDokuMaid(): DokuMaidTestBuilder = DokuMaidTestBuilder()
    }
}
