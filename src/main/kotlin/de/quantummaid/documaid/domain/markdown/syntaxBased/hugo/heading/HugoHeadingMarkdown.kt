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
package de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.heading

import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentationWeights.HugoWeight
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.paths.IndexedPath
import de.quantummaid.documaid.domain.paths.IndexedPath.Companion.isIndexedPath

class HugoHeadingMarkdown private constructor(private val title: String, private val weight: HugoWeight) {

    companion object {
        private val RAW_HEADING_PATTERN = Regex("# ?(?<title>[^\n]+)\n")

        fun create(
            rawHeadingString: String,
            file: MarkdownFile
        ): HugoHeadingMarkdown {

            val matchEntire = RAW_HEADING_PATTERN.matchEntire(rawHeadingString)
            val errorMessage = "Could not parse title of heading: '$rawHeadingString'"
            matchEntire ?: throw IllegalArgumentException(errorMessage)
            val title = matchEntire.groups["title"]?.value ?: throw IllegalArgumentException(errorMessage)

            val hugoWeight = createWeight(file)
            return HugoHeadingMarkdown(title, hugoWeight)
        }

        private fun createWeight(file: MarkdownFile): HugoWeight {
            return if (file.hasDataFor(DOCUMENTATION_GEN_INFO_KEY)) {
                val generationInformation = file.getData(DOCUMENTATION_GEN_INFO_KEY)
                val hugoWeight = HugoWeight.createForMultiLevelWeight(generationInformation.weight!!)
                hugoWeight
            } else if (isIndexedPath(file)) {
                val indexedPath = IndexedPath.anIndexedPath(file)
                HugoWeight.createForIndividualWeight("${indexedPath.index}")
            } else {
                HugoWeight.createForIndividualWeight("0")
            }
        }

        fun create(title: String, weight: HugoWeight): HugoHeadingMarkdown {
            return HugoHeadingMarkdown(title, weight)
        }
    }

    fun generateMarkdown(): String {
        return """
            ---
            title: "$title"
            weight: ${weight.value}
            ---
        """.trimIndent() + "\n"
    }

    fun generateMarkdownWithSkipParam(): String {
        return """
            ---
            title: "$title"
            weight: ${weight.value}
            skip: "true"
            ---
        """.trimIndent() + "\n"
    }
}
