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

package de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.GithubTableOfContentsMarkdown.Companion.startsWithTrailingTableOfContentsMarkdown
import de.quantummaid.documaid.errors.DocuMaidException
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsLookupData.Companion.TOC_LOOKUP_KEY

class GithubTableOfContentsMarkdownTagHandler : MarkdownTagHandler {

    companion object {
        val INDEX_MARKDOWN_FILE_NAME_PATTERN = """(?<index>([1-9]|[\d]{2,}))_+(?<name>[\w]+)(\.md)?""".toRegex()
    }

    override fun tag(): String = TOC_TAG.toString()

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        val textToReplace = textToReplace(project, directive, file)
        val (textToBeReplaced) = textToBeReplaced(directive)
        val rangeStart = directive.range.first
        val rangeEnd = rangeStart + Math.max(textToBeReplaced.length, textToReplace.length)
        return Pair(MarkdownReplacement(IntRange(rangeStart, rangeEnd), textToBeReplaced, textToReplace), emptyList())
    }

    private fun textToReplace(project: Project, directive: RawMarkdownDirective, file: MarkdownFile): String {
        val tableOfContentsLookupData = project.getInformation(TOC_LOOKUP_KEY)
        if (!tableOfContentsLookupData.tableOfContentsAvailable()) {
            throw DocuMaidException.create("Found [${tag()}] without a Table of Contents being generated", file)
        }
        val tableOfContents = tableOfContentsLookupData.getTableOfContents()
        val tocMarkdown = GithubTableOfContentsMarkdown(directive, tableOfContents, file)
        return tocMarkdown.markdownString()
    }

    private fun textToBeReplaced(directive: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val markdownMatchResult = startsWithTrailingTableOfContentsMarkdown(directive.remainingMarkupFileContent)
        val text = if (markdownMatchResult.matches) {
            directive.completeString + markdownMatchResult.content
        } else {
            directive.completeString
        }
        return Pair(text, markdownMatchResult)
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val textToReplace = textToReplace(project, directive, file)
        val (textToBeReplaced, trailingMarkdownMatchResult) = textToBeReplaced(directive)
        return if (textToBeReplaced != textToReplace) {
            val trailingCodeFound = trailingMarkdownMatchResult.matches
            if (trailingCodeFound) {
                val verificationError = VerificationError.create("Found [${tag()}] tag with incorrect TOC", file)
                listOf(verificationError)
            } else {
                val verificationError = VerificationError.create("Found [${tag()}] tag with missing TOC", file)
                listOf(verificationError)
            }
        } else {
            emptyList()
        }
    }
}
