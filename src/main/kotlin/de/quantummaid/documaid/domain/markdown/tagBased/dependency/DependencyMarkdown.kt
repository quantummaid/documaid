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

package de.quantummaid.documaid.domain.markdown.tagBased.dependency

import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.TrailingMarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Scope
import de.quantummaid.documaid.domain.maven.Version

class DependencyMarkdown private constructor(private val groupId: GroupId, private val artifactId: ArtifactId, private val version: Version, private val scope: Scope?) {

    companion object {
        fun create(groupId: GroupId, artifactId: ArtifactId, version: Version, scope: Scope?): DependencyMarkdown {
            return DependencyMarkdown(groupId, artifactId, version, scope)
        }

        fun startsWithDependencyMarkdown(remainingMarkupFileContent: RemainingMarkupFileContent): TrailingMarkdownMatchResult {
            val content = remainingMarkupFileContent.content
            return if (content.trimStart().startsWith("```")) {
                val trailingCodeSection = TrailingMarkdownCodeSection.extractTrailingCodeSection(remainingMarkupFileContent)
                return if (trailingCodeSection.codeContent.contains("<dependency>")) {
                    TrailingMarkdownMatchResult.createForMatch(trailingCodeSection.completeLength, trailingCodeSection.untrimmedContent)
                } else {
                    TrailingMarkdownMatchResult.createForNoMatch()
                }
            } else {
                TrailingMarkdownMatchResult.createForNoMatch()
            }
        }
    }

    fun markdownString(): String {
        val optionalScopeString = if (scope != null) "\n                <scope>${scope.value}</scope>" else ""
        return """
            ```xml
            <dependency>
                <groupId>${groupId.value}</groupId>
                <artifactId>${artifactId.value}</artifactId>
                <version>${version.value}</version>$optionalScopeString
            </dependency>
            ```
        """.trimIndent()
    }
}
