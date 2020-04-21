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
package de.quantummaid.documaid.config

import de.quantummaid.documaid.logging.Logger
import java.nio.file.Path
import java.nio.file.Paths

class DocuMaidConfigurationBuilder private constructor() {
    private var basePath: Path? = null
    private var goal: Goal? = null
    private var logger: Logger? = null
    private var mavenConfiguration = MavenConfiguration(null, null, null)
    private var skippedPaths = emptyList<Path>()
    private var platform: Platform = Platform.GITHUB
    private var hugoOutputPath = "hugo"
    private var repository: Repository? = null
    private var generationFlavorType: String? = null
    private var overriddenDocumentationDepth: String? = null

    fun withBasePath(basePath: String): DocuMaidConfigurationBuilder {
        this.basePath = Paths.get(basePath)
        return this
    }

    fun withBasePath(basePath: Path): DocuMaidConfigurationBuilder {
        this.basePath = basePath
        return this
    }

    fun forGoal(goal: Goal): DocuMaidConfigurationBuilder {
        this.goal = goal
        return this
    }

    fun withLogger(logger: Logger): DocuMaidConfigurationBuilder {
        this.logger = logger
        return this
    }

    fun withMavenConfiguration(mavenConfiguration: MavenConfiguration): DocuMaidConfigurationBuilder {
        this.mavenConfiguration = mavenConfiguration
        return this
    }

    fun withSkippedPaths(skippedPaths: List<Path>): DocuMaidConfigurationBuilder {
        this.skippedPaths = skippedPaths
        return this
    }

    fun forPlatform(platform: Platform): DocuMaidConfigurationBuilder {
        this.platform = platform
        return this
    }

    fun withHugoOutputPath(hugoOutputPath: String): DocuMaidConfigurationBuilder {
        this.hugoOutputPath = hugoOutputPath
        return this
    }

    fun withRepositoryUrl(repositoryBaseUrl: String): DocuMaidConfigurationBuilder {
        this.repository = GithubRepository.create(repositoryBaseUrl)
        return this
    }

    fun withGenerationFlavorType(generationFlavorType: String?): DocuMaidConfigurationBuilder {
        this.generationFlavorType = generationFlavorType
        return this
    }

    fun withOverriddenDocumentationDepth(overridenDocumentationDepth: String?): DocuMaidConfigurationBuilder {
        this.overriddenDocumentationDepth = overridenDocumentationDepth
        return this
    }

    fun build(): DocuMaidConfiguration {
        val absolutePath = basePath!!.toAbsolutePath()
        val documentationDepth = if (overriddenDocumentationDepth != null) {
            Integer.parseInt(overriddenDocumentationDepth)
        } else {
            0
        }
        return DocuMaidConfiguration(absolutePath, goal!!, logger!!, mavenConfiguration, skippedPaths,
            platform, hugoOutputPath, repository, generationFlavorType, documentationDepth)
    }

    companion object {
        fun builder(): DocuMaidConfigurationBuilder {
            return DocuMaidConfigurationBuilder()
        }
    }
}
