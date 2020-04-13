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

package de.quantummaid.documaid.generating

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.processing.ProcessingResult
import java.nio.file.Path
import java.nio.file.Paths

enum class GenerationFlavorType {
    NO_FLAVOR,
    QUANTUMMAID,
}

interface GenerationFlavor {

    fun process(processingResult: ProcessingResult): ProcessingResult?

    companion object {

        fun createFor(docuMaidConfiguration: DocuMaidConfiguration): GenerationFlavor {
            val type = when (docuMaidConfiguration.generationFlavorType?.toLowerCase()) {
                "quantummaid" -> GenerationFlavorType.QUANTUMMAID
                else -> GenerationFlavorType.NO_FLAVOR
            }
            return when (type) {
                GenerationFlavorType.NO_FLAVOR -> NoopGenerationFlavor()
                GenerationFlavorType.QUANTUMMAID -> QuantumMaidGenerationFlavor(docuMaidConfiguration.basePath)
            }
        }
    }
}

private class NoopGenerationFlavor : GenerationFlavor {

    override fun process(processingResult: ProcessingResult): ProcessingResult? {
        return processingResult
    }
}

private class QuantumMaidGenerationFlavor(val projectBasePath: Path) : GenerationFlavor {
    override fun process(processingResult: ProcessingResult): ProcessingResult? {
        if (processingResult.file is MarkdownFile) {
            val path = processingResult.file.absolutePath()
            val relativePath = projectBasePath.relativize(path)
            if (relativePath.startsWith("documentation/")) {
                val rootToRemove = Paths.get("documentation")
                val newPath = rootToRemove.relativize(relativePath)
                val newPathAbsolute = projectBasePath.resolve(newPath)
                val movedFile = processingResult.file.createCopyForPath(newPathAbsolute)
                return ProcessingResult.successfulProcessingResult(movedFile, processingResult.newContent)
            }
            if (relativePath.toString() == "README.md") {
                val absoluteNewPath = projectBasePath.resolve("_index.md")
                val movedFile = processingResult.file.createCopyForPath(absoluteNewPath)
                return ProcessingResult.successfulProcessingResult(movedFile, processingResult.newContent)
            }
        }
        return processingResult
    }
}
