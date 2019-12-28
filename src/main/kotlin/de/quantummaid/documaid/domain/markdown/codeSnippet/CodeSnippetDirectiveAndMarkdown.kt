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

import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.snippets.CodeSnippetsLookupTable
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.java.JavaFile
import de.quantummaid.documaid.domain.markdown.MarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.domain.unclassifiedFile.UnclassifiedFile
import de.quantummaid.documaid.domain.xml.XmlFile
import de.quantummaid.documaid.io.readFile
import java.nio.file.Files
import java.nio.file.Path

class CodeSnippetDirectiveAndMarkdown(private val codeSnippetDirective: CodeSnippetDirective, private val markdownCodeSection: MarkdownCodeSection) {

    companion object {
        val CODE_SEGMENT_PATTERN = "(?s)^[\\s]*```.*?(?=```)```".toRegex()

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): CodeSnippetDirectiveAndMarkdown {
            val codeSnippetDirective = CodeSnippetDirective.create(rawMarkdownDirective)
            val options = codeSnippetDirective.options
            val markdownCodeSection = loadCode(options, file, project, rawMarkdownDirective)
            return CodeSnippetDirectiveAndMarkdown(codeSnippetDirective, markdownCodeSection)
        }

        private fun loadCode(options: CodeSnippetDirectiveOptions, file: MarkdownFile, project: Project, rawMarkdownDirective: RawMarkdownDirective): MarkdownCodeSection {
            if (options.snippetId != null) {
                return loadSnippetCode(options.snippetId, project, rawMarkdownDirective)
            } else if (options.filePath != null) {
                return loadCompleteFile(options.filePath, file, rawMarkdownDirective)
            } else {
                throw IllegalArgumentException("[$CODE_SNIPPET_TAG] could not handle config without snippetId or path")
            }
        }

        private fun loadSnippetCode(snippetId: SnippetId, project: Project, rawMarkdownDirective: RawMarkdownDirective): MarkdownCodeSection {
            val snippetsLookupTable = project.getInformation(CodeSnippetsLookupTable.SNIPPETS_LOOKUP_TABLE_KEY)
            if (!snippetsLookupTable.uniqueSnippetExists(snippetId)) {
                throw IllegalArgumentException("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '${rawMarkdownDirective.completeString}'")
            }
            val path = snippetsLookupTable.getUniqueSnippet(snippetId)

            val fileLookupTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
            val fileObject = fileLookupTable.getFileObject(path)

            val snippet = when (fileObject) {
                is JavaFile -> fileObject.snippetForId(snippetId)
                is XmlFile -> fileObject.snippetForId(snippetId)
                is UnclassifiedFile -> fileObject.snippetForId(snippetId)
                else -> null
            }
                    ?: throw java.lang.IllegalArgumentException("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '${rawMarkdownDirective.completeString}'")
            val codeSnippet = CodeSnippet(snippet.content.trimIndent(), fileObject as ProjectFile)
            return MarkdownCodeSection.createForFile(codeSnippet.code, fileObject)
        }

        private fun loadCompleteFile(path: Path, file: MarkdownFile, rawMarkdownDirective: RawMarkdownDirective): MarkdownCodeSection {
            val targetPath = file.absolutePath().parent.resolve(path)
            if (!(Files.exists(targetPath) && Files.isRegularFile(targetPath))) {
                throw IllegalArgumentException("Found [$CODE_SNIPPET_TAG] referencing not existing file '$path' in '${rawMarkdownDirective.completeString}'")
            }
            val content = readFile(targetPath)
            val codeSnippet = CodeSnippet(content, UnclassifiedFile.create(targetPath))
            return MarkdownCodeSection.createForPath(codeSnippet.code, path)
        }
    }

    fun generateMarkdown(): String {
        return "${codeSnippetDirective.rawMarkdownDirective.completeString}\n${markdownCodeSection.content}"
    }
}
