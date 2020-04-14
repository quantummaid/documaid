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
package de.quantummaid.documaid.domain.markdown.tagBased.link

import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.createMatchForTrailingMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.noMatchForTrailingCodeSection

class GithubLinkMarkdown(val name: String, val target: String, val linkDirective: LinkDirective) {

    companion object {
        val LINK_PATTERN = """\n? *\[ *[^]]+ *] *\([^)]*\)""".toRegex()

        fun create(linkDirective: LinkDirective): GithubLinkMarkdown {
            val name = linkDirective.options.name
            val originalPathString = linkDirective.options.originalPathString
            return GithubLinkMarkdown(name, originalPathString, linkDirective)
        }

        fun startsWithLinkMarkdown(
            remainingMarkupFileContent: RemainingMarkupFileContent
        ): TrailingMarkdownMatchResult {
            val remainingContent = remainingMarkupFileContent.content
            return if (!remainingContent.trimStart().startsWith("[")) {
                noMatchForTrailingCodeSection()
            } else {
                val find = LINK_PATTERN.find(remainingContent)
                if (find != null) {
                    createMatchForTrailingMarkdown(find.range.last - find.range.start, find.value)
                } else {
                    noMatchForTrailingCodeSection()
                }
            }
        }

        fun createLinkMarkdown(name: String, target: String) = "[$name]($target)"
    }

    fun generateMarkdown(): String {
        val markdownLink = createLinkMarkdown(name, target)
        return "${linkDirective.directive.completeString}\n$markdownLink"
    }
}
