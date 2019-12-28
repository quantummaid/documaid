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
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetMarkdown.Companion.startsWithCodeSnippetMarkdown
import de.quantummaid.documaid.domain.markdown.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.errors.VerificationError

class SnippetMarkdownHandler : MarkdownTagHandler {

    override fun tag(): String {
        return CODE_SNIPPET_TAG.toString()
    }

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        val codeSnippetDirective = CodeSnippetDirective.create(directive, file, project)
        val markdown = codeSnippetDirective.generateMarkdown()
        val (textToBeReplaced) = textToBeReplaced(directive)
        val range = rangeToReplaceIn(directive, textToBeReplaced)
        val markdownReplacement = MarkdownReplacement(range, textToBeReplaced, markdown)
        return Pair(markdownReplacement, emptyList())
    }

    private fun rangeToReplaceIn(markdownDirective: RawMarkdownDirective, textToReplace: String): IntRange {
        val startIndex = markdownDirective.range.first
        val endIndexInitialTag = markdownDirective.range.last
        val lengthNewContent = textToReplace.length
        return IntRange(startIndex, Math.max(endIndexInitialTag, startIndex + lengthNewContent))
    }

    private fun textToBeReplaced(markdownDirective: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val markdownMatchResult = startsWithCodeSnippetMarkdown(markdownDirective.remainingMarkupFileContent)
        val text = if (markdownMatchResult.matches) {
            markdownDirective.completeString + markdownMatchResult.content
        } else {
            markdownDirective.completeString
        }
        return Pair(text, markdownMatchResult)
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val codeSnippetDirective = CodeSnippetDirective.create(directive, file, project)
        val textToReplace = codeSnippetDirective.generateMarkdown()
        val (textToBeReplaced, trailingMarkdownMatchResult) = textToBeReplaced(directive)
        return if (textToBeReplaced != textToReplace) {
            val trailingCodeFound = trailingMarkdownMatchResult.matches
            if (trailingCodeFound) {
                listOf(VerificationError.create("Found [${tag()}] tag with incorrect code for '${directive.completeString}'", file))
            } else {
                listOf(VerificationError.create("Found [${tag()}] tag with missing snippet for '${directive.completeString}'", file))
            }
        } else {
            return emptyList()
        }
    }
}
