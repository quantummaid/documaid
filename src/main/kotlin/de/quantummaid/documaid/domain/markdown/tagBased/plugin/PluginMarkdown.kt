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
package de.quantummaid.documaid.domain.markdown.tagBased.plugin

import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.TrailingMarkdownCodeSection.Companion.extractTrailingCodeSection
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.createMatchForTrailingMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.noMatchForTrailingCodeSection
import de.quantummaid.documaid.domain.maven.*

class PluginMarkdown private constructor(
    private val groupId: GroupId,
    private val artifactId: ArtifactId,
    private val version: Version,
    private val goal: MavenGoal,
    private val phase: MavenPhase
) {

    companion object {
        fun create(
            groupId: GroupId,
            artifactId: ArtifactId,
            version: Version,
            goal: MavenGoal,
            phase: MavenPhase
        ): PluginMarkdown {

            return PluginMarkdown(groupId, artifactId, version, goal, phase)
        }

        fun startsWithPluginMarkdown(
            remainingMarkupFileContent: RemainingMarkupFileContent
        ): TrailingMarkdownMatchResult {

            val content = remainingMarkupFileContent.content
            return if (content.trimStart().startsWith("```")) {
                val trailingCodeSection = extractTrailingCodeSection(remainingMarkupFileContent)
                return if (trailingCodeSection.codeContent.contains("<plugin>")) {
                    val completeLength = trailingCodeSection.completeLength
                    val untrimmedContent = trailingCodeSection.untrimmedContent
                    createMatchForTrailingMarkdown(completeLength, untrimmedContent)
                } else {
                    noMatchForTrailingCodeSection()
                }
            } else {
                noMatchForTrailingCodeSection()
            }
        }
    }

    fun markdownString(): String {
        return """
            ```xml
            <plugin>
                <groupId>${groupId.value}</groupId>
                <artifactId>${artifactId.value}</artifactId>
                <version>${version.value}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>${goal.value}</goal>
                        </goals>
                        <phase>${phase.value}</phase>
                    </execution>
                </executions>
            </plugin>
            ```
        """.trimIndent()
    }
}
