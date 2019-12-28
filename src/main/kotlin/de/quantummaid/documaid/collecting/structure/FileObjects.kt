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

package de.quantummaid.documaid.collecting.structure

import de.quantummaid.documaid.domain.snippet.RawSnippet
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.errors.VerificationError
import java.nio.file.Path

interface FileObject {
    fun absolutePath(): Path

    fun name(): String = absolutePath().fileName.toString()
}

class Directory(private val path: Path, childrenIn: List<FileObject>) : FileObject {
    private val children = ArrayList(childrenIn)

    constructor(path: Path) : this(path, ArrayList())

    override fun absolutePath(): Path = path

    fun addChild(child: FileObject) {
        children.add(child)
    }

    fun children(): List<FileObject> = children
}

interface ProjectFile : FileObject {
    fun fileType(): FileType

    fun snippets(): List<RawSnippet>

    fun generate(project: Project): List<VerificationError>

    fun validate(project: Project): List<VerificationError>

    fun snippetForId(snippetId: SnippetId): RawSnippet {
        val matchingSnippets = snippets().filter { it.id == snippetId }
                .toList()
        when {
            matchingSnippets.size == 1 -> return matchingSnippets[0]
            matchingSnippets.isEmpty() -> throw IllegalArgumentException("Snippet $snippetId not found")
            else -> throw IllegalArgumentException("Not unique snippet $snippetId found")
        }
    }
}

enum class FileType {
    JAVA,
    XML,
    MARKDOWN,
    OTHER
}
