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
package de.quantummaid.documaid.preparing.tableOfContents

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.GithubTableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.domain.tableOfContents.TableOfContents
import de.quantummaid.documaid.domain.tableOfContents.TableOfContentsCreator
import de.quantummaid.documaid.domain.tableOfContents.TocTraversalDecision
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.PreparingVisitor
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsLookupData.Companion.TOC_LOOKUP_KEY

class TableOfContentsPreparer : PreparingVisitor {
    private val tableOfContentsLookupData = TableOfContentsLookupData()

    override fun startPreparation(project: Project) {
        project.setInformation(TOC_LOOKUP_KEY, tableOfContentsLookupData)
    }

    override fun prepareFile(file: ProjectFile, project: Project): List<VerificationError> {
        return if (file is MarkdownFile) {
            val tocDirectives = file.markdownDirectivesWithIdentifier(TOC_TAG)
            when {
                tocDirectives.isEmpty() -> emptyList()
                tocDirectives.size == 1 -> prepareToc(tocDirectives[0], file, project)
                else -> {
                    val message = "[$TOC_TAG] Found multiple Table of contents in a single file ${file.absolutePath()}"
                    listOf(VerificationError.create(message, file))
                }
            }
        } else {
            emptyList()
        }
    }

    private fun prepareToc(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): List<VerificationError> {
        if (tableOfContentsLookupData.tableOfContentsAvailable()) {
            val message = "[$TOC_TAG] cannot generate Table of Contents for file '${file.absolutePath()}, " +
                "because one has already been created"
            val verificationError = VerificationError.create(message, file)
            return listOf(verificationError)
        }
        val (tableOfContents, errors) = generateToc(directive, file, project)
        if (errors.isNotEmpty() || tableOfContents == null) {
            return errors
        }
        tableOfContentsLookupData.registerTableOfContents(tableOfContents)
        return emptyList()
    }

    private fun generateToc(
        directive: RawMarkdownDirective,
        file: MarkdownFile,
        project: Project
    ): Pair<TableOfContents?, List<VerificationError>> {
        try {
            val tableOfContentsDirective = TableOfContentsDirective.create(directive, file, project)
            val traversalDecision = object : TocTraversalDecision {
                override fun directoryShouldBeTraversed(directory: Directory): Boolean {
                    val fileName = directory.absolutePath().fileName.toString()
                    return GithubTableOfContentsMarkdownTagHandler.INDEX_MARKDOWN_FILE_NAME_PATTERN.matches(fileName)
                }
            }
            val tableOfContentsCollector = TableOfContentsCreator(traversalDecision)
            return tableOfContentsCollector.createFrom(tableOfContentsDirective, file)
        } catch (e: Exception) {
            return Pair(null, listOf(VerificationError.createFromException(e, file)))
        }
    }
}
