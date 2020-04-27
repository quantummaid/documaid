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

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.generating.GenerationFlavorType
import java.nio.file.Path

class SutFileStructure internal constructor() {
    private val children = mutableListOf<SutFileObject>()
    private var basePath: Path? = null
    private var overriddenExpectedFileStructure: PhysicalFileSystemStructure? = null

    companion object {
        fun aFileStructureForDocuMaidToProcess(): SutFileStructure {
            return SutFileStructure()
        }
    }

    fun with(vararg children: SutFileObject): SutFileStructure {
        this.children.addAll(children.toList())
        return this
    }

    fun inDirectory(temporaryTestDirectory: TemporaryTestDirectory): SutFileStructure {
        this.basePath = temporaryTestDirectory.path.toAbsolutePath()
        return this
    }

    fun generateFileStructureForDocuMaidToProcess(): PhysicalFileSystemStructure {
        return generate(basePath!!)
    }

    fun constructExpectedFileStructureForGithub(): PhysicalFileSystemStructure {
        return construct(basePath!!, ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_GITHUB)
    }

    fun constructExpectedFileStructureForHugo(config: DocuMaidConfiguration): PhysicalFileSystemStructure {
        val documentationDirectory = children.filterIsInstance(SutDirectory::class.java)
            .find { it.name == HugoDocumentationAssumptions.DOCUMENTATION_DIRECTORY }
        val generationFlavorType = config.generationFlavorType
        return if (generationFlavorType == GenerationFlavorType.QUANTUMMAID.name && documentationDirectory != null) {
            this.children.clear()
            this.children.addAll(documentationDirectory.children)
            val hugoOutputPath = basePath!!.resolve(config.hugoOutputPath)
            construct(hugoOutputPath, ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_HUGO)
        } else {
            val hugoOutputPath = basePath!!.resolve(config.hugoOutputPath)
            construct(hugoOutputPath, ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_HUGO)
        }
    }

    private fun generate(basePath: Path): PhysicalFileSystemStructure {
        val parentPath = basePath.parent
        createDirectoryAndParentsIfNotExisting(parentPath)

        val fileName = basePath.toFile().name
        val rootDirectory = SutDirectory.aDirectory(fileName)
            .with(children)
            .generate(parentPath)
        return PhysicalFileSystemStructure(rootDirectory)
    }

    private fun construct(
        basePath: Path,
        constructionForPlatformType: ConstructionForPlatformType
    ): PhysicalFileSystemStructure {
        if (overriddenExpectedFileStructure != null) {
            return overriddenExpectedFileStructure!!
        }

        val parentPath = basePath.parent
        createDirectoryAndParentsIfNotExisting(parentPath)

        val fileName = basePath.toFile().name
        val rootDirectory = SutDirectory.aDirectory(fileName)
            .with(children)
            .construct(parentPath, constructionForPlatformType)
        return PhysicalFileSystemStructure(rootDirectory!!)
    }

    fun cleanUp() {
        deleteDirectoryAndChildren(basePath!!)
    }

    fun overrideExpectedFileStructure(expectedFileStructure: PhysicalFileSystemStructure) {
        this.overriddenExpectedFileStructure = expectedFileStructure
    }
}

enum class ConstructionForPlatformType {
    EXPECTED_OUTPUT_FOR_GITHUB,
    EXPECTED_OUTPUT_FOR_HUGO,
}

interface SutFileObject {

    fun generate(parentPath: Path): PhysicalFileObject
    fun construct(parentPath: Path, constructionForPlatformType: ConstructionForPlatformType): PhysicalFileObject?
}

class SutDirectory private constructor(val name: String) : SutFileObject {

    val children = ArrayList<SutFileObject>()

    companion object {
        fun aDirectory(name: String): SutDirectory {
            return SutDirectory(name)
        }
    }

    fun with(vararg children: SutFileObject): SutDirectory {
        this.children.addAll(children)
        return this
    }

    fun with(children: List<SutFileObject>): SutDirectory {
        this.children.addAll(children)
        return this
    }

