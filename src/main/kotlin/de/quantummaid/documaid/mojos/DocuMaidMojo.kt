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

package de.quantummaid.documaid.mojos

import de.quantummaid.documaid.DocuMaid.Companion.docuMaid
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.errors.ErrorsEncounteredInDokuMaidException
import de.quantummaid.documaid.logging.MavenLogger
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.nio.file.Path
import java.nio.file.Paths

abstract class DocuMaidMojo : AbstractMojo() {
    @Parameter(property = "project", required = true, readonly = true)
    private val project: MavenProject? = null
    @Parameter(property = "skipTests", defaultValue = "false")
    private val skip: Boolean = false
    @Parameter(property = "skipPaths")
    private val skipPaths: List<String>? = null
    @Parameter(property = "platform")
    private val platform: String? = null

    protected abstract val goal: Goal

    @Throws(MojoFailureException::class)
    override fun execute() {
        if (skip) {
            return
        }

        val basePath = project!!.basedir
            .path
        val log = log
        val configuration = DocuMaidConfiguration.aDocuMaidConfiguration()
            .withBasePath(basePath)
            .forGoal(goal)
            .withLogger(MavenLogger.mavenLogger(log))
            .withMavenConfiguration(createMavenConfiguration())
            .withSkippedPaths(getSkippedPaths())
            .forPlatform(determinePlatform())
            .build()
        val dokuMaid = docuMaid(configuration)
        try {
            log.info("DocuMaid started")
            dokuMaid.pimpMyDocu()
            log.info("DocuMaid finished successful")
        } catch (e: ErrorsEncounteredInDokuMaidException) {
            val errors = e.errors
            log.error("DocuMaid finished with " + errors.size + " errors.")
            errors.forEach { error -> log.error(error.message()) }
            throw MojoFailureException("DocuMaid encountered errors during execution. See log for more information.")
        }
    }

    private fun createMavenConfiguration(): MavenConfiguration {
        if (project != null) {
            val groupId = GroupId.create(project.groupId)
            val artifactId = ArtifactId.create(project.artifactId)
            val version = Version.create(project.version)
            return MavenConfiguration(groupId, artifactId, version)
        } else {
            return MavenConfiguration(null, null, null)
        }
    }

    private fun getSkippedPaths(): List<Path> {
        return if (skipPaths == null || skipPaths.isEmpty()) {
            emptyList()
        } else {
            return skipPaths
                .map { Paths.get(it) }
                .map { it.toAbsolutePath() }
        }
    }

    private fun determinePlatform(): Platform {
        if (platform == null) {
            return Platform.GITHUB
        }
        return when (platform.toLowerCase()) {
            "hugo" -> Platform.HUGO
            else -> Platform.GITHUB
        }
    }
}
