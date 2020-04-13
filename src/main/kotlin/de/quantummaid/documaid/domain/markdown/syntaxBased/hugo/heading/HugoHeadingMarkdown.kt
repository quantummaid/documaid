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

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.IndexedPath
import de.quantummaid.documaid.domain.IndexedPath.Companion.anIndexedPath
import de.quantummaid.documaid.domain.markdown.MarkdownFile

class HugoHeadingMarkdown private constructor(private val title: String, private val index: Int) {

    companion object {
        private val RAW_HEADING_PATTERN = Regex("# ?(?<title>[^\n]+)\n")
        private const val DEFAULT_INDEX = 1

        fun create(rawHeadingString: String, file: MarkdownFile, docuMaidConfiguration: DocuMaidConfiguration): HugoHeadingMarkdown {
            val matchEntire = RAW_HEADING_PATTERN.matchEntire(rawHeadingString)
            val errorMessage = "Could not parse title of heading: '$rawHeadingString'"
            matchEntire ?: throw IllegalArgumentException(errorMessage)
            val title = matchEntire.groups["title"]?.value ?: throw IllegalArgumentException(errorMessage)

            val indexedFile = extractIndex(file, docuMaidConfiguration)
            return HugoHeadingMarkdown(title, indexedFile)
        }

        private fun extractIndex(file: MarkdownFile, docuMaidConfiguration: DocuMaidConfiguration): Int {
            if (IndexedPath.isIndexedPath(file)) {
                val indexedFile = anIndexedPath(file)
                return indexedFile.index
            } else {
                val isRootReadme = docuMaidConfiguration.basePath.relativize(file.path).toString() == "README.md"
                if (isRootReadme) {
                    val hugoOutputPath = docuMaidConfiguration.hugoOutputPath
                    if (IndexedPath.isIndexedPath(hugoOutputPath)) {
                        return IndexedPath.anIndexedPath(hugoOutputPath, null).index
                    } else {
                        return DEFAULT_INDEX
                    }
                } else {
                    return DEFAULT_INDEX
                }
            }
        }
    }

    fun generateMarkdown(): String {
        val weight = index * 10
        return """
            ---
            title: "$title"
            weight: $weight
            ---
            """.trimIndent() + "\n"
    }
}
