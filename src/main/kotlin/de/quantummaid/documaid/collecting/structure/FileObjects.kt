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
package de.quantummaid.documaid.collecting.structure

import de.quantummaid.documaid.domain.snippet.RawSnippet
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.errors.DocuMaidException.Companion.aDocuMaidException
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.processing.ProcessingResult
import java.nio.file.Path

interface FileObject {
    fun absolutePath(): Path

    fun name(): String = absolutePath().fileName.toString()

    fun hasDataFor(key: FileObjectDataKey<Any>): Boolean

    fun <T> getData(key: FileObjectDataKey<T>): T

    fun <T> setData(key: FileObjectDataKey<T>, value: T)
}

abstract class FileObjectWithData : FileObject {
    private val data: MutableMap<FileObjectDataKey<Any?>, Any> = HashMap()

    override fun hasDataFor(key: FileObjectDataKey<Any>): Boolean {
        return data[key] != null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getData(key: FileObjectDataKey<T>): T {
        val data = data[key]
        if (data != null) {
            return data as T
        } else {
            throw IllegalArgumentException("Not data set for key $key")
        }
    }

    override fun <T> setData(key: FileObjectDataKey<T>, value: T) {
        data[key] = value as Any
    }
}

data class FileObjectDataKey<out T>(val name: String)

class Directory(private val path: Path, childrenIn: List<FileObject>) : FileObjectWithData() {
    private val children = ArrayList(childrenIn)

    constructor(path: Path) : this(path, ArrayList())

    override fun absolutePath(): Path = path

    fun addChild(child: FileObject) {
        children.add(child)
    }

    fun children(): List<FileObject> = children
}

abstract class ProjectFile : FileObjectWithData() {

    abstract fun fileType(): FileType

    abstract fun snippets(): List<RawSnippet>

    abstract fun process(project: Project): ProcessingResult

    abstract fun validate(project: Project): List<VerificationError>

    fun snippetForId(snippetId: SnippetId): RawSnippet {
        val matchingSnippets = snippets().filter { it.id == snippetId }
            .toList()
        when {
            matchingSnippets.size == 1 -> return matchingSnippets[0]
            matchingSnippets.isEmpty() -> throw aDocuMaidException("Snippet $snippetId not found", this)
            else -> throw aDocuMaidException("Not unique snippet $snippetId found", this)
        }
    }
}

enum class FileType {
    JAVA,
    KOTLIN,
    XML,
    MARKDOWN,
    OTHER
}
