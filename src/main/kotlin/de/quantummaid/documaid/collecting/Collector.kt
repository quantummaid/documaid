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

package de.quantummaid.documaid.collecting

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileCreator
import de.quantummaid.documaid.collecting.structure.FileObjectVisitor
import de.quantummaid.documaid.collecting.structure.Project
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

interface Collector {
    fun collectData(basePath: Path, visitors: List<FileObjectVisitor>): Project
}

class FullCollector : Collector {

    override fun collectData(basePath: Path, visitors: List<FileObjectVisitor>): Project {
        val visitor = CollectingFileVisitor(visitors)
        Files.walkFileTree(basePath, visitor)
        val rootDirectory = visitor.getRootDirectory()
        visitors.forEach { it.directoryVisited(rootDirectory) }
        val project = Project.create(rootDirectory)

        visitors.forEach { it.finishTreeWalk(project) }

        return project
    }
}

private class CollectingFileVisitor(val visitors: List<FileObjectVisitor>) : SimpleFileVisitor<Any>() {
    private val currentDirectoryStack: MutableList<Directory> = mutableListOf()

    override fun preVisitDirectory(dir: Any?, attrs: BasicFileAttributes?): FileVisitResult {
        super.preVisitDirectory(dir, attrs)

        val path = Paths.get(dir.toString())
            .toAbsolutePath()
        val directory = Directory(path)

        if (currentDirectoryStack.isNotEmpty()) {
            val parentDirectory = currentDirectoryStack.last()
            parentDirectory.addChild(directory)
        }
        currentDirectoryStack.add(directory)

        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Any?, attrs: BasicFileAttributes?): FileVisitResult {
        super.visitFile(file, attrs)

        val directory = currentDirectoryStack.last()
        val path = Paths.get(file.toString())
            .toAbsolutePath()
        val fileObject = FileCreator.create(path)
        directory.addChild(fileObject)

        visitors.forEach { it.fileVisited(fileObject) }

        return FileVisitResult.CONTINUE
    }

    override fun postVisitDirectory(dir: Any?, exc: IOException?): FileVisitResult {
        super.postVisitDirectory(dir, exc)

        val directory = currentDirectoryStack.last()
        if (currentDirectoryStack.size > 1)
            currentDirectoryStack.removeAt(currentDirectoryStack.lastIndex)

        visitors.forEach { it.directoryVisited(directory) }

        return FileVisitResult.CONTINUE
    }

    fun getRootDirectory(): Directory {
        return currentDirectoryStack.first()
    }
}