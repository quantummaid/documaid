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
package de.quantummaid.documaid.domain.markdown.tagBased.dependency

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.config.DocuMaidConfiguration.Companion.DOCUMAID_CONFIGURATION_KEY
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.dependency.DependencyDirective.Companion.DEPENDENCY_TAG
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Scope
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.errors.DocuMaidException.Companion.aDocuMaidException

class DependencyDirective private constructor(
    private val rawMarkdownDirective: RawMarkdownDirective,
    private val options: DependencyDirectiveOptions
) {

    companion object {
        val DEPENDENCY_TAG = DirectiveTag("Dependency")

        fun create(
            rawMarkdownDirective: RawMarkdownDirective,
            file: MarkdownFile,
            project: Project
        ): DependencyDirective {
            val options = DependencyDirectiveOptions.create(rawMarkdownDirective, file, project)
            return DependencyDirective(rawMarkdownDirective, options)
        }
    }

    fun generateCompleteMarkdown(): String {
        val groupId = options.groupId
        val artifactId = options.artifactId
        val version = options.version
        val scope = options.scope
        val dependencyMarkdown = DependencyMarkdown.create(groupId, artifactId, version, scope)
        return "${rawMarkdownDirective.completeString}\n${dependencyMarkdown.markdownString()}"
    }
}

class DependencyDirectiveOptions(
    val groupId: GroupId,
    val artifactId: ArtifactId,
    val version: Version,
    val scope: Scope?
) {

    companion object {
        private val DEPENDENY_OPTIONS_REGEX = ("\\(? *(?<groupId>groupId(=[^ ]*)?) *" +
            "(?<artifactId>artifactId(=[^ ]*)?) *" +
            "(?<version>version(=[^ ]*)?) *" +
            "(?<scope>scope=[^ ]*)? *\\)?").toRegex()
        private val PROPERTY_VALUE_REGEX = """[\w]+=(?<value>.+)""".toRegex()

        fun create(
            rawMarkdownDirective: RawMarkdownDirective,
            file: MarkdownFile,
            project: Project
        ): DependencyDirectiveOptions {
            val mavenConfiguration = project.getInformation(DOCUMAID_CONFIGURATION_KEY).mavenConfiguration
            val optionsString = rawMarkdownDirective.optionsString
            val matchEntire = DEPENDENY_OPTIONS_REGEX.matchEntire(optionsString.value)
            if (matchEntire != null) {
                val groupId = extractGroupId(matchEntire, mavenConfiguration, file)
                val artifactId = extractArtifactId(matchEntire, mavenConfiguration, file)
                val version = extractVersion(matchEntire, mavenConfiguration, file)
                val scope = extractScope(matchEntire)
                return DependencyDirectiveOptions(groupId, artifactId, version, scope)
            } else {
                val message = "Cannot parse options for [${DEPENDENCY_TAG.value}]: ${optionsString.value}"
                throw aDocuMaidException(message, file)
            }
        }

        private fun extractGroupId(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): GroupId {
            val groupIdString = extractMandatoryValue(matchResult, "groupId", file)
            return if (groupIdString != null) {
                GroupId.create(groupIdString)
            } else {
                mavenConfig.getGroupId()
            }
        }

        private fun extractArtifactId(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): ArtifactId {
            val artifactIdString = extractMandatoryValue(matchResult, "artifactId", file)
            return if (artifactIdString != null) {
                ArtifactId.create(artifactIdString)
            } else {
                mavenConfig.getArtifactId()
            }
        }

        private fun extractVersion(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): Version {
            val versionString = extractMandatoryValue(matchResult, "version", file)
            return if (versionString != null) {
                Version.create(versionString)
            } else {
                mavenConfig.getVersion()
            }
        }

        private fun extractScope(matchResult: MatchResult): Scope? {
            val scopeString = extractOptionalValue(matchResult, "scope")
            return if (scopeString != null) {
                Scope.create(scopeString)
            } else {
                null
            }
        }

        private fun extractMandatoryValue(matchResult: MatchResult, valueName: String, file: MarkdownFile): String? {
            val matchGroup = matchResult.groups[valueName]
            if (matchGroup != null) {
                val propertyValueMatch = PROPERTY_VALUE_REGEX.matchEntire(matchGroup.value)
                return if (propertyValueMatch != null) {
                    propertyValueMatch.groups["value"]!!.value
                } else {
                    null
                }
            } else {
                throw aDocuMaidException("[${DEPENDENCY_TAG.value}] requires '$valueName' to be set in options.", file)
            }
        }

        private fun extractOptionalValue(matchResult: MatchResult, valueName: String): String? {
            val matchGroup = matchResult.groups[valueName]
            if (matchGroup != null) {
                val propertyValueMatch = PROPERTY_VALUE_REGEX.matchEntire(matchGroup.value)
                if (propertyValueMatch != null) {
                    return propertyValueMatch.groups["value"]!!.value
                }
            }
            return null
        }
    }
}
