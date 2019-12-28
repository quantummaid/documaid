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

import de.quantummaid.documaid.domain.markdown.MarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.markdown.TrailingMarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.matching.TrailingMarkdownMatchResult

class CodeSnippetMarkdown private constructor(val markdownCodeSection: MarkdownCodeSection) {

    companion object {
        fun create(markdownCodeSection: MarkdownCodeSection): CodeSnippetMarkdown {
            return CodeSnippetMarkdown(markdownCodeSection)
        }

        fun startsWithCodeSnippetMarkdown(remainingMarkupFileContent: RemainingMarkupFileContent): TrailingMarkdownMatchResult {
            val content = remainingMarkupFileContent.content
            return if (content.trimStart().startsWith("```")) {
                val trailingCodeSection = TrailingMarkdownCodeSection.extractTrailingCodeSection(remainingMarkupFileContent)
                TrailingMarkdownMatchResult.createForMatch(trailingCodeSection.completeLength, trailingCodeSection.untrimmedContent)
            } else {
                TrailingMarkdownMatchResult.createForNoMatch()
            }
        }
    }
}