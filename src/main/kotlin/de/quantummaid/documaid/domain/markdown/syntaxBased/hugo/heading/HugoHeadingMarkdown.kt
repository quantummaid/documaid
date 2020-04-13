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

import de.quantummaid.documaid.domain.IndexedFile
import de.quantummaid.documaid.domain.IndexedFile.Companion.anIndexedFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile

class HugoHeadingMarkdown private constructor(private val title: String, private val indexedFile: IndexedFile) {

    companion object {

        private val RAW_HEADING_PATTERN = Regex("# ?(?<title>[^\n]+)\n")
        fun create(rawHeadingString: String, file: MarkdownFile): HugoHeadingMarkdown {
            val matchEntire = RAW_HEADING_PATTERN.matchEntire(rawHeadingString)
            val errorMessage = "Could not parse title of heading: '$rawHeadingString'"
            matchEntire ?: throw IllegalArgumentException(errorMessage)
            val title = matchEntire.groups["title"]?.value ?: throw IllegalArgumentException(errorMessage)

            val indexedFile = anIndexedFile(file)
            return HugoHeadingMarkdown(title, indexedFile)
        }
    }

    fun generateMarkdown(): String {
        val weight = indexedFile.index * 10
        return """
            ---
            title: "$title"
            weight: $weight
            ---
            """.trimIndent() + "\n"
    }
}
