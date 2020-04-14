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

package de.quantummaid.documaid.domain.markdown.tagBased.plugin

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownReplacement
import de.quantummaid.documaid.domain.markdown.tagBased.MarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.matching.TrailingMarkdownMatchResult
import de.quantummaid.documaid.domain.markdown.tagBased.plugin.PluginDirective.Companion.PLUGIN_TAG
import de.quantummaid.documaid.errors.VerificationError

class PluginMarkdownHandler : MarkdownTagHandler {

    override fun tag(): String = PLUGIN_TAG.value

    override fun generate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): Pair<MarkdownReplacement?, List<VerificationError>> {

        val pluginDirective = PluginDirective.create(directive, file, project)
        val textToReplace = pluginDirective.generateCompleteMarkdown()
        val (textToBeReplaced) = textToBeReplaced(directive)
        val rangeStart = directive.range.first
        val rangeEnd = rangeStart + Math.max(textToBeReplaced.length, textToReplace.length)
        return Pair(MarkdownReplacement(IntRange(rangeStart, rangeEnd), textToBeReplaced, textToReplace), emptyList())
    }

    override fun validate(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {

        val pluginDirective = PluginDirective.create(directive, file, project)
        val textToReplace = pluginDirective.generateCompleteMarkdown()
        val (textToBeReplaced, trailingMarkdownMatchResult) = textToBeReplaced(directive)
        return if (textToBeReplaced != textToReplace) {
            val trailingCodeFound = trailingMarkdownMatchResult.matches
            if (trailingCodeFound) {
                val message = "Found [${tag()}] tag with incorrect dependency code for '${directive.completeString}'"
                listOf(VerificationError.create(message, file))
            } else {
                val message = "Found [${tag()}] tag with missing dependency code for '${directive.completeString}'"
                listOf(VerificationError.create(message, file))
            }
        } else {
            emptyList()
        }
    }

    private fun textToBeReplaced(directive: RawMarkdownDirective): Pair<String, TrailingMarkdownMatchResult> {
        val remainingContent = directive.remainingMarkupFileContent
        val trailingMarkdownMatchResult = PluginMarkdown.startsWithPluginMarkdown(remainingContent)
        val text = if (trailingMarkdownMatchResult.matches) {
            "${directive.completeString}${trailingMarkdownMatchResult.content}"
        } else {
            directive.completeString
        }
        return Pair(text, trailingMarkdownMatchResult)
    }
}
