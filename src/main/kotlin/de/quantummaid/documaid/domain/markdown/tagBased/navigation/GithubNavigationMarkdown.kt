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
package de.quantummaid.documaid.domain.markdown.tagBased.navigation

import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.tagBased.link.GithubLinkMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.createMatchForTrailingMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.noMatchForTrailingCodeSection

class GithubNavigationMarkdown(
    val fileWithDirective: MarkdownFile,
    val previousFile: MarkdownFile?,
    val overviewFile: MarkdownFile,
    val nextFile: MarkdownFile?
) {

    companion object {
        val SPACES = "&nbsp;&nbsp;&nbsp;"
        val NAV_MARKDOWN_REGEX = """\n? *(\[&larr;]\([^)]+\)$SPACES)?\[Overview]\([^)]+\)($SPACES\[&rarr;]\([^)]+\))?"""

        fun startsWithNavigationMarkdown(
            remainingMarkupFileContent: RemainingMarkupFileContent
        ): TrailingMarkdownMatchResult {
            val content = remainingMarkupFileContent.content
            if (!content.trimStart().startsWith("[")) {
                return noMatchForTrailingCodeSection()
            }

            val matchResult = NAV_MARKDOWN_REGEX.toRegex().find(content)
            return if (matchResult != null) {
                createMatchForTrailingMarkdown(matchResult.range.last - matchResult.range.start, matchResult.value)
            } else {
                noMatchForTrailingCodeSection()
            }
        }
    }

    fun generateMarkdown(navigationDirective: NavigationDirective): String {
        val directiveString = navigationDirective.directive.completeString
        val navMarkdown = "${previousFileLink()}${overviewLink()}${nextFileLink()}"
        return "$directiveString\n$navMarkdown"
    }

    private fun previousFileLink(): String {
        return if (previousFile != null) {
            val relativePath = fileWithDirective.absolutePath().parent.relativize(previousFile.absolutePath())
            val linkMarkdown = GithubLinkMarkdown.createLinkMarkdown("&larr;", relativePath.toString())
            "$linkMarkdown&nbsp;&nbsp;&nbsp;"
        } else {
            ""
        }
    }

    private fun overviewLink(): String {
        val relativePath = fileWithDirective.absolutePath().parent.relativize(overviewFile.absolutePath())
        return GithubLinkMarkdown.createLinkMarkdown("Overview", relativePath.toString())
    }

    private fun nextFileLink(): String {
        return if (nextFile != null) {
            val relativePath = fileWithDirective.absolutePath().parent.relativize(nextFile.absolutePath())
            val linkMarkdown = GithubLinkMarkdown.createLinkMarkdown("&rarr;", relativePath.toString())
            "&nbsp;&nbsp;&nbsp;$linkMarkdown"
        } else {
            ""
        }
    }
}
