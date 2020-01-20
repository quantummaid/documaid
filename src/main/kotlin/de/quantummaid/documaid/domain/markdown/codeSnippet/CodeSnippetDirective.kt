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
import de.quantummaid.documaid.domain.markdown.DirectiveTag
import de.quantummaid.documaid.domain.markdown.MarkdownCodeSection
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.domain.unclassifiedFile.UnclassifiedFile
import de.quantummaid.documaid.domain.xml.XmlFile
import de.quantummaid.documaid.errors.DocuMaidException
import de.quantummaid.documaid.io.readFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CodeSnippetDirective(val rawMarkdownDirective: RawMarkdownDirective, val options: CodeSnippetDirectiveOptions, private val codeSnippetMarkdown: CodeSnippetMarkdown) {

    companion object {
        val CODE_SNIPPET_TAG = DirectiveTag("CodeSnippet")

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): CodeSnippetDirective {
            val options = CodeSnippetDirectiveOptions.create(rawMarkdownDirective, file)
            val codeSnippetMarkdown = loadCode(options, file, project, rawMarkdownDirective)
            return CodeSnippetDirective(rawMarkdownDirective, options, codeSnippetMarkdown)
        }

        private fun loadCode(options: CodeSnippetDirectiveOptions, file: MarkdownFile, project: Project, rawMarkdownDirective: RawMarkdownDirective): CodeSnippetMarkdown {
            if (options.snippetId != null) {
                return loadSnippetCode(options.snippetId, file, project, rawMarkdownDirective)
            } else if (options.filePath != null) {
                return loadCompleteFile(options.filePath, file, rawMarkdownDirective)
            } else {
                throw DocuMaidException.create("[$CODE_SNIPPET_TAG] could not handle config without snippetId or path", file)
            }
        }

        private fun loadSnippetCode(snippetId: SnippetId, file: MarkdownFile, project: Project, rawMarkdownDirective: RawMarkdownDirective): CodeSnippetMarkdown {
            val snippetsLookupTable = project.getInformation(CodeSnippetsLookupTable.SNIPPETS_LOOKUP_TABLE_KEY)
            if (!snippetsLookupTable.uniqueSnippetExists(snippetId)) {
                throw DocuMaidException.create("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '${rawMarkdownDirective.completeString}'", file)
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
                ?: throw DocuMaidException.create("Found [$CODE_SNIPPET_TAG] tag with missing snippet for '${rawMarkdownDirective.completeString}'", file)
            val codeSnippet = CodeSnippet(snippet.content.trimIndent(), fileObject as ProjectFile)
            val markdownCodeSection = MarkdownCodeSection.createForFile(codeSnippet.code, fileObject)
            return CodeSnippetMarkdown.create(markdownCodeSection)
        }

        private fun loadCompleteFile(path: Path, file: MarkdownFile, rawMarkdownDirective: RawMarkdownDirective): CodeSnippetMarkdown {
            val targetPath = file.absolutePath().parent.resolve(path)
            if (!(Files.exists(targetPath) && Files.isRegularFile(targetPath))) {
                throw DocuMaidException.create("Found [$CODE_SNIPPET_TAG] referencing not existing file '$path' in '${rawMarkdownDirective.completeString}'", file)
            }
            val content = readFile(targetPath)
            val codeSnippet = CodeSnippet(content, UnclassifiedFile.create(targetPath))
            val markdownCodeSection = MarkdownCodeSection.createForPath(codeSnippet.code, path)
            return CodeSnippetMarkdown.create(markdownCodeSection)
        }
    }

    fun generateMarkdown(): String {
        return "${rawMarkdownDirective.completeString}\n${codeSnippetMarkdown.markdownCodeSection.content}"
    }
}

class CodeSnippetDirectiveOptions(val snippetId: SnippetId?, val filePath: Path?) {

    companion object {
        val OPTIONS_WITH_SNIPPET_ID_REGEX = """ *\(? *(?<id>[\w]*) *\)? *""".toRegex()
        val OPTIONS_WITH_FILE = """ *\(? *file=(?<filePath>[^ )]+) *\)? *""".toRegex()

        fun create(directive: RawMarkdownDirective, file: MarkdownFile): CodeSnippetDirectiveOptions {
            val options = directive.optionsString.value
            val matchResult = OPTIONS_WITH_SNIPPET_ID_REGEX.matchEntire(options)
            if (matchResult != null) {
                val snippetId = SnippetId(matchResult.groups["id"]!!.value)
                return CodeSnippetDirectiveOptions(snippetId, null)
            } else {
                val matchResult2 = OPTIONS_WITH_FILE.matchEntire(options)
                if (matchResult2 != null) {
                    val filePathString = matchResult2.groups["filePath"]!!.value
                    val filePath = Paths.get(filePathString)
                    return CodeSnippetDirectiveOptions(null, filePath)
                } else {
                    throw DocuMaidException.create("Found [$CODE_SNIPPET_TAG] tag with not parsable options '${directive.completeString}'", file)
                }
            }
        }
    }
}
