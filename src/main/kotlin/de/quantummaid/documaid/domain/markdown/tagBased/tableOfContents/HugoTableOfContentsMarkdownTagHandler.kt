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

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.GithubTableOfContentsMarkdown.Companion.startsWithTrailingTableOfContentsMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.errors.VerificationError

class HugoTableOfContentsMarkdownTagHandler : MarkdownTagHandler {

    override fun tag(): String = TOC_TAG.toString()

    override fun generate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): Pair<MarkdownReplacement?, List<VerificationError>> {
        val newMarkdown = newMarkdown(directive)
        val (oldMarkdown) = oldMarkdownToBeReplaced(directive)
        val range = calculateRangeToReplaceIn(directive, oldMarkdown, newMarkdown)
        val markdownReplacement = MarkdownReplacement(range, oldMarkdown, newMarkdown)
        return Pair(markdownReplacement, emptyList())
    }

    private fun newMarkdown(directive: RawMarkdownDirective): String {
        return directive.completeString
    }

    private fun oldMarkdownToBeReplaced(directive: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val markdownMatchResult = startsWithTrailingTableOfContentsMarkdown(directive.remainingMarkupFileContent)
        val text = if (markdownMatchResult.matches) {
            directive.completeString + markdownMatchResult.content
        } else {
            directive.completeString
        }
        return Pair(text, markdownMatchResult)
    }

    private fun calculateRangeToReplaceIn(
        directive: RawMarkdownDirective,
        oldMarkdown: String,
        newMarkdown: String
    ): IntRange {
        val rangeStart = directive.range.first
        val rangeEnd = rangeStart + Math.max(oldMarkdown.length, newMarkdown.length)
        return IntRange(rangeStart, rangeEnd)
    }

    override fun validate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {
        val newMarkdown = newMarkdown(directive)
        val (textToBeReplaced) = oldMarkdownToBeReplaced(directive)
        return if (textToBeReplaced != newMarkdown) {
            val verificationError = VerificationError.create("Found [${tag()}] tag with incorrect TOC", file)
            listOf(verificationError)
        } else {
            emptyList()
        }
    }
}
