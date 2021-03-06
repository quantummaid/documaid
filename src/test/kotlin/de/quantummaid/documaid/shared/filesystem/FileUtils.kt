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
package de.quantummaid.documaid.shared.filesystem

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun createFileWithContent(basePath: String, filePath: String, content: String) {
    val path = Paths.get(basePath, filePath).toAbsolutePath()
    createFileWithContent(path, content)
}

fun createFileWithContent(path: Path, content: String) {
    if (Files.exists(path)) {
        throw IllegalArgumentException("Cannot create file $path, because file already exists.")
    }
    Files.write(path, content.toByteArray())
}

fun createDirectory(path: Path) {
    if (Files.exists(path)) {
        throw IllegalArgumentException("Cannot create directory $path, because directory already exists.")
    }
    Files.createDirectory(path)
}

fun createDirectoryAndParents(path: Path) {
    if (Files.exists(path)) {
        throw IllegalArgumentException("Cannot create directory $path, because directory already exists.")
    }
    Files.createDirectories(path)
}

fun createDirectoryAndParentsIfNotExisting(path: Path) {
    if (!Files.exists(path)) {
        Files.createDirectories(path)
    }
}

fun assertFileWithContent(basePath: String, filePath: String, expectedContent: String) {
    val path = Paths.get(basePath, filePath).toAbsolutePath()
    assertFileWithContent(path, expectedContent)
}

fun assertFileWithContent(filePath: Path, expectedContent: String) {
    try {
        val bytes = Files.readAllBytes(filePath)
        val content = String(bytes)
        assertEquals(expectedContent, content, "Wrong content in file $filePath")
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

fun assertDirectoryExists(directoryPath: Path) {
    if (!Files.isDirectory(directoryPath)) {
        throw IllegalStateException("Directory $directoryPath did not exist")
    }
}

fun deleteFileIfExisting(basePath: String, filePath: String) {
    val path = Paths.get(basePath, filePath).toAbsolutePath()
    deleteFileIfExisting(path)
}

fun deleteFileIfExisting(path: Path) {
    Files.deleteIfExists(path)
}

fun deleteDirectory(path: Path) {
    Files.deleteIfExists(path)
}

fun deleteDirectoryAndChildren(path: Path) {
    path.toFile().deleteRecursively()
}

fun deletePath(path: Path) {
    if (Files.isRegularFile(path)) {
        Files.delete(path)
    } else {
        path.toFile().deleteRecursively()
    }
}
