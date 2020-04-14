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

package de.quantummaid.documaid.shared.filesystem

import java.nio.file.Path
import java.nio.file.Paths

class PhysicalFileSystemStructureBuilder internal constructor(val physicalDirectoryBuilder: PhysicalDirectoryBuilder) {

    companion object {
        fun aPhysicalFileStructureIn(directoryName: String): PhysicalFileSystemStructureBuilder {
            val physicalDirectoryBuilder = PhysicalDirectoryBuilder.aDirectory(directoryName)
            return PhysicalFileSystemStructureBuilder(physicalDirectoryBuilder)
        }
    }

    fun with(vararg physicalFileObjectBuilders: PhysicalFileObjectBuilder): PhysicalFileSystemStructureBuilder {
        physicalFileObjectBuilders.forEach {
            physicalDirectoryBuilder.with(it)
        }
        return this
    }

    fun create(): PhysicalFileSystemStructure {
        val currentWorkingDir = Paths.get(System.getProperty("user.dir"))
        val rootDirectory = physicalDirectoryBuilder.create(currentWorkingDir)
        return PhysicalFileSystemStructure(rootDirectory)
    }

    fun construct(): PhysicalFileSystemStructure {
        val currentWorkingDir = Paths.get(System.getProperty("user.dir"))
        val rootDirectory = physicalDirectoryBuilder.construct(currentWorkingDir)
        return PhysicalFileSystemStructure(rootDirectory)
    }
}

class PhysicalFileSystemStructure internal constructor(val baseDirectory: PhysicalDirectory) {

    fun cleanUp() {
        baseDirectory.cleanUp()
    }
}

interface PhysicalFileObjectBuilder {
    fun create(parentPath: Path): PhysicalFileObject
    fun construct(parentPath: Path): PhysicalFileObject
}

abstract class PhysicalFileObject(val path: Path) {
    val name = path.fileName.toString()
    abstract fun cleanUp()
}

class PhysicalDirectoryBuilder private constructor(private val name: String) : PhysicalFileObjectBuilder {

    private val children = ArrayList<PhysicalFileObjectBuilder>()

    companion object {
        fun aDirectory(name: String): PhysicalDirectoryBuilder {
            return PhysicalDirectoryBuilder(name)
        }
    }

    fun with(vararg physicalFileObjectBuilders: PhysicalFileObjectBuilder): PhysicalDirectoryBuilder {
        physicalFileObjectBuilders.forEach {
            children.add(it)
        }
        return this
    }

    override fun create(parentPath: Path): PhysicalDirectory {
        val path = parentPath.resolve(name)
        createDirectory(path)
        val childrenObjects = children
            .map { it.create(path) }
        return PhysicalDirectory(path, childrenObjects.toMutableList())
    }

    override fun construct(parentPath: Path): PhysicalDirectory {
        val path = parentPath.resolve(name)
        val childrenObjects = children
            .map { it.construct(path) }
        return PhysicalDirectory(path, childrenObjects.toMutableList())
    }
}

class PhysicalDirectory internal constructor(
    path: Path,
    val children: MutableList<PhysicalFileObject>
) : PhysicalFileObject(path) {

    fun add(children: List<PhysicalFileObject>) {
        this.children.addAll(children)
    }

    override fun cleanUp() {
        children.forEach { it.cleanUp() }
        deleteDirectory(path)
    }
}

class PhysicalFileBuilder private constructor(val name: String) : PhysicalFileObjectBuilder {

    var content: String = ""

    companion object {
        fun aFile(name: String): PhysicalFileBuilder {
            return PhysicalFileBuilder(name)
        }
    }

    fun withContent(content: String): PhysicalFileBuilder {
        this.content = content
        return this
    }

    override fun create(parentPath: Path): PhysicalFileObject {
        val path = parentPath.resolve(name)
        createFileWithContent(path, content)
        return PhysicalFile(path, content)
    }

    override fun construct(parentPath: Path): PhysicalFileObject {
        val path = parentPath.resolve(name)
        return PhysicalFile(path, content)
    }
}

class PhysicalFile internal constructor(path: Path, val content: String) : PhysicalFileObject(path) {

    override fun cleanUp() {
        deleteFileIfExisting(path)
    }
}
