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

package de.quantummaid.documaid.domain.markdown.navigation

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.navigation.NavigationDirective.Companion.NAV_TAG
import de.quantummaid.documaid.errors.VerificationError

class NavigationMarkdownHandler : MarkdownTagHandler {

    override fun tag(): String = NAV_TAG.toString()

    companion object {
        val NAV_MARKDOWN_REGEX = """(\[&larr;]\([^)]+\)&nbsp;&nbsp;&nbsp;)?\[Overview]\([^)]+\)(&nbsp;&nbsp;&nbsp;\[&rarr;]\([^)]+\))?""".toRegex()
    }

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        val navigationDirective = NavigationDirective.create(file, project)
        val navigationMarkdown = navigationDirective.generateNavigationMarkdownObject()
        val markdown = navigationMarkdown.generateMarkdown()
        val textToReplace = directive.completeString + markdown
        val textToBeReplaced = textToBeReplaced(directive)
        val rangeToReplaceIn = rangeToReplaceIn(directive, textToReplace, textToBeReplaced)
        val markdownReplacement = MarkdownReplacement(rangeToReplaceIn, textToBeReplaced, textToReplace)
        return Pair(markdownReplacement, emptyList())
    }

    private fun textToBeReplaced(directive: RawMarkdownDirective): String {
        val followingMarkdown = directive.remainingMarkupFileContent.content
        val matchResult = NAV_MARKDOWN_REGEX.find(followingMarkdown)
        return if (matchResult != null) {
            "${directive.completeString}${matchResult.value}"
        } else {
            directive.completeString
        }
    }

    private fun rangeToReplaceIn(directive: RawMarkdownDirective, markdown: String, textToBeReplaced: String): IntRange {
        return IntRange(directive.startIndex(), directive.startIndex() + Math.max(markdown.length, textToBeReplaced.length))
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val (markdownReplacement, errors) = generate(directive, file, project)
        if (errors.isNotEmpty() || markdownReplacement == null) {
            return errors
        }
        val (_, textToBeReplaced, textToReplace) = markdownReplacement
        return if (textToReplace != textToBeReplaced) {
            if (noNavigationPresent(textToBeReplaced)) {
                listOf(VerificationError("Found [${tag()}] tag with missing navigation in '${file.absolutePath()}'", file.absolutePath()))
            } else {
                listOf(VerificationError("Found [${tag()}] tag with wrong navigation in '${file.absolutePath()}'", file.absolutePath()))
            }
        } else {
            emptyList()
        }
    }

    private fun noNavigationPresent(textToBeReplaced: String): Boolean {
        return !textToBeReplaced.contains(NAV_MARKDOWN_REGEX)
    }
}
