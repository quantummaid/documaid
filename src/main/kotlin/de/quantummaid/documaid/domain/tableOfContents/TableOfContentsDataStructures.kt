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

package de.quantummaid.documaid.domain.tableOfContents

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileObject
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.domain.markdown.tableOfContents.TableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.errors.VerificationError
import java.nio.file.Path

class TocRootDirectory private constructor(val children: List<TocDataFileObject>) {

    companion object {
        fun create(directory: Directory, children: List<TocDataFileObject>): Pair<TocRootDirectory?, List<VerificationError>> {
            return try {
                val sortedChildren = children.sortedBy { it.index }
                val dirAbsolutePath = directory.absolutePath()
                verifyIndicesCorrect(sortedChildren, dirAbsolutePath)
                val rootDirectory = TocRootDirectory(sortedChildren)
                return Pair(rootDirectory, emptyList())
            } catch (e: Exception) {
                val verificationError = VerificationError.createFromException(e, directory)
                Pair(null, listOf(verificationError))
            }
        }
    }
}

open class TocDataFileObject(val index: Int, val title: String, val scanRootRelativeDirectory: Path, val fileObject: FileObject) {
    val absolutePath = fileObject.absolutePath()
    val fileName = absolutePath.fileName.toString()
}

class TocDataDirectory internal constructor(
    index: Int,
    title: String,
    scanRootRelativeDirectory: Path,
    directory: Directory,
    val children: List<TocDataFileObject>
) : TocDataFileObject(index, title, scanRootRelativeDirectory, directory) {

    companion object {
        fun create(directory: Directory, scanBaseDir: Path, children: List<TocDataFileObject>): Pair<TocDataDirectory?, List<VerificationError>> {
            return try {
                val dirAbsolutePath = directory.absolutePath()
                val fileName = dirAbsolutePath.fileName.toString()
                val (index, title) = parseName(fileName)
                val sortedChildren = children.sortedBy { it.index }
                verifyIndicesCorrect(sortedChildren, dirAbsolutePath)
                val relativePath = scanBaseDir.relativize(dirAbsolutePath)
                val tocDataDirectory = TocDataDirectory(index, title, relativePath, directory, sortedChildren)
                Pair(tocDataDirectory, emptyList())
            } catch (e: Exception) {
                val verificationError = VerificationError.createFromException(e, directory)
                Pair(null, listOf(verificationError))
            }
        }
    }
}

class TocDataFile(
    index: Int,
    title: String,
    scanRootRelativeDirectory: Path,
    file: ProjectFile
) : TocDataFileObject(index, title, scanRootRelativeDirectory, file) {
    companion object {
        fun create(file: ProjectFile, scanBaseDir: Path): Pair<TocDataFile?, List<VerificationError>> {
            return try {
                val fileAbsolutePath = file.absolutePath()
                val fileName = fileAbsolutePath.fileName.toString()
                val (index, title) = parseName(fileName)
                val relativePath = scanBaseDir.relativize(fileAbsolutePath)
                val tocDataFile = TocDataFile(index, title, relativePath, file)
                Pair(tocDataFile, emptyList())
            } catch (e: Exception) {
                val verificationError = VerificationError.createFromException(e, file)
                Pair(null, listOf(verificationError))
            }
        }
    }
}

fun parseName(name: String): Pair<Int, String> {
    val matchResult = TableOfContentsMarkdownTagHandler.INDEX_MARKDOWN_FILE_NAME_PATTERN.matchEntire(name)
    if (matchResult != null) {
        val (indexString, _) = matchResult.groups["index"]!!
        val index = Integer.parseInt(indexString)
        val (title, _) = matchResult.groups["name"]!!
        val normalCaseName = convertToNormalCase(title)
        return Pair(index, normalCaseName)
    } else {
        throw IllegalArgumentException("[$TOC_TAG] Cannot parse Toc indexed name '$name'")
    }
}

fun convertToNormalCase(name: String): String {
    val nameWithOutFirstCharacter = name.substring(1)
    val normalCaseRest = nameWithOutFirstCharacter.replace("""\p{Lu}""".toRegex()) { matchResult -> " " + matchResult.value.toLowerCase() }
    return name[0].toUpperCase() + normalCaseRest
}

private fun verifyIndicesCorrect(list: List<TocDataFileObject>, absolutePath: Path) {
    val array = list.toTypedArray()
    for (i in 0 until array.size) {
        val currentIndex = array[i].index
        val expectedIndex = i + 1
        if (currentIndex > expectedIndex) {
            throw IllegalArgumentException("[$TOC_TAG] Missing index $expectedIndex for TOC in directory '${absolutePath.fileName}'")
        }
        if (currentIndex < expectedIndex) {
            throw IllegalArgumentException("[$TOC_TAG] File '${array[i - 1].fileName}' has same TOC index as '${array[i].fileName}'")
        }
    }
}
