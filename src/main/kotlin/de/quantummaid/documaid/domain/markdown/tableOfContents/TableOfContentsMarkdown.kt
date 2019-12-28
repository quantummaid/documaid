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

package de.quantummaid.documaid.domain.markdown.tableOfContents

import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.RemainingMarkupFileContent
import de.quantummaid.documaid.domain.tableOfContents.TableOfContents

class TableOfContentsMarkdown(private val rawMarkdownDirective: RawMarkdownDirective, private val tableOfContents: TableOfContents, private val file: MarkdownFile) {

    fun generateReplacement(): MarkdownReplacement? {
        val textToBeReplaced = textToBeReplaced(rawMarkdownDirective)
        val tocString = tableOfContents.generate()
        val textToReplace = rawMarkdownDirective.completeString + tocString
        val endIndex = rawMarkdownDirective.endIndex() + Math.max(textToBeReplaced.length, tocString.length)
        val range = IntRange(rawMarkdownDirective.startIndex(), endIndex)
        return MarkdownReplacement(range, textToBeReplaced, textToReplace)
    }

    private fun textToBeReplaced(directive: RawMarkdownDirective): String {
        val trailingToc = loadTrailingToc(directive.remainingMarkupFileContent)
        return if (trailingToc != null) {
            directive.completeString + trailingToc
        } else {
            directive.completeString
        }
    }

    private fun loadTrailingToc(remainingMarkupFileContent: RemainingMarkupFileContent): String? {
        if (remainingMarkupFileContent.startsWith("\n1. [")) {
            val trailingTocRegex = """^\n1\. \[ *.*(?=<!---EndOfToc-->)<!---EndOfToc-->""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val matchResult = trailingTocRegex.find(remainingMarkupFileContent.content)
            if (matchResult != null) {
                return matchResult.value
            }
        }
        return null
    }
}
