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

package de.quantummaid.documaid.domain.markdown.link

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.link.LinkDirective.Companion.LINK_TAG
import de.quantummaid.documaid.errors.VerificationError

class LinkMarkdownTagHandler : MarkdownTagHandler {

    override fun tag(): String = LINK_TAG.toString()

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        val linkDirective = LinkDirective.create(directive, file)
        val (directiveAndMarkdownLink, errors) = LinkDirectiveAndMarkdown.create(linkDirective, file, project)
        if (errors.isNotEmpty() || directiveAndMarkdownLink == null) {
            return Pair(null, errors)
        }
        val markdown = directiveAndMarkdownLink.generateMarkdown()

        val textToBeReplaced = textToBeReplaced(directive)
        val rangeToReplaceIn = rangeToReplaceIn(directive, markdown)
        val markdownReplacement = MarkdownReplacement(rangeToReplaceIn, textToBeReplaced, markdown)
        return Pair(markdownReplacement, emptyList())
    }

    private fun textToBeReplaced(markdownDirective: RawMarkdownDirective): String {
        val trailingLink = loadTrailingLink(markdownDirective)
        return if (trailingLink != null) {
            "${markdownDirective.completeString}\n$trailingLink"
        } else {
            markdownDirective.completeString
        }
    }

    private fun loadTrailingLink(markdownDirective: RawMarkdownDirective): String? {
        val remainingContent = markdownDirective.remainingMarkupFileContent.content
        if (!remainingContent.trimStart().startsWith("[")) {
            return null
        }

        val LINK_PATTERN = """\[ *[^]]+ *] *\([^)]*\)""".toRegex()
        val find = LINK_PATTERN.find(remainingContent)
        val range = find?.range ?: return ""

        val bigRange = IntRange(range.first, range.last)

        val link = remainingContent.substring(bigRange).trim()
        return link
    }

    private fun rangeToReplaceIn(markdownDirective: RawMarkdownDirective, textToReplace: String): IntRange {
        val startIndex = markdownDirective.range.first
        val endIndexInitialTag = markdownDirective.range.last
        val lengthNewContent = textToReplace.length
        return IntRange(startIndex, Math.max(endIndexInitialTag, startIndex + lengthNewContent))
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val (markdownReplacement, errors) = generate(directive, file, project)
        if (errors.isNotEmpty() || markdownReplacement == null) {
            return errors
        }
        val (_, textToBeReplaced, textToReplace) = markdownReplacement
        return if (textToReplace != textToBeReplaced) {
            if (noLinkPresent(textToBeReplaced)) {
                listOf(VerificationError("Found [${tag()}] tag without link being set for '${directive.completeString}'", file.absolutePath()))
            } else {
                listOf(VerificationError("Found [${tag()}] tag with wrong link being set: '${directive.completeString}'", file.absolutePath()))
            }
        } else {
            emptyList()
        }
    }

    private fun noLinkPresent(textToBeReplaced: String): Boolean {
        val stringWithPotentialLink = textToBeReplaced.substringAfter(">")
            .trim()
        return !stringWithPotentialLink.startsWith("[")
    }
}
