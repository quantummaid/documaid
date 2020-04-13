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

package de.quantummaid.documaid.preparing.navigation

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.NavigationDirective.Companion.NAV_TAG
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.PreparingVisitor
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsLookupData.Companion.TOC_LOOKUP_KEY
import java.nio.file.Path

class NavigationPreparer : PreparingVisitor {
    private val filesWithNavigationDirective: MutableMap<Path, MarkdownFile> = HashMap()

    override fun prepareFile(file: ProjectFile, project: Project): List<VerificationError> {
        if (file is MarkdownFile) {
            val navDirectives = file.markdownDirectivesWithIdentifier(NAV_TAG)
            if (navDirectives.isNotEmpty()) {
                filesWithNavigationDirective.put(file.absolutePath(), file)
            }
        }
        return emptyList()
    }

    override fun finishPreparation(project: Project): List<VerificationError> {
        if (filesWithNavigationDirective.isEmpty()) {
            return emptyList()
        }

        val tableOfContentsLookupData = project.getInformation(TOC_LOOKUP_KEY)
        if (!tableOfContentsLookupData.tableOfContentsAvailable()) {
            return listOf(VerificationError.createWithoutFileOrigin("Found [$NAV_TAG] tags without a [$TOC_TAG]"))
        }

        val tableOfContents = tableOfContentsLookupData.getTableOfContents()
        val filePathsIncludedInToc = tableOfContents.getFilePathsIncludedInToc()
        val filesWithNavButNotIndexed = filesWithNavigationDirective.minus(filePathsIncludedInToc)
        if (filesWithNavButNotIndexed.isNotEmpty()) {
            return filesWithNavButNotIndexed.map {
                VerificationError.create("Found [$NAV_TAG] tag for file not indexed by table of contents", it.value)
            }
        }

        val filesWithoutNavigationDirective = filePathsIncludedInToc.minus(filesWithNavigationDirective.keys)
        if (filesWithoutNavigationDirective.isNotEmpty()) {
            return filesWithoutNavigationDirective.map {
                VerificationError.createForPath("Found file indexed by table of contents but without [$NAV_TAG] tag", it)
            }
        }
        return emptyList()
    }
}
