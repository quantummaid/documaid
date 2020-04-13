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

package de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.heading

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.syntaxBased.SyntaxBasedMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.errors.VerificationError

class HugoHeadingMarkdownHandler : SyntaxBasedMarkdownHandler {

    override fun generate(file: MarkdownFile, project: Project): Pair<MarkdownReplacement?, List<VerificationError>> {
        val content = file.content()
        val (lineWithH1Heading, headingStart) = locateLeadingH1Heading(content)
        return if (lineWithH1Heading != null) {
            val docuMaidConfiguration = project.getInformation(DocuMaidConfiguration.DOCUMAID_CONFIGURATION_KEY)
            val headingMarkdown = HugoHeadingMarkdown.create(lineWithH1Heading, file, docuMaidConfiguration)
            val newMarkdown = headingMarkdown.generateMarkdown()
            val range = IntRange(headingStart, headingStart + newMarkdown.length)
            Pair(MarkdownReplacement(range, lineWithH1Heading, newMarkdown), emptyList())
        } else {
            Pair(MarkdownReplacement.noReplacement(), emptyList())
        }
    }

    private fun locateLeadingH1Heading(content: String): Pair<String?, Int> {
        val headingStart = content.indexOf("#")
        val headingNotFound = headingStart < 0
        val endOfFileReached = headingStart + 1 == content.length
        if (headingNotFound || endOfFileReached) {
            return Pair(null, 0)
        }
        val notH1Heading = content[headingStart + 1] == '#'
        if (notH1Heading) {
            return Pair(null, 0)
        }

        val indexOfHeadingEnd = content.indexOf("\n", headingStart)
        val lineWithH1Heading = content.substring(headingStart, indexOfHeadingEnd + 1)
        return Pair(lineWithH1Heading, headingStart)
    }

    override fun validate(file: MarkdownFile, project: Project): List<VerificationError> {
        // Nothing to do, because the information is lost during generation and can not be validated with its own information
        return emptyList()
    }
}
