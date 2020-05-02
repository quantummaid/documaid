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
package de.quantummaid.documaid.domain.markdown

import de.quantummaid.documaid.collecting.structure.FileType
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.markdown.syntaxBased.SyntaxBasedMarkdownHandler
import de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.SyntaxBasedMarkdownHandlerFactory
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandlerFactory.Companion.obtainMarkdownHandlersFor
import de.quantummaid.documaid.domain.markdown.tagBased.OptionsString
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.snippet.RawSnippet
import de.quantummaid.documaid.errors.DocuMaidException
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.processing.ProcessingResult
import de.quantummaid.documaid.processing.ProcessingResult.Companion.erroneousProcessingResult
import java.nio.file.Path
import java.nio.file.Paths

class MarkdownFile private constructor(
    val path: Path,
    private val directives: List<RawMarkdownDirective>,
    private val tagHandlers: List<MarkdownTagHandler>,
    private val syntaxBasedHandlers: List<SyntaxBasedMarkdownHandler>
) : ProjectFile() {

    companion object {

        fun create(path: Path, docuMaidConfig: DocuMaidConfiguration): MarkdownFile {
            val content = path.toFile().readText()
            val tagHandlers = obtainMarkdownHandlersFor(docuMaidConfig)
            val tagMatches = loadTags(content, tagHandlers, path)

            val syntaxBasedHandlers = SyntaxBasedMarkdownHandlerFactory.obtainMarkdownHandlersFor(docuMaidConfig)
            return MarkdownFile(path, tagMatches, tagHandlers, syntaxBasedHandlers)
        }

        fun createFromGeneratedFile(path: Path): MarkdownFile {
            return MarkdownFile(path, emptyList(), emptyList(), emptyList())
        }

        private fun loadTags(
            content: String,
            tagHandlers: List<MarkdownTagHandler>,
            path: Path
        ): List<RawMarkdownDirective> {

            val tagsGroup = tagHandlers.map { it.tag() }
                .joinToString(separator = "|", prefix = "(", postfix = ")") { it }
            val regex = "<!--- *\\[(?<tag>$tagsGroup)](?<options>.*?) *-->".toRegex()

            val matches: MutableList<RawMarkdownDirective> = mutableListOf()
            var currentMatch = findNextTagInComment(content, 0, regex, path)
            while (currentMatch != null) {
                matches.add(currentMatch)
                val nextStartIndex = currentMatch.endIndex()
                currentMatch = findNextTagInComment(content, nextStartIndex, regex, path)
            }
            return matches
        }

        private fun findNextTagInComment(
            content: String,
            startIndex: Int,
            regex: Regex,
            path: Path
        ): RawMarkdownDirective? {

            val tagFound: MatchResult? = regex.find(content, startIndex)
            return tagFound?.let { matchResult ->
                val range = matchResult.range
                val optionsString: String = matchResult.groups["options"]?.value ?: ""
                val tag = matchResult.groups["tag"]?.value
                    ?: throw DocuMaidException.aDocuMaidException("Could not identify tag of markdown directive", path)
                val completeString = matchResult.value
                val remainingMarkupFileContent = RemainingMarkupFileContent(content.substring(range.last + 1))
                val directiveTag = DirectiveTag(tag)
                val options = OptionsString(optionsString)
                RawMarkdownDirective(directiveTag, options, completeString, range, remainingMarkupFileContent)
            }
        }
    }

    override fun fileType(): FileType {
        return FileType.MARKDOWN
    }

    override fun absolutePath(): Path = path

    fun content() = path.toFile().readText()

    private fun handlerFor(directive: RawMarkdownDirective): MarkdownTagHandler? {
        return tagHandlers.first { directive.tag.value == it.tag() }
    }

    override fun process(project: Project): ProcessingResult {
        val content = content()

        // Must happen before syntax changes as snippets already have their position determined
        val (processedContentStep1, tagProcessErrors) = processMarkdownTags(content, project)
        if (tagProcessErrors.isNotEmpty()) {
            return ProcessingResult.erroneousProcessingResult(this, tagProcessErrors)
        }

        val (processedContentStep2, syntaxBasedHandlerErrors) = processSyntaxChanges(processedContentStep1!!, project)
        if (syntaxBasedHandlerErrors.isNotEmpty()) {
            return erroneousProcessingResult(this, syntaxBasedHandlerErrors)
        }

        return ProcessingResult.successfulProcessingResult(this, processedContentStep2!!)
    }

    private fun processSyntaxChanges(
        initialContent: String,
        project: Project
    ): Pair<String?, List<VerificationError>> {

        var content = initialContent
        var indexOffsets = 0
        syntaxBasedHandlers
            .map {
                val (markdownReplacement, errors) = it.generate(this, project)
                if (errors.isNotEmpty()) {
                    return Pair(null, errors)
                } else {
                    markdownReplacement!!
                }
            }
            .forEach {
                val (range, textToBeReplaced, textToReplace) = it
                if (!range.isEmpty()) {
                    val startIndex = range.start + indexOffsets
                    val endIndex = Math.min(range.last + indexOffsets, content.length)
                    val contentToChange = content.substring(startIndex, endIndex)
                    val changed = contentToChange.replaceFirst(textToBeReplaced, textToReplace)
                    content = content.substring(0, startIndex) + changed + content.substring(endIndex)
                    indexOffsets += textToReplace.length - textToBeReplaced.length
                }
            }
        return Pair(content, emptyList())
    }

    private fun processMarkdownTags(initialContent: String, project: Project): Pair<String?, List<VerificationError>> {
        val (processedMarkdownTags, creationErrors) = createMarkdownTags(project)
        if (creationErrors.isNotEmpty()) {
            return Pair(null, creationErrors)
        }

        var content = initialContent
        var indexOffsets = 0
        processedMarkdownTags!!
            .forEach {
                val (range, textToBeReplaced, textToReplace) = it
                if (!range.isEmpty()) {
                    val startIndex = range.start + indexOffsets
                    val endIndex = Math.min(range.last + indexOffsets, content.length)
                    val contentToChange = content.substring(startIndex, endIndex)
                    val changed = contentToChange.replaceFirst(textToBeReplaced, textToReplace)
                    content = content.substring(0, startIndex) + changed + content.substring(endIndex)
                    indexOffsets += textToReplace.length - textToBeReplaced.length
                }
            }
        return Pair(content, emptyList())
    }

    private fun createMarkdownTags(project: Project): Pair<List<MarkdownReplacement>?, List<VerificationError>> {
        val tagHandlerPairs = createTagHandlerPairs()
        val verificationErrors = mutableListOf<VerificationError>()
        val processedSnippets = tagHandlerPairs.mapNotNull {
            val (processedTags, errors) = invokeGenerateOnHandler(it, this, project)
            if (errors.isNotEmpty()) {
                verificationErrors.addAll(errors)
            }
            processedTags
        }

        if (verificationErrors.isNotEmpty()) {
            return Pair(null, verificationErrors)
        }
        return Pair(processedSnippets, emptyList())
    }

    private fun invokeGenerateOnHandler(
        pair: Pair<RawMarkdownDirective, MarkdownTagHandler>,
        file: MarkdownFile,
        project: Project
    ): Pair<MarkdownReplacement?, List<VerificationError>> {

        val (directive, handler) = pair
        return try {
            handler.generate(directive, file, project)
        } catch (e: Exception) {
            Pair(null, listOf(VerificationError.createFromException(e, file)))
        }
    }

    override fun validate(project: Project): List<VerificationError> {
        val tagHandlerPairs = createTagHandlerPairs()
        return tagHandlerPairs.flatMap { invokeValidateOnHandler(it, this, project) }
    }

    private fun invokeValidateOnHandler(
        pair: Pair<RawMarkdownDirective, MarkdownTagHandler>,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {

        val (directive, handler) = pair
        return try {
            handler.validate(directive, file, project)
        } catch (e: Exception) {
            listOf(VerificationError.createFromException(e, file))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createTagHandlerPairs(): List<Pair<RawMarkdownDirective, MarkdownTagHandler>> {
        val pairs = directives.map { it to handlerFor(it) }
            .filter { it.second != null }
        return pairs as List<Pair<RawMarkdownDirective, MarkdownTagHandler>>
    }

    override fun snippets(): List<RawSnippet> = emptyList()

    fun markdownDirectivesWithIdentifier(identifier: DirectiveTag): List<RawMarkdownDirective> {
        return directives.filter { it.tag.equals(identifier) }
    }

    fun createCopyForPath(path: String): MarkdownFile {
        return createCopyForPath(Paths.get(path))
    }

    private fun createCopyForPath(path: Path): MarkdownFile {
        return MarkdownFile(path, directives, tagHandlers, syntaxBasedHandlers)
    }
}
