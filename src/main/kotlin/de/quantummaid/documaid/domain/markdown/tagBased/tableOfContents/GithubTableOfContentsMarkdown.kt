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
package de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents

import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.createMatchForTrailingMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult.Companion.noMatchForTrailingCodeSection
import de.quantummaid.documaid.domain.tableOfContents.TableOfContents

class GithubTableOfContentsMarkdown private constructor(
    private val rawMarkdownDirective: RawMarkdownDirective,
    private val tableOfContents: TableOfContents
) {

    companion object {
        val TOC_REGEX =
            """^\n1\. \[ *.*(?=<!---EndOfToc-->)<!---EndOfToc-->""".toRegex(RegexOption.DOT_MATCHES_ALL)

        fun create(
            rawMarkdownDirective: RawMarkdownDirective,
            tableOfContents: TableOfContents
        ): GithubTableOfContentsMarkdown {
            return GithubTableOfContentsMarkdown(rawMarkdownDirective, tableOfContents)
        }

        fun startsWithTrailingTableOfContentsMarkdown(
            remainingMarkupFileContent: RemainingMarkupFileContent
        ): TrailingMarkdownMatchResult {
            val matchResult = TOC_REGEX.find(remainingMarkupFileContent.content)
            return if (matchResult != null) {
                val length = matchResult.range.last - matchResult.range.start
                createMatchForTrailingMarkdown(length, matchResult.value)
            } else {
                noMatchForTrailingCodeSection()
            }
        }
    }

    fun markdownString(): String {
        return rawMarkdownDirective.completeString + tableOfContents.generate()
    }
}
