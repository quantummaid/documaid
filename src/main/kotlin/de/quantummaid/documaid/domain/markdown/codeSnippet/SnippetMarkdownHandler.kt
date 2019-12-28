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

package de.quantummaid.documaid.domain.markdown.codeSnippet

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirectiveAndMarkdown.Companion.CODE_SEGMENT_PATTERN
import de.quantummaid.documaid.errors.VerificationError

class SnippetMarkdownHandler : MarkdownTagHandler {

    override fun tag(): String {
        return CODE_SNIPPET_TAG.toString()
    }

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        return try {
            val codeSnippetDirectiveAndMarkdown = CodeSnippetDirectiveAndMarkdown.create(directive, file, project)

            val markdown = codeSnippetDirectiveAndMarkdown.generateMarkdown()
            val textToBeReplaced = textToBeReplaced(directive)
            val range = rangeToReplaceIn(directive, textToBeReplaced)
            val markdownReplacement = MarkdownReplacement(range, textToBeReplaced, markdown)
            Pair(markdownReplacement, emptyList())
        } catch (e: Exception) {
            Pair(null, listOf(VerificationError.createFromException(e, file)))
        }
    }

    private fun rangeToReplaceIn(markdownDirective: RawMarkdownDirective, textToReplace: String): IntRange {
        val startIndex = markdownDirective.range.first
        val endIndexInitialTag = markdownDirective.range.last
        val lengthNewContent = textToReplace.length
        return IntRange(startIndex, Math.max(endIndexInitialTag, startIndex + lengthNewContent))
    }

    private fun textToBeReplaced(markdownDirective: RawMarkdownDirective): String {
        val trailingCodeSection = loadTrailingCodeSection(markdownDirective)
        if (trailingCodeSection != null) {
            return markdownDirective.completeString + "\n" + trailingCodeSection.content
        } else {
            return markdownDirective.completeString
        }
    }

    private fun loadTrailingCodeSection(markdownDirective: RawMarkdownDirective): MarkdownCodeSection? {
        val content = markdownDirective.remainingMarkupFileContent.content
        if (!content.startsWith("\n```")) {
            return null
        }

        val find = CODE_SEGMENT_PATTERN.find(content)
        val range = find?.range ?: return null

        val bigRange = IntRange(range.first, range.last)

        val codeContent = content.substring(bigRange).trim()
        return MarkdownCodeSection(codeContent)
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val (markdownReplacement, errors) = generate(directive, file, project)
        if (errors.isNotEmpty() || markdownReplacement == null) {
            return errors
        }
        val (_, textToBeReplaced, textToReplace) = markdownReplacement
        if (textToReplace != textToBeReplaced) {
            return if (!textToBeReplaced.contains("```")) {
                listOf(VerificationError("Found [${tag()}] tag with missing snippet for '${directive.completeString}'", file.absolutePath()))
            } else {
                listOf(VerificationError("Found [${tag()}] tag with incorrect code for '${directive.completeString}'", file.absolutePath()))
            }
        } else {
            return emptyList()
        }
    }
}
