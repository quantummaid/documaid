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

import de.quantummaid.documaid.domain.markdown.MarkdownFile
import java.nio.file.Path

class TableOfContents private constructor(
    private val rootDirectory: TocRootDirectory,
    private val fileWithTocDirective: MarkdownFile,
    private val linkedFilesList: LinkedFilesList
) {

    companion object {
        fun create(rootDirectory: TocRootDirectory, fileWithTocDirective: MarkdownFile): TableOfContents {
            val linkedFilesList = LinkedFilesList.create(rootDirectory)
            return TableOfContents(rootDirectory, fileWithTocDirective, linkedFilesList)
        }
    }

    fun generate(): String {
        return collectToc(rootDirectory)
    }

    private fun collectToc(tocRootDirectory: TocRootDirectory): String {
        val tocEntries = collectTocEntries(tocRootDirectory)

        val stringBuilder = StringBuilder()
            .append("\n")
        tocEntries.forEach { stringBuilder.append(it.generate()).append("\n") }
        stringBuilder.append("<!---EndOfToc-->")
        return stringBuilder.toString()
    }

    private fun collectTocEntries(rootDirectory: TocRootDirectory): List<TocEntry> {
        return collectChildren(rootDirectory.children, 0)
    }

    private fun collectTocDirectory(directory: TocDataDirectory, indentation: Int): List<TocEntry> {
        return collectChildren(directory.children, indentation)
    }

    private fun collectChildren(children: List<TocDataFileObject>, indentation: Int): List<TocEntry> {
        return children
            .map {
                when (it) {
                    is TocDataDirectory -> {
                        val childEntries = collectTocDirectory(it, indentation + 1)
                        listOf(TocDirectoryEntry(it, indentation)).plus(childEntries)
                    }
                    is TocDataFile -> listOf(TocFileEntry(it, indentation))
                    else -> emptyList()
                }
            }.flatten()
    }

    fun getFilePathsIncludedInToc(): List<Path> {
        val tocEntries = collectTocEntries(rootDirectory)
        return tocEntries
            .filter { it is TocFileEntry }
            .map { it as TocFileEntry }
            .map { it.file.absolutePath }
    }

    fun getFileWithToc(): MarkdownFile = fileWithTocDirective

    fun getFilePredecessor(currentFile: MarkdownFile): MarkdownFile? {
        val fileObject = linkedFilesList.getPredecessor(currentFile.absolutePath())?.fileObject
        return if (fileObject != null) {
            fileObject as MarkdownFile
        } else {
            null
        }
    }

    fun getFileSuccessor(currentFile: MarkdownFile): MarkdownFile? {
        val fileObject = linkedFilesList.getSuccessor(currentFile.absolutePath())?.fileObject
        return if (fileObject != null) {
            fileObject as MarkdownFile
        } else {
            null
        }
    }
}

private abstract class TocEntry {

    companion object {
        const val ONE_LEVEL_INDENTATION_SPACES = "    "
    }

    abstract fun generate(): String
}

private class TocDirectoryEntry(val directory: TocDataDirectory, val indentation: Int) : TocEntry() {

    override fun generate(): String {
        val intendationSpaces = ONE_LEVEL_INDENTATION_SPACES.repeat(indentation)
        return "$intendationSpaces${directory.index}. ${directory.title}"
    }
}

private class TocFileEntry(val file: TocDataFile, val indentation: Int) : TocEntry() {

    override fun generate(): String {
        val intendationSpaces = ONE_LEVEL_INDENTATION_SPACES.repeat(indentation)
        return "$intendationSpaces${file.index}. [${file.title}](${file.scanRootRelativeDirectory})"
    }
}

private class LinkedFilesList(val lookUpMap: HashMap<Path, TocLinkedFile>) {

    companion object {
        fun create(rootDirectory: TocRootDirectory): LinkedFilesList {
            val nextFiles = rootDirectory.children.toMutableList()
            val lookUpMap = HashMap<Path, TocLinkedFile>()
            var currentLinkedFile: TocLinkedFile? = null
            while (nextFiles.isNotEmpty()) {
                val fileObject = nextFiles.removeAt(0)
                if (fileObject is TocDataFile) {
                    val newLinkedFile = TocLinkedFile(fileObject, currentLinkedFile, null)
                    lookUpMap[fileObject.absolutePath] = newLinkedFile
                    if (currentLinkedFile != null) {
                        currentLinkedFile.successor = newLinkedFile
                    }
                    currentLinkedFile = newLinkedFile
                }
                if (fileObject is TocDataDirectory) {
                    val children = fileObject.children
                    for (i in 0 until children.size) {
                        nextFiles.add(i, children[i])
                    }
                }
            }
            return LinkedFilesList(lookUpMap)
        }
    }

    fun getPredecessor(path: Path): TocDataFile? {
        return lookUpMap[path]?.predecessor?.fileObject
    }

    fun getSuccessor(path: Path): TocDataFile? {
        return lookUpMap[path]?.successor?.fileObject
    }
}

private class TocLinkedFile(
    val fileObject: TocDataFile,
    val predecessor: TocLinkedFile?,
    var successor: TocLinkedFile?
)
