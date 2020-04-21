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
package de.quantummaid.documaid.generating

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.paths.makeRelativeTo
import de.quantummaid.documaid.domain.paths.pathFileNameMatchesFileNameExactly
import de.quantummaid.documaid.domain.paths.pathMatchesFileRegex
import de.quantummaid.documaid.generating.GenerationFlavorType.Companion.generationTypeForString
import de.quantummaid.documaid.processing.ProcessingResult
import java.nio.file.Path

enum class GenerationFlavorType {
    NO_FLAVOR,
    QUANTUMMAID;

    companion object {
        fun generationTypeForString(generationFlavorType: String?): GenerationFlavorType {
            return when (generationFlavorType?.toLowerCase()) {
                "quantummaid" -> GenerationFlavorType.QUANTUMMAID
                else -> GenerationFlavorType.NO_FLAVOR
            }
        }
    }
}

interface GenerationFlavor {

    fun process(processingResult: ProcessingResult): ProcessingResult?

    companion object {

        fun createFor(docuMaidConfiguration: DocuMaidConfiguration): GenerationFlavor {
            val type = generationTypeForString(docuMaidConfiguration.generationFlavorType)
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

    private val DO_NOT_GENERATE_FILE = null

    override fun process(processingResult: ProcessingResult): ProcessingResult? {
        val file = processingResult.file
        if (file is MarkdownFile) {
            val relativePath = makeRelativeTo(file, projectBasePath)
            if (pathFileNameMatchesFileNameExactly(relativePath, "README.md")) {
                DO_NOT_GENERATE_FILE
            }
            if (pathMatchesFileRegex(relativePath, "README.*?\\.md")) {
                return DO_NOT_GENERATE_FILE
            }
            if (!file.hasDataFor(DOCUMENTATION_GEN_INFO_KEY)) {
                return DO_NOT_GENERATE_FILE
            }
            return processingResult
        } else {
            return DO_NOT_GENERATE_FILE
        }
    }
}
