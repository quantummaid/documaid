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

package de.quantummaid.documaid.collecting.fastLookup

import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.collecting.structure.FileObject
import java.nio.file.Path

class FileObjectsFastLookUpTable {
    private val map: MutableMap<Path, FileObject> = HashMap()

    companion object {
        val FILES_LOOKUP_TABLE_KEY = CollectedInformationKey<FileObjectsFastLookUpTable>("FILES_LOOKUP_TABLE_KEY")
    }

    fun addFileObject(path: Path, fileObject: FileObject) {
        map[path] = fileObject
    }

    fun getFileObject(path: Path): FileObject? {
        return map[path]
    }

    fun fileExists(path: Path): Boolean {
        return map.containsKey(path)
    }
}