    override fun generate(parentPath: Path): PhysicalDirectory {
        val physicalDirectory = PhysicalDirectoryBuilder.aDirectory(name)
            .create(parentPath)
        val physicalChildren = children.map { it.generate(physicalDirectory.path) }
        physicalDirectory.add(physicalChildren)
        return physicalDirectory
    }

    override fun construct(
        parentPath: Path,
        constructionForPlatformType: ConstructionForPlatformType
    ): PhysicalDirectory? {
        val physicalDirectory = PhysicalDirectoryBuilder.aDirectory(name)
            .construct(parentPath)
        val physicalChildren = children.map { it.construct(physicalDirectory.path, constructionForPlatformType) }
            .filterNotNull()
        physicalDirectory.add(physicalChildren)
        return when (constructionForPlatformType) {
            ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_GITHUB -> {
                physicalDirectory
            }
            ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_HUGO -> {
                if (physicalChildren.isNotEmpty()) {
                    physicalDirectory
                } else {
                    null
                }
            }
        }
    }
}

interface ProcessedFile : SutFileObject {
    fun originalFile(): PhysicalFileBuilder

    fun unprocessedFile(): PhysicalFileBuilder

    fun processedFile(): PhysicalFileBuilder

    fun processedFileInHugoFormat(): PhysicalFileBuilder
}

class ProcessedFileBuilder {
    private var originalFile: PhysicalFileBuilder? = null
    private var docuMaidedFile: PhysicalFileBuilder? = null

    private var docuMaidedFileInHugoFormat: PhysicalFileBuilder? = null

    companion object {
        fun anExpectedFile(): ProcessedFileBuilder {
            return ProcessedFileBuilder()
        }
    }

    fun withOriginalNameAndContent(name: String, content: String): ProcessedFileBuilder {
        originalFile = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun withProcessedNameAndContent(name: String, content: String): ProcessedFileBuilder {
        docuMaidedFile = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun withProcessedNameAndContentInHugoFormat(name: String, content: String): ProcessedFileBuilder {
        docuMaidedFileInHugoFormat = PhysicalFileBuilder.aFile(name)
            .withContent(content)
        return this
    }

    fun build(): ProcessedFile {
        return SimpleProcessedFile(originalFile!!, docuMaidedFile!!, docuMaidedFileInHugoFormat!!)
    }
}

open class SimpleProcessedFile(
    private val originalFile: PhysicalFileBuilder,
    private val processedFile: PhysicalFileBuilder,
    private val processedFileInHugoFormat: PhysicalFileBuilder
) : ProcessedFile {

    override fun originalFile(): PhysicalFileBuilder {
        return originalFile
    }

    override fun unprocessedFile(): PhysicalFileBuilder {
        return originalFile
    }

    override fun processedFile(): PhysicalFileBuilder {
        return processedFile
    }

    override fun processedFileInHugoFormat(): PhysicalFileBuilder {
        return processedFileInHugoFormat
    }

    override fun generate(parentPath: Path): PhysicalFileObject {
        return originalFile.create(parentPath)
    }

    override fun construct(
        parentPath: Path,
        constructionForPlatformType: ConstructionForPlatformType
    ): PhysicalFileObject? {
        return when (constructionForPlatformType) {
            ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_GITHUB -> processedFile.construct(parentPath)
            ConstructionForPlatformType.EXPECTED_OUTPUT_FOR_HUGO -> {
                return if (originalFile.name.endsWith(".md")) {
                    processedFileInHugoFormat.construct(parentPath)
                } else {
                    null
                }
            }
        }
    }
}

open class NotProcessedSourceFile(
    notChangingFileBuilder: PhysicalFileBuilder
) : SimpleProcessedFile(notChangingFileBuilder, notChangingFileBuilder, notChangingFileBuilder)

open class EmptySutFile(
    notChangingFileBuilder: PhysicalFileBuilder
) : SimpleProcessedFile(notChangingFileBuilder, notChangingFileBuilder, notChangingFileBuilder) {

    companion object {
        fun aFile(fileName: String): EmptySutFile {
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
            return EmptySutFile(fileBuilder)
        }
    }
}
