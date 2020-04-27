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
package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.generating.GenerationFlavorType
import de.quantummaid.documaid.givenWhenThen.TestEnvironment.Companion.emptyTestEnvironment
import de.quantummaid.documaid.shared.filesystem.Setup
import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutFileStructure
import de.quantummaid.documaid.shared.filesystem.SutFileStructure.Companion.aFileStructureForDocuMaidToProcess
import de.quantummaid.documaid.shared.samplesFiles.SampleMavenProjectProperties
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import java.nio.file.Path

class DokuMaidTestBuilder private constructor() {
    private val testEnvironment = emptyTestEnvironment()
    private val docuMaidConfigurationBuilder = DocuMaidConfiguration.aDocuMaidConfiguration()
    private val sutFileStructure: SutFileStructure = aFileStructureForDocuMaidToProcess()

    fun configuredWithGoal(goal: Goal): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.forGoal(goal)
        return this
    }

    fun configuredWithBasePath(basePath: Path): DokuMaidTestBuilder {
        return configuredWithBasePath(basePath.toString())
    }

    fun configuredwith(platformConfiguration: PlatformConfiguration): DokuMaidTestBuilder {
        platformConfiguration.apply(docuMaidConfigurationBuilder)
        return this
    }

    fun configuredWithBasePath(basePath: String): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.withBasePath(basePath)
        return this
    }

    fun configuredWith(setupUpdate: SetupUpdate): DokuMaidTestBuilder {
        val setup = Setup(testEnvironment, sutFileStructure)
        setupUpdate(setup)
        return this
    }

    fun configuredWithMavenCoordinates(): DokuMaidTestBuilder {
        val groupId = GroupId.create(SampleMavenProjectProperties.SAMPLE_GROUP_ID)
        val artifactId = ArtifactId.create(SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID)
        val version = Version.create(SampleMavenProjectProperties.SAMPLE_VERSION_ID)
        val mavenConfiguration = MavenConfiguration(groupId, artifactId, version)
        docuMaidConfigurationBuilder.withMavenConfiguration(mavenConfiguration)
        return this
    }

    fun configuredWith(platform: Platform): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.forPlatform(platform)
        return this
    }

    fun configuredWithFlavorType(flavorType: String): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.withGenerationFlavorType(flavorType)
        return this
    }

    fun configuredWithFlavorType(flavorType: GenerationFlavorType): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.withGenerationFlavorType(flavorType.name)
        return this
    }

    fun configuredWithHugoOutputPath(hugoOutputPath: String): DokuMaidTestBuilder {
        docuMaidConfigurationBuilder.withHugoOutputPath(hugoOutputPath)
        return this
    }

    fun build(): TestEnvironment {
        docuMaidConfigurationBuilder.withLogger(NoopTestLogger.noopTestLogger())
        testEnvironment.setProperty(TestEnvironmentProperty.DOCU_MAID_CONFIG_BUILDER, docuMaidConfigurationBuilder)
        testEnvironment.setProperty(TestEnvironmentProperty.SUT_FILE_STRUCTURE, sutFileStructure)
        return testEnvironment
    }

    companion object {
        fun aDokuMaid(): DokuMaidTestBuilder = DokuMaidTestBuilder()
    }
}
