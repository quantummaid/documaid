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
package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileObject
import de.quantummaid.documaid.collecting.structure.FileObjectDataKey
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import java.nio.file.Path

data class HugoDocumentationGenerationInformation constructor(
    val originalPath: Path,
    var originalFileObject: FileObject,
    var targetPath: Path? = null,
    var levelWithinDocumentation: Int? = null,
    var weight: String? = null,
    var weightPrefix: String? = null
) {

    companion object {
        val DOCUMENTATION_GEN_INFO_KEY =
            FileObjectDataKey<HugoDocumentationGenerationInformation>("DOCUMENTATION_GEN_INFO_KEY")
        val DOCUMENTATION_MAX_LEVEL = CollectedInformationKey<Int>("DOCUMENTATION_MAX_LEVEL")

        fun hugoGenerationInformationForFile(file: MarkdownFile): HugoDocumentationGenerationInformation {
            val absolutePath = file.absolutePath()
            return HugoDocumentationGenerationInformation(absolutePath, file)
        }

        fun hugoGenerationInformationForDirectory(
            directory: Directory,
            levelWithinDocu: Int
        ): HugoDocumentationGenerationInformation {
            val absolutePath = directory.absolutePath()
            return HugoDocumentationGenerationInformation(absolutePath,
                directory, levelWithinDocumentation = levelWithinDocu)
        }
    }
}
