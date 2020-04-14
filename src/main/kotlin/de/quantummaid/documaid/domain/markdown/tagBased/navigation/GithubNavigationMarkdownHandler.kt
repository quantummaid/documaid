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

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.GithubNavigationMarkdown.Companion.startsWithNavigationMarkdown
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.NavigationDirective.Companion.NAV_TAG
import de.quantummaid.documaid.errors.VerificationError

class GithubNavigationMarkdownHandler : MarkdownTagHandler {

    override fun tag(): String = NAV_TAG.toString()

    override fun generate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): Pair<MarkdownReplacement?, List<VerificationError>> {
        val newMarkdown = generateNewMarkdown(directive, file, project)
        val (textToBeReplaced) = textToBeReplaced(directive)
        val rangeToReplaceIn = rangeToReplaceIn(directive, newMarkdown, textToBeReplaced)
        val markdownReplacement = MarkdownReplacement(rangeToReplaceIn, textToBeReplaced, newMarkdown)
        return Pair(markdownReplacement, emptyList())
    }

    private fun textToBeReplaced(directive: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val matchResult = startsWithNavigationMarkdown(directive.remainingMarkupFileContent)
        val text = if (matchResult.matches) {
            "${directive.completeString}${matchResult.content}"
        } else {
            directive.completeString
        }
        return Pair(text, matchResult)
    }

    private fun rangeToReplaceIn(
        directive: RawMarkdownDirective,
        markdown: String,
        textToBeReplaced: String
    ): IntRange {
        val endInclusive = directive.startIndex() + Math.max(markdown.length, textToBeReplaced.length)
        return IntRange(directive.startIndex(), endInclusive)
    }

    override fun validate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {
        val markdown = generateNewMarkdown(directive, file, project)
        val (textToBeReplaced, matchResult) = textToBeReplaced(directive)
        return if (markdown != textToBeReplaced) {
            val navFound = matchResult.matches
            if (navFound) {
                listOf(VerificationError.create("Found [${tag()}] tag with wrong navigation", file))
            } else {
                listOf(VerificationError.create("Found [${tag()}] tag with missing navigation", file))
            }
        } else {
            emptyList()
        }
    }

    private fun generateNewMarkdown(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): String {
        val navDirective = NavigationDirective.create(directive, file, project)
        val previousFile = navDirective.previousFile
        val overviewFile = navDirective.overviewFile
        val nextFile = navDirective.nextFile
        val markdown = GithubNavigationMarkdown(file, previousFile, overviewFile, nextFile)
        return markdown.generateMarkdown(navDirective)
    }
}
