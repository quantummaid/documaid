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

import de.quantummaid.documaid.collecting.structure.FileType
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.processing.ProcessingResult
import java.nio.file.Files
import java.nio.file.Path

class GenerationStep private constructor(private val fileGenerator: FileGenerator) {
    companion object {
        fun create(docuMaidConfiguration: DocuMaidConfiguration): GenerationStep {
            val fileGenerator = when (docuMaidConfiguration.platform) {
                Platform.GITHUB -> GithubFileGenerator()
                Platform.HUGO -> HugoFileGenerator.create(docuMaidConfiguration)
            }
            return GenerationStep(fileGenerator)
        }
    }

    fun generate(processingResults: List<ProcessingResult>): List<VerificationError> {
        return fileGenerator.generate(processingResults)
    }
}

internal interface FileGenerator {

    fun generate(processingResults: List<ProcessingResult>): List<VerificationError>
}

internal class GithubFileGenerator : FileGenerator {
    override fun generate(processingResults: List<ProcessingResult>): List<VerificationError> {
        return try {
            processingResults.filter { it.file.fileType() == FileType.MARKDOWN }
                .filter { it.contentChanged }
                .forEach { it.file.absolutePath().toFile().writeText(it.newContent) }
            emptyList()
        } catch (e: Exception) {
            listOf(VerificationError.createFromException(e, null))
        }
    }
}

internal class HugoFileGenerator(
    val basePath: Path,
    val hugoBasePath: Path,
    val generationFlavor: GenerationFlavor
) : FileGenerator {

    companion object {
        fun create(docuMaidConfiguration: DocuMaidConfiguration): HugoFileGenerator {
            val basePath = docuMaidConfiguration.basePath
            val hugoBasePath = basePath.resolve(docuMaidConfiguration.hugoOutputPath)
            val generationFlavor = GenerationFlavor.createFor(docuMaidConfiguration)
            return HugoFileGenerator(basePath, hugoBasePath, generationFlavor)
        }
    }

    override fun generate(processingResults: List<ProcessingResult>): List<VerificationError> {

        return try {
            processingResults.filter { it.file.fileType() == FileType.MARKDOWN }
                .mapNotNull { generationFlavor.process(it) }
                .filter { it.contentChanged }
                .forEach {
                    val absoluteFilePath = it.file.absolutePath()
                    val relativizedPath = basePath.relativize(absoluteFilePath)
                    val targetPath = hugoBasePath.resolve(relativizedPath)
                    createDirectoryAndParentsIfNotExisting(targetPath.parent)
                    targetPath.toFile().writeText(it.newContent)
                }
            emptyList()
        } catch (e: Exception) {
            listOf(VerificationError.createFromException(e, null))
        }
    }

    private fun createDirectoryAndParentsIfNotExisting(path: Path) {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
    }
}
