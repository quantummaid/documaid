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

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsLookupData.Companion.TOC_LOOKUP_KEY

class TableOfContentsMarkdownTagHandler : MarkdownTagHandler {

    companion object {
        val INDEX_MARKDOWN_FILE_NAME_PATTERN = """(?<index>([1-9]|[\d]{2,}))_+(?<name>[\w]+)(\.md)?""".toRegex()
    }

    override fun tag(): String = TOC_TAG.toString()

    override fun generate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        return try {
            val tableOfContentsLookupData = project.getInformation(TOC_LOOKUP_KEY)
            if (!tableOfContentsLookupData.tableOfContentsAvailable()) {
                return Pair(null, listOf(VerificationError.create("Found [${tag()}] without a Table of Contents being generated", file)))
            }
            val tableOfContents = tableOfContentsLookupData.getTableOfContents()
            val tocMarkdown = TableOfContentsMarkdown(directive, tableOfContents, file)
            val markdownReplacement = tocMarkdown.generateReplacement()
            Pair(markdownReplacement, emptyList())
        } catch (e: Exception) {
            Pair(null, listOf(VerificationError.createFromException(e, file)))
        }
    }

    override fun validate(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): List<VerificationError> {
        val (markdownReplacement, errors) = generate(directive, file, project)
        if (errors.isNotEmpty() || markdownReplacement == null) {
            return errors
        }
        val (_, textToBeReplaced, textToReplace) = markdownReplacement
        return if (textToReplace != textToBeReplaced) {
            if (directive.remainingMarkupFileContent.startsWith("\n1. [")) {
                val verificationError = VerificationError.create("Found [${tag()}] tag with incorrect TOC for '${file.absolutePath()}'", file)
                listOf(verificationError)
            } else {
                val verificationError = VerificationError.create("Found [${tag()}] tag with missing TOC for '${file.absolutePath()}'", file)
                listOf(verificationError)
            }
        } else {
            emptyList()
        }
    }
}
