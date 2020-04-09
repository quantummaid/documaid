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

package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.shared.PhysicalFileBuilder
import java.nio.file.Paths

class SampleFile(val contentInput: String, val expectedContentOutput: String?, val fileName: String, val baseDirRelativePath: String) {
    companion object {

        fun sampleFile(contentInput: String, expectedContentOutput: String, fileName: String): SampleFile {
            return SampleFile(contentInput, expectedContentOutput, fileName, fileName)
        }

        fun inputOnlySampleFile(contentInput: String, fileName: String): SampleFile {
            return SampleFile(contentInput, null, fileName, fileName)
        }

        fun sampleFileInDirectory(contentInput: String, expectedContentOutput: String, baseDirRelativePath: String): SampleFile {
            val fileName = Paths.get(baseDirRelativePath).fileName.toString()
            return SampleFile(contentInput, expectedContentOutput, fileName, baseDirRelativePath)
        }
    }

    fun asBuilder(): PhysicalFileBuilder {
        return PhysicalFileBuilder.aFile(fileName)
                .withContent(contentInput)
    }
}

class SampleFiles(vararg val files: SampleFile)
