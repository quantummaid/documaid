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
package de.quantummaid.documaid.domain.markdown.tagBased.archetype

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.config.DocuMaidConfiguration.Companion.DOCUMAID_CONFIGURATION_KEY
import de.quantummaid.documaid.config.MavenConfiguration
import de.quantummaid.documaid.domain.command.MultiLineCommandNewLine
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.archetype.ArchetypeDirective.Companion.ARCHETYPE_TAG
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Packaging
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.domain.os.OsType
import de.quantummaid.documaid.errors.DocuMaidException.Companion.aDocuMaidException

class ArchetypeDirective private constructor(
    private val rawMarkdownDirective: RawMarkdownDirective,
    private val options: ArchetypeDirectiveOptions
) {

    companion object {
        val ARCHETYPE_TAG = DirectiveTag("Archetype")

        fun create(
            rawMarkdownDirective: RawMarkdownDirective,
            file: MarkdownFile,
            project: Project
        ): ArchetypeDirective {
            val options = ArchetypeDirectiveOptions.create(rawMarkdownDirective, file, project)
            return ArchetypeDirective(rawMarkdownDirective, options)
        }
    }

    fun generateCompleteMarkdown(): String {
        val multiLineCommandNewLine = MultiLineCommandNewLine.forOsType(options.osType)
        val dependencyMarkdown = ArchetypeMarkdown.create(options.archetype, multiLineCommandNewLine)
        return "${rawMarkdownDirective.completeString}\n${dependencyMarkdown.markdownString()}"
    }
}

