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

import de.quantummaid.documaid.domain.command.MultiLineCommandNewLine
import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.TrailingMarkdownCodeSection.Companion.extractTrailingCodeSection
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.createMatchForTrailingMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.noMatchForTrailingCodeSection

class ArchetypeMarkdown private constructor(
    private val archetype: Archetype,
    private val commandNewLine: MultiLineCommandNewLine
) {

    companion object {
        fun create(
            archetype: Archetype,
            commandNewLine: MultiLineCommandNewLine
        ): ArchetypeMarkdown {
            return ArchetypeMarkdown(archetype, commandNewLine)
        }

        fun startsWithArchetypeMarkdown(
            remainingMarkupFileContent: RemainingMarkupFileContent
        ): TrailingMarkdownMatchResult {
            val content = remainingMarkupFileContent.content
            return if (content.trimStart().startsWith("```")) {
                val trailingCodeSection = extractTrailingCodeSection(remainingMarkupFileContent)
                return if (trailingCodeSection.codeContent.contains("archetype:generate")) {
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
        val (
            archetypeGroupId, archetypeArtifactId, archetypeVersion,
            groupId, artifactId, version, packaging
        ) = archetype
        return "```xml\n" +
            "mvn archetype:generate ${commandNewLine.value}" +
            "    --batch-mode ${commandNewLine.value}" +
            "    -DarchetypeGroupId=${archetypeGroupId.value} ${commandNewLine.value}" +
            "    -DarchetypeArtifactId=${archetypeArtifactId.value} ${commandNewLine.value}" +
            "    -DarchetypeVersion=${archetypeVersion.value} ${commandNewLine.value}" +
            "    -DgroupId=${groupId.value} ${commandNewLine.value}" +
            "    -DartifactId=${artifactId.value} ${commandNewLine.value}" +
            "    -Dversion=${version.value} ${commandNewLine.value}" +
            "    -Dpackaging=${packaging.value} ${commandNewLine.value}" +
            "\n" +
            "cd ./${artifactId.value}\n" +
            "```"
    }
}
