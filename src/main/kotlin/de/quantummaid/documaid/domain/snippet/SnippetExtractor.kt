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
package de.quantummaid.documaid.domain.snippet

import de.quantummaid.documaid.collecting.structure.FileType
import java.nio.file.Path

interface SnippetExtractor {

    fun extractSnippets(path: Path, regex: Regex): List<RawSnippet>

    companion object {
        fun createExtractorFor(fileType: FileType): SnippetExtractor {
            return when (fileType) {
                FileType.JAVA -> JavaProcessingSnippetExtractor()
                else -> RawSnippetExtractor()
            }
        }
    }
}

private class RawSnippetExtractor : SnippetExtractor {

    override fun extractSnippets(path: Path, regex: Regex): List<RawSnippet> {
        val fileContent = path.toFile().readText()
        return regex.findAll(fileContent)
            .map { it.groups }
            .map { RawSnippet(it["snippet"]!!.value, SnippetId(it["id"]!!.value)) }
            .toList()
    }
}

private class JavaProcessingSnippetExtractor : SnippetExtractor {
    private val rawSnippetExtractor: RawSnippetExtractor = RawSnippetExtractor()
    private val snippetProcessors: List<SnippetProcessor> = listOf(
        SupressionAnnotationsAndCommentsStrippingSnippetProcessor()
    )

    override fun extractSnippets(path: Path, regex: Regex): List<RawSnippet> {
        var snippets = rawSnippetExtractor.extractSnippets(path, regex)
        for (snippetProcessor in snippetProcessors) {
            snippets = snippets.map { snippetProcessor.process(it) }
        }
        return snippets
    }
}

private interface SnippetProcessor {

    fun process(snippet: RawSnippet): RawSnippet
}

private class SupressionAnnotationsAndCommentsStrippingSnippetProcessor : SnippetProcessor {
    private val suppressWarningsRegex = Regex(
        "@SuppressWarnings\\( *['\"][^'\"]*['\"] *\\)|@SuppressWarnings\\( *\\{ *(['\"][^'\"]*['\"] *(, *)?)* *} *\\)"
    )
    private val noSonarRegex = Regex("// *NOSONAR")

    override fun process(snippet: RawSnippet): RawSnippet {
        val cleanedLines = snippet.content.lines()
            .mapNotNull { removeOptionalSuppressWarning(it, suppressWarningsRegex) }
            .mapNotNull { removeOptionalSuppressWarning(it, noSonarRegex) }
            .joinToString(separator = "\n")
        return RawSnippet(cleanedLines, snippet.id)
    }

    private fun removeOptionalSuppressWarning(line: String, regexToRemove: Regex): String? {
        val matchResult = regexToRemove.find(line)
        return if (matchResult == null) {
            line
        } else {
            val range = matchResult.range
            val cleanedLine = line.substring(0, range.start) + line.substring(range.last + 1)
            if (cleanedLine.isBlank()) {
                null
            } else {
                cleanedLine
            }
        }
    }
}
