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
package de.quantummaid.documaid.domain.paths

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import java.nio.file.Path

fun makeRelativeTo(directory: Directory, project: Project): Path {
    val dirPath = directory.absolutePath()
    val rootDir = project.rootDirectory.absolutePath()
    return rootDir.relativize(dirPath)
}

fun makeRelativeTo(projectFile: ProjectFile, rootDir: Path): Path {
    val dirPath = projectFile.absolutePath()
    return rootDir.relativize(dirPath)
}

fun makeRelativeTo(absolutePath: Path, rootDir: Path): Path {
    return rootDir.relativize(absolutePath)
}

fun pathUnderTopLevelDirectory(projectRelativePath: Path, directoryName: String): Boolean {
    return if (directoryName.endsWith("/")) {
        projectRelativePath.startsWith(directoryName)
    } else {
        projectRelativePath.startsWith("$directoryName/")
    }
}

fun stripTopLevelDirectoryFromRelativePath(path: Path): Path {
    val rootDirectory = path.getName(0)
    return rootDirectory.relativize(path)
}

fun pathMatchesFileNameExactly(path: Path, fileName: String): Boolean {
    return path.fileName.toString() == fileName
}

fun pathFileNameMatchesFileNameExactly(path: Path, fileName: String): Boolean {
    return path.toString() == fileName
}

fun pathMatchesFileRegex(path: Path, fileNameRegex: String): Boolean {
    return if (path.nameCount == 1) {
        val regex = Regex(fileNameRegex)
        regex.matches(path.toString())
    } else {
        false
    }
}
