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

import de.quantummaid.documaid.collecting.structure.FileType
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.codeSnippet.SnippetMarkdownHandler
import de.quantummaid.documaid.domain.markdown.dependency.DependencyMarkdownHandler
import de.quantummaid.documaid.domain.markdown.link.LinkMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.navigation.NavigationMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.domain.snippet.RawSnippet
import de.quantummaid.documaid.errors.VerificationError
import java.nio.file.Path

class MarkdownFile private constructor(private val path: Path, val directives: List<RawMarkdownDirective>, val tagHandlers: List<MarkdownTagHandler>) : ProjectFile {

    companion object {
        val tagHandlers = listOf(
            SnippetMarkdownHandler(),
            LinkMarkdownTagHandler(),
            TableOfContentsMarkdownTagHandler(),
            NavigationMarkdownHandler(),
            DependencyMarkdownHandler()
        )

        fun create(path: Path): MarkdownFile {
            val content = path.toFile().readText()
            val tagsGroup = tagHandlers.map { it.tag() }
                .joinToString(separator = "|", prefix = "(", postfix = ")") { it }
            val regex = "<!--- *\\[(?<tag>$tagsGroup)](?<options>.*?) *-->".toRegex()

            val tagMatches = loadTags(content, regex)
            return MarkdownFile(path, tagMatches, tagHandlers)
        }

        private fun loadTags(content: String, regex: Regex): List<RawMarkdownDirective> {
            val matches: MutableList<RawMarkdownDirective> = mutableListOf()
            var currentMatch = findNextTagInComment(content, 0, regex)
            while (currentMatch != null) {
                matches.add(currentMatch)
                val nextStartIndex = currentMatch.endIndex()
                currentMatch = findNextTagInComment(content, nextStartIndex, regex)
            }
            return matches
        }

        private fun findNextTagInComment(content: String, startIndex: Int, regex: Regex): RawMarkdownDirective? {
            val tagFound: MatchResult? = regex.find(content, startIndex)
            return tagFound?.let { matchResult ->
                val range = matchResult.range
                val optionsString: String = matchResult.groups["options"]?.value ?: ""
                val tag = matchResult.groups["tag"]?.value
                    ?: throw IllegalArgumentException("Could not identify tag of markdown directive")
                val completeString = matchResult.value
                val remainingMarkupFileContent = RemainingMarkupFileContent(content.substring(range.last + 1))
                RawMarkdownDirective(DirectiveTag(tag), OptionsString(optionsString), completeString, range, remainingMarkupFileContent)
            }
        }
    }

    override fun fileType(): FileType {
        return FileType.MARKDOWN
    }

    override fun absolutePath(): Path = path

    private fun handlerFor(directive: RawMarkdownDirective): MarkdownTagHandler? {
        return tagHandlers.first { directive.tag.value == it.tag() }
    }

    override fun generate(project: Project): List<VerificationError> {
        val (processedMarkdownTags, creationErrors) = createMarkdownTags(project)
        if (creationErrors.isNotEmpty()) {
            return creationErrors
        }

        val file = path.toFile()
        var content = file.readText()
        var indexOffsets = 0
        processedMarkdownTags!!
            .forEach {
                val (range, textToBeReplaced, textToReplace) = it
                val startIndex = range.start + indexOffsets
                val endIndex = Math.min(range.last + indexOffsets, content.length)
                val contentToChange = content.substring(startIndex, endIndex)
                val changed = contentToChange.replaceFirst(textToBeReplaced, textToReplace)
                content = content.substring(0, startIndex) + changed + content.substring(endIndex)
                indexOffsets += textToReplace.length - textToBeReplaced.length
            }
        file.writeText(content)
        return emptyList()
    }

    override fun validate(project: Project): List<VerificationError> {
        val tagHandlerPairs = directives.map { it to handlerFor(it) }
            .filter { it.second != null }

        return tagHandlerPairs.flatMap { it.second!!.validate(it.first, this, project) }
    }

    private fun createMarkdownTags(project: Project): Pair<List<MarkdownReplacement>?, List<VerificationError>> {
        val tagHandlerPairs = directives.map { it to handlerFor(it) }
            .filter { it.second != null }
        val processedSnippetsErrorPairs: List<Pair<MarkdownReplacement?, List<VerificationError>>> = tagHandlerPairs
            .map { it.second!!.generate(it.first, this, project) }

        val errors = processedSnippetsErrorPairs
            .map { it.second }
            .flatten()
        if (errors.isNotEmpty()) {
            return Pair(null, errors)
        }
        val processedMarkdownTags = processedSnippetsErrorPairs
            .filter { it.first != null }
            .map { it.first!! }
        return Pair(processedMarkdownTags, emptyList())
    }

    override fun snippets(): List<RawSnippet> = emptyList()

    fun markdownDirectivesWithIdentifier(identifier: DirectiveTag): List<RawMarkdownDirective> {
        return directives.filter { it.tag.equals(identifier) }
    }
}
