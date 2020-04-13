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

package de.quantummaid.documaid.domain.markdown

import de.quantummaid.documaid.collecting.structure.ProjectFile
import java.nio.file.Path

data class MarkdownCodeSection(val content: String) {

    companion object {
        fun create(code: String, language: String): MarkdownCodeSection {
            return MarkdownCodeSection("```$language\n$code\n```")
        }

        fun createForFile(code: String, file: ProjectFile): MarkdownCodeSection {
            return createForPath(code, file.absolutePath())
        }

        fun createForPath(code: String, path: Path): MarkdownCodeSection {
            val fileName = path.fileName.toString()
            val language = when {
                fileName.endsWith(".java") -> "java"
                fileName.endsWith(".xml") -> "xml"
                else -> ""
            }
            return MarkdownCodeSection.create(code, language)
        }

        fun createFromContent(content: String): MarkdownCodeSection {
            return MarkdownCodeSection(content)
        }
    }
}

data class TrailingMarkdownCodeSection(val untrimmedContent: String, val codeSection: MarkdownCodeSection) {
    val completeLength = untrimmedContent.length
    val codeContent = codeSection.content

    companion object {
        fun extractTrailingCodeSection(remainingMarkupFileContent: RemainingMarkupFileContent): TrailingMarkdownCodeSection {
            val content = remainingMarkupFileContent.content
            val startOfCodeSection = content.indexOf("```")
            val startOfCodeContent = content.indexOf("\n", startOfCodeSection) + 1
            val endOfCodeSection = content.indexOf("```", startIndex = startOfCodeContent)
            var endOfCodeContent = endOfCodeSection + "```".length
            if (content.length > endOfCodeContent && content[endOfCodeContent] == '\n') {
                endOfCodeContent + 1
            }
            val untrimmedContent = content.substring(0, endOfCodeContent)
            val codeSection = content.substring(startOfCodeContent, endOfCodeContent)
            val markdownCodeSection = MarkdownCodeSection.createFromContent(codeSection)
            return TrailingMarkdownCodeSection(untrimmedContent, markdownCodeSection)
        }
    }
}
