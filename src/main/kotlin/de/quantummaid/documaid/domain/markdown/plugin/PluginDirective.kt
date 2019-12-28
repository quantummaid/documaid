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

package de.quantummaid.documaid.domain.markdown.plugin

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.config.DocuMaidConfiguration.Companion.DOCUMAID_CONFIGURATION_KEY
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.domain.markdown.DirectiveTag
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.plugin.PluginDirective.Companion.PLUGIN_TAG
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.MavenGoal
import de.quantummaid.documaid.domain.maven.MavenPhase
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.errors.DocuMaidException

class PluginDirective(val rawMarkdownDirective: RawMarkdownDirective, val options: DependencyDirectiveOptions) {

    companion object {
        val PLUGIN_TAG = DirectiveTag("Plugin")

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): PluginDirective {
            val options = DependencyDirectiveOptions.create(rawMarkdownDirective, file, project)
            return PluginDirective(rawMarkdownDirective, options)
        }
    }

    fun generateCompleteMarkdown(): String {
        val pluginMarkdown = PluginMarkdown.create(options.groupId, options.artifactId, options.version, options.goal, options.phase)
        return "${rawMarkdownDirective.completeString}\n${pluginMarkdown.markdownString()}"
    }
}

class DependencyDirectiveOptions(val groupId: GroupId, val artifactId: ArtifactId, val version: Version, val goal: MavenGoal, val phase: MavenPhase) {

    companion object {
        private val PLUGIN_OPTIONS_REGEX = """\(? *(?<groupId>groupId(=[^ ]*)?) *(?<artifactId>artifactId(=[^ ]*)?) *(?<version>version(=[^ ]*)?) *(?<goal>goal=[^ ]*) *(?<phase>phase=[^ ]*) *\)?""".toRegex()
        private val PROPERTY_VALUE_REGEX = """[\w]+=(?<value>.+)""".toRegex()

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): DependencyDirectiveOptions {
            val mavenConfiguration = project.getInformation(DOCUMAID_CONFIGURATION_KEY).mavenConfiguration
            val optionsString = rawMarkdownDirective.optionsString
            val matchEntire = PLUGIN_OPTIONS_REGEX.matchEntire(optionsString.value)
            if (matchEntire != null) {
                val groupId = extractGroupId(matchEntire, mavenConfiguration, file)
                val artifactId = extractArtifactId(matchEntire, mavenConfiguration, file)
                val version = extractVersion(matchEntire, mavenConfiguration, file)
                val goal = extractGoal(matchEntire, file)
                val phase = extractPhase(matchEntire, file)
                return DependencyDirectiveOptions(groupId, artifactId, version, goal, phase)
            } else {
                throw DocuMaidException.create("Cannot parse options for [${PLUGIN_TAG.value}]: ${optionsString.value}", file)
            }
        }

        private fun extractGroupId(matchResult: MatchResult, mavenConfig: MavenConfiguration, file: MarkdownFile): GroupId {
            val groupIdString = extractMandatoryValue(matchResult, "groupId", file)
            return if (groupIdString != null) {
                GroupId.create(groupIdString)
            } else {
                mavenConfig.getGroupId()
            }
        }

        private fun extractArtifactId(matchResult: MatchResult, mavenConfig: MavenConfiguration, file: MarkdownFile): ArtifactId {
            val artifactIdString = extractMandatoryValue(matchResult, "artifactId", file)
            return if (artifactIdString != null) {
                ArtifactId.create(artifactIdString)
            } else {
                mavenConfig.getArtifactId()
            }
        }

        private fun extractVersion(matchResult: MatchResult, mavenConfig: MavenConfiguration, file: MarkdownFile): Version {
            val versionString = extractMandatoryValue(matchResult, "version", file)
            return if (versionString != null) {
                Version.create(versionString)
            } else {
                mavenConfig.getVersion()
            }
        }

        private fun extractGoal(matchResult: MatchResult, file: MarkdownFile): MavenGoal {
            val goalString = extractMandatoryValue(matchResult, "goal", file)
            return if (goalString != null) {
                MavenGoal.create(goalString)
            } else {
                throw DocuMaidException.create("[${PLUGIN_TAG.value}] requires 'goal' to be defined with a value in options.", file)
            }
        }

        private fun extractPhase(matchResult: MatchResult, file: MarkdownFile): MavenPhase {
            val phaseString = extractMandatoryValue(matchResult, "phase", file)
            return if (phaseString != null) {
                MavenPhase.create(phaseString)
            } else {
                throw DocuMaidException.create("[${PLUGIN_TAG.value}] requires 'phase' to be defined with a value in options.", file)
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
                throw DocuMaidException.create("[${PLUGIN_TAG.value}] requires '$valueName' to be set in options.", file)
            }
        }
    }
}
