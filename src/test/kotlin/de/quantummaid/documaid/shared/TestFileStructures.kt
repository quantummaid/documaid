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

package de.quantummaid.documaid.shared

import java.nio.file.Files
import java.nio.file.Path

class TestStructureBuilder(private val basePath: Path) {
    private val children = ArrayList<TestFileObjectBuilder>()

    companion object {
        fun aTestStructureIn(basePath: Path): TestStructureBuilder {
            if (Files.exists(basePath)) {
                throw IllegalArgumentException("Test directory base path already exists")
            }
            createDirectoryAndParents(basePath)
            return TestStructureBuilder(basePath)
        }
    }

    fun with(vararg testFileObjectBuilders: TestFileObjectBuilder): TestStructureBuilder {
        testFileObjectBuilders.forEach {
            children.add(it)
        }
        return this
    }

    fun build(): TestFileStructure {
        val childrenObjects = children
            .map { it.build(basePath) }
        return TestFileStructure(basePath, childrenObjects)
    }
}

class TestFileStructure(val basePath: Path, private val children: List<TestFileObject>) {

    fun cleanUp() {
        children.forEach { it.cleanUp() }
        deleteDirectory(basePath)
    }
}

interface TestFileObjectBuilder {
    fun build(parentPath: Path): TestFileObject
}

abstract class TestFileObject(val path: Path) {
    val name = path.fileName.toString()
    abstract fun cleanUp()
}

class TestDirectoryBuilder(private val name: String) : TestFileObjectBuilder {

    private val children = ArrayList<TestFileObjectBuilder>()

    companion object {
        fun aDirectory(name: String): TestDirectoryBuilder {
            return TestDirectoryBuilder(name)
        }
    }

    fun with(vararg testFileObjectBuilders: TestFileObjectBuilder): TestDirectoryBuilder {
        testFileObjectBuilders.forEach {
            children.add(it)
        }
        return this
    }

    override fun build(parentPath: Path): TestDirectory {
        val path = parentPath.resolve(name)
        createDirectory(path)
        val childrenObjects = children
            .map { it.build(path) }
        return TestDirectory(path, childrenObjects)
    }
}

class TestDirectory(path: Path, val children: List<TestFileObject>) : TestFileObject(path) {
    override fun cleanUp() {
        children.forEach { it.cleanUp() }
        deleteDirectory(path)
    }
}

class TestFileBuilder(private val name: String) : TestFileObjectBuilder {

    private var content: String = ""

    companion object {
        fun aFile(name: String): TestFileBuilder {
            return TestFileBuilder(name)
        }
    }

    fun withContent(content: String): TestFileBuilder {
        this.content = content
        return this
    }

    override fun build(parentPath: Path): TestFileObject {
        val path = parentPath.resolve(name)
        createFileWithContent(path, content)
        return TestFile(path)
    }
}

class TestFile(path: Path) : TestFileObject(path) {

    override fun cleanUp() {
        deleteFileIfExisting(path)
    }
}
