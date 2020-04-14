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
package de.quantummaid.documaid.domain.tableOfContents

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileObject
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.GithubTableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective
import de.quantummaid.documaid.errors.VerificationError
import java.nio.file.Path

class TableOfContentsCreator(private val traversalDecision: TocTraversalDecision) {

    fun createFrom(
        tableOfContentsDirective: TableOfContentsDirective,
        fileWithToc: MarkdownFile
    ): Pair<TableOfContents?, List<VerificationError>> {
        val tocDataOnlyDirectory = collectTocData(tableOfContentsDirective.scanBaseDirectory)
        val rootDirectoryToRelateLinksTo = tableOfContentsDirective.file.absolutePath().parent
        val (tocRootDirectory, verificationErrors) =
            mapToTocDataStructure(tocDataOnlyDirectory, rootDirectoryToRelateLinksTo)
        return if (verificationErrors.isNotEmpty() || tocRootDirectory == null) {
            Pair(null, verificationErrors)
        } else {
            val tableOfContents = TableOfContents.create(tocRootDirectory, fileWithToc)
            Pair(tableOfContents, emptyList())
        }
    }

    private fun collectTocData(directory: Directory): Directory {
        return collectTocDirectory(directory)
    }

    private fun collectTocDirectory(directory: Directory): Directory {
        val tocRelevantChildren = directory.children()
            .filter {
                when (it) {
                    is Directory -> traversalDecision.directoryShouldBeTraversed(it)
                    is ProjectFile -> isIndexedMarkdown(it)
                    else -> false
                }
            }
            .map {
                when (it) {
                    is Directory -> collectTocDirectory(it)
                    is MarkdownFile -> it
                    else -> null
                }
            }.map { it!! }
        return Directory(directory.absolutePath(), tocRelevantChildren)
    }

    private fun isIndexedMarkdown(it: FileObject): Boolean {
        val matchesPattern = GithubTableOfContentsMarkdownTagHandler.INDEX_MARKDOWN_FILE_NAME_PATTERN.matches(it.name())
        return it is MarkdownFile && matchesPattern
    }

    private fun mapToTocDataStructure(
        tocDataOnlyDirectory: Directory,
        rootDirectoryToRelateLinksTo: Path
    ): Pair<TocRootDirectory?, List<VerificationError>> {
        val children = tocDataOnlyDirectory.children()
        val (tocDataChildren, errors) = mapTocChildren(children, rootDirectoryToRelateLinksTo)
        return if (errors.isNotEmpty()) {
            Pair(null, errors)
        } else {
            return TocRootDirectory.create(tocDataOnlyDirectory, tocDataChildren)
        }
    }

    private fun mapTocChildren(
        children: List<FileObject>,
        rootDirectoryToRelateLinksTo: Path
    ): Pair<MutableList<TocDataFileObject>, MutableList<VerificationError>> {
        return children
            .map {
                if (it is Directory) {
                    mapToTocDataDirectory(it, rootDirectoryToRelateLinksTo)
                } else {
                    mapToTocDataFile(it as ProjectFile, rootDirectoryToRelateLinksTo)
                }
            }
            .fold(Pair(mutableListOf(), mutableListOf())) { acc, pair ->
                if (pair.first != null) {
                    acc.first.add(pair.first!!)
                }
                acc.second.addAll(pair.second)
                acc
            }
    }

    private fun mapToTocDataDirectory(
        directory: Directory,
        rootDirectoryToRelateLinksTo: Path
    ): Pair<TocDataDirectory?, List<VerificationError>> {
        val children = directory.children()
        val (tocDataChildren, errors) = mapTocChildren(children, rootDirectoryToRelateLinksTo)
        if (errors.isNotEmpty()) {
            return Pair(null, errors)
        }
        return TocDataDirectory.create(directory, rootDirectoryToRelateLinksTo, tocDataChildren)
    }

    private fun mapToTocDataFile(
        file: ProjectFile,
        rootDirectoryToRelateLinksTo: Path
    ): Pair<TocDataFile?, List<VerificationError>> {
        return TocDataFile.create(file, rootDirectoryToRelateLinksTo)
    }
}

interface TocTraversalDecision {

    fun directoryShouldBeTraversed(directory: Directory): Boolean
}
