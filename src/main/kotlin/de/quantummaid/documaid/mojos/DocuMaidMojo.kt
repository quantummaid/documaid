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

import de.quantummaid.documaid.DocuMaid.Companion.dokuMaid
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.domain.markdown.dependency.ArtifactId
import de.quantummaid.documaid.domain.markdown.dependency.GroupId
import de.quantummaid.documaid.domain.markdown.dependency.Version
import de.quantummaid.documaid.errors.ErrorsEncounteredInDokuMaidException
import de.quantummaid.documaid.logging.MavenLogger
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

abstract class DocuMaidMojo : AbstractMojo() {
    @Parameter(property = "project", required = true, readonly = true)
    private val project: MavenProject? = null
    @Parameter(property = "skipTests", defaultValue = "false")
    private val skip: Boolean = false

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
            .build()
        val dokuMaid = dokuMaid(configuration)
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
}
