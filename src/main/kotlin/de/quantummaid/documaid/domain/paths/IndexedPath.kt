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

import de.quantummaid.documaid.collecting.structure.ProjectFile
import java.nio.file.Path

class IndexedPath constructor(val name: String, val index: Int) {

    companion object {
        private val INDEXED_FILE_PATTERN = Regex("(?<index>[\\d]+)_+(?<name>.*)")

        fun anIndexedPath(file: ProjectFile): IndexedPath {
            return anIndexedPath(file.name(), file.absolutePath())
        }

        fun anIndexedPath(path: Path): IndexedPath {
            val fileName = path.fileName.toString()
            return anIndexedPath(fileName, path.toAbsolutePath())
        }

        fun anIndexedPath(fileName: String, filePath: Path?): IndexedPath {
            val parsingErrorMessage = "Cannot extract index from file $${filePath ?: fileName}"
            val matchEntire = INDEXED_FILE_PATTERN.matchEntire(fileName)
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val index = matchEntire.groups["index"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val name = matchEntire.groups["name"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val indexInt = Integer.parseInt(index)
            return IndexedPath(name, indexInt)
        }

        fun isIndexedPath(file: ProjectFile): Boolean {
            return isIndexedPath(file.name())
        }

        fun isIndexedPath(path: Path): Boolean {
            val fileName = path.fileName.toString()
            return isIndexedPath(fileName)
        }

        fun isIndexedPath(fileName: String): Boolean {
            val matchEntire = INDEXED_FILE_PATTERN.matchEntire(fileName)
            return matchEntire != null
        }
    }
}