class ArchetypeDirectiveOptions(
    val archetype: Archetype,
    val osType: OsType
) {

    companion object {
        private val ARCHETYPE_OPTIONS_REGEX = ("\\(? *" +
            "(?<archetypeGroupId>archetypeGroupId(=[^ ]*)?) *" +
            "(?<archetypeArtifactId>archetypeArtifactId(=[^ ]*)?) *" +
            "(?<archetypeVersion>archetypeVersion(=[^ ]*)?) *" +
            "(?<groupId>groupId=[^ ]*) *" +
            "(?<artifactId>artifactId=[^ ]*) *" +
            "(?<version>version=[^ ]*) *" +
            "(?<packaging>packaging=[^ ]*) *" +
            "(?<os>os=[^ ]*)? *" +
            "\\)?").toRegex()
        private val PROPERTY_VALUE_REGEX = """[\w]+=(?<value>.+)""".toRegex()

        fun create(
            rawMarkdownDirective: RawMarkdownDirective,
            file: MarkdownFile,
            project: Project
        ): ArchetypeDirectiveOptions {
            val mavenConfiguration = project.getInformation(DOCUMAID_CONFIGURATION_KEY).mavenConfiguration
            val optionsString = rawMarkdownDirective.optionsString
            val matchEntire = ARCHETYPE_OPTIONS_REGEX.matchEntire(optionsString.value)
            if (matchEntire != null) {
                val (osType, archetype) = extractArchetype(matchEntire, mavenConfiguration, file)
                return ArchetypeDirectiveOptions(archetype, osType)
            } else {
                val message = "Cannot parse options for [${ARCHETYPE_TAG.value}]: ${optionsString.value}"
                throw aDocuMaidException(message, file)
            }
        }

        private fun extractArchetype(
            matchEntire: MatchResult,
            mavenConfiguration: MavenConfiguration,
            file: MarkdownFile
        ): Pair<OsType, Archetype> {
            val archetypeGroupId = extractArchetypeGroupId(matchEntire, mavenConfiguration, file)
            val archetypeArtifactId = extractArchetypeArtifactId(matchEntire, mavenConfiguration, file)
            val archetypeVersion = extractArchetypeVersion(matchEntire, mavenConfiguration, file)
            val groupId = extractGroupId(matchEntire, file)
            val artifactId = extractArtifactId(matchEntire, file)
            val version = extractVersion(matchEntire, file)
            val packaging = extractPackaging(matchEntire, file)
            val osType = extractOsType(matchEntire)
            val archetype = Archetype(archetypeGroupId, archetypeArtifactId, archetypeVersion,
                groupId, artifactId, version, packaging)
            return Pair(osType, archetype)
        }

        private fun extractArchetypeGroupId(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): GroupId {
            val groupName = "archetypeGroupId"
            val groupIdString = extractMandatoryPropertyWithOptionalValue(matchResult, groupName, file)
            return if (groupIdString != null) {
                GroupId.create(groupIdString)
            } else {
                mavenConfig.getGroupId()
            }
        }

        private fun extractArchetypeArtifactId(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): ArtifactId {
            val groupName = "archetypeArtifactId"
            val artifactIdString = extractMandatoryPropertyWithOptionalValue(matchResult, groupName, file)
            return if (artifactIdString != null) {
                ArtifactId.create(artifactIdString)
            } else {
                mavenConfig.getArtifactId()
            }
        }

        private fun extractArchetypeVersion(
            matchResult: MatchResult,
            mavenConfig: MavenConfiguration,
            file: MarkdownFile
        ): Version {
            val groupName = "archetypeVersion"
            val versionString = extractMandatoryPropertyWithOptionalValue(matchResult, groupName, file)
            return if (versionString != null) {
                Version.create(versionString)
            } else {
                mavenConfig.getVersion()
            }
        }

        private fun extractGroupId(matchResult: MatchResult, file: MarkdownFile): GroupId {
            val groupIdString = extractMandatoryPropertyWithMandatoryValue(matchResult, "groupId", file)
            return GroupId.create(groupIdString)
        }

        private fun extractArtifactId(matchResult: MatchResult, file: MarkdownFile): ArtifactId {
            val artifactIdString = extractMandatoryPropertyWithMandatoryValue(matchResult, "artifactId", file)
            return ArtifactId.create(artifactIdString)
        }

        private fun extractVersion(matchResult: MatchResult, file: MarkdownFile): Version {
            val versionString = extractMandatoryPropertyWithMandatoryValue(matchResult, "version", file)
            return Version.create(versionString)
        }

        private fun extractPackaging(matchResult: MatchResult, file: MarkdownFile): Packaging {
            val packagingString = extractMandatoryPropertyWithMandatoryValue(matchResult, "packaging", file)
            return Packaging.create(packagingString)
        }

        private fun extractOsType(matchResult: MatchResult): OsType {
            val osTypeString = extractOptionalValue(matchResult, "os")
            return if (osTypeString != null) {
                OsType.forString(osTypeString)
            } else {
                OsType.LINUX
            }
        }

        private fun extractMandatoryPropertyWithOptionalValue(
            matchResult: MatchResult,
            valueName: String,
            file: MarkdownFile
        ): String? {
            val matchGroup = matchResult.groups[valueName]
            if (matchGroup != null) {
                val propertyValueMatch = PROPERTY_VALUE_REGEX.matchEntire(matchGroup.value)
                return if (propertyValueMatch != null) {
                    propertyValueMatch.groups["value"]!!.value
                } else {
                    null
                }
            } else {
                throw aDocuMaidException("[${ARCHETYPE_TAG.value}] requires '$valueName' to be set in options.", file)
            }
        }

        private fun extractMandatoryPropertyWithMandatoryValue(
            matchResult: MatchResult,
            valueName: String,
            file: MarkdownFile
        ): String {
            val matchGroup = matchResult.groups[valueName]
            val exceptionMessage = "[${ARCHETYPE_TAG.value}] requires '$valueName' to be set in options."
            if (matchGroup != null) {
                val propertyValueMatch = PROPERTY_VALUE_REGEX.matchEntire(matchGroup.value)
                return if (propertyValueMatch != null) {
                    propertyValueMatch.groups["value"]!!.value
                } else {
                    throw aDocuMaidException(exceptionMessage, file)
                }
            } else {
                throw aDocuMaidException(exceptionMessage, file)
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
