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

package de.quantummaid.documaid.domain.markdown.tagBased.link

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.link.GithubLinkMarkdown.Companion.startsWithLinkMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.link.LinkDirective.Companion.LINK_TAG
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.errors.VerificationError

class GithubLinkMarkdownTagHandler : MarkdownTagHandler {

    override fun tag(): String = LINK_TAG.toString()

    override fun generate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): Pair<MarkdownReplacement?, List<VerificationError>> {
        val markdown = newMarkdown(directive, file, project)
        val (textToBeReplaced) = textToBeReplaced(directive)
        val rangeToReplaceIn = rangeToReplaceIn(directive, markdown, textToBeReplaced)
        val markdownReplacement = MarkdownReplacement(rangeToReplaceIn, textToBeReplaced, markdown)
        return Pair(markdownReplacement, emptyList())
    }

    override fun validate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {
        val markdown = newMarkdown(directive, file, project)
        val (textToBeReplaced, trailingMarkdownMatchResult) = textToBeReplaced(directive)
        return if (textToBeReplaced != markdown) {
            val trailingCodeFound = trailingMarkdownMatchResult.matches
            if (trailingCodeFound) {
                val message = "Found [${tag()}] tag with wrong link being set: '${directive.completeString}'"
                listOf(VerificationError.create(message, file))
            } else {
                val message = "Found [${tag()}] tag without link being set for '${directive.completeString}'"
                listOf(VerificationError.create(message, file))
            }
        } else {
            emptyList()
        }
    }

    private fun newMarkdown(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): String {
        val linkDirective = LinkDirective.create(directive, file, project)
        val githubLinkMarkdown = GithubLinkMarkdown.create(linkDirective)
        val markdown = githubLinkMarkdown.generateMarkdown()
        return markdown
    }

    private fun textToBeReplaced(markdownDirective: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val markdownMatchResult = startsWithLinkMarkdown(markdownDirective.remainingMarkupFileContent)
        val text = if (markdownMatchResult.matches) {
            "${markdownDirective.completeString}${markdownMatchResult.content}"
        } else {
            markdownDirective.completeString
        }
        return Pair(text, markdownMatchResult)
    }

    private fun rangeToReplaceIn(
        markdownDirective: RawMarkdownDirective,
        textToReplace: String,
        textToBeReplaced: String
    ): IntRange {
        val startIndex = markdownDirective.range.first
        val endIndexInitialTag = markdownDirective.range.last
        val lengthNewContent = textToReplace.length
        val endIndexNewContent = Math.max(startIndex + lengthNewContent, startIndex + textToBeReplaced.length)
        val endIndex = Math.max(endIndexInitialTag, endIndexNewContent)
        return IntRange(startIndex, endIndex)
    }
}
