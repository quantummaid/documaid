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

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions
import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.DOCUMENTATION_DIRECTORY
import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.INDEX_MD_FILE_NAME
import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_MAX_LEVEL
import de.quantummaid.documaid.domain.hugo.documentationWeights.HugoDirectoryWeightPadder
import de.quantummaid.documaid.domain.hugo.documentationWeights.HugoWeight
import de.quantummaid.documaid.domain.paths.IndexedPath
import de.quantummaid.documaid.domain.paths.pathMatchesFileNameExactly
import de.quantummaid.documaid.processing.ProcessingResult
import java.nio.file.Path

interface DocumentationDirectory {

    companion object {
        fun aDocumentationDirectory(directory: Directory): DocumentationDirectory {
            return if (pathMatchesFileNameExactly(directory.absolutePath(), DOCUMENTATION_DIRECTORY)) {
                DocumentationRootDirectory(directory)
            } else {
                NormalDocumentationDirectory(directory)
            }
        }

        fun isDocumentationDirectory(directory: Directory): Boolean {
            return directory.hasDataFor(DOCUMENTATION_GEN_INFO_KEY)
        }
    }

    fun calculateTargetPath(project: Project)

    fun calculateWeight(project: Project)

    fun getWeighPrefix(): String

    fun generateHugoIndexFileIfNotPresent(): ProcessingResult?

    fun resolveInTargetPath(name: String): Path

    fun getLevelWithinDocumentationHierarchy(): Int
}

private abstract class AbstractDocumentationDirectory protected constructor(
    protected val directory: Directory
) : DocumentationDirectory {

    protected fun getParent(project: Project): DocumentationDirectory {
        val absolutePath = directory.absolutePath()
        val lookUpTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
        val parent = lookUpTable.getFileObject(absolutePath.parent)
        if (parent != null) {
            return DocumentationDirectory.aDocumentationDirectory(parent as Directory)
        } else {
            throw IllegalArgumentException("Could not obtain parent ${absolutePath.parent} for $absolutePath")
        }
    }

    protected fun getFileIndex(generationInformation: HugoDocumentationGenerationInformation): Int {
        val targetPath = generationInformation.targetPath
            ?: throw IllegalArgumentException("Can not obtain index for ${generationInformation.originalPath}, " +
                "because 'targetpath' not set in generation information.")
        val indexedPath = IndexedPath.anIndexedPath(targetPath)
        return indexedPath.index
    }

    protected fun fillRemainingLevelsWithZeros(
        generationInformation: HugoDocumentationGenerationInformation,
        project: Project
    ): String {
        val zeroPadded = HugoDirectoryWeightPadder.padIndex(0)
        val docuMaxLevel = project.getInformation(DOCUMENTATION_MAX_LEVEL)
        val levelsToFillUp = docuMaxLevel - generationInformation.levelWithinDocumentation!!
        val increaseByOneDueToTheLevelAddedByTheFilesWithinTheDirectoryItself = levelsToFillUp + 1
        return zeroPadded.repeat(increaseByOneDueToTheLevelAddedByTheFilesWithinTheDirectoryItself)
    }

    protected fun indexFileNotPresent(): Boolean {
        val indexFile = directory.children()
            .firstOrNull { pathMatchesFileNameExactly(it.absolutePath(), INDEX_MD_FILE_NAME) }
        return indexFile == null
    }

    override fun getLevelWithinDocumentationHierarchy(): Int {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        return generationInformation.levelWithinDocumentation!!
    }

    override fun generateHugoIndexFileIfNotPresent(): ProcessingResult? {
        return if (indexFileNotPresent()) {
            val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
            val targetPath = generationInformation.targetPath!!
            val fileName = targetPath.resolve(HugoDocumentationAssumptions.INDEX_MD_FILE_NAME)
            val rawHugoWeight = generationInformation.weight!!
            val hugoWeight = HugoWeight.createForMultiLevelWeight(rawHugoWeight)
            val name = if (IndexedPath.isIndexedPath(targetPath)) {
                IndexedPath.anIndexedPath(targetPath).name
            } else {
                directory.name()
            }
            val (mdFile, content) = HugoIndexedDirectoryMarkdownFile.create(fileName, name, hugoWeight)
            directory.addChild(mdFile)
            ProcessingResult.successfulProcessingResult(mdFile, content)
        } else {
            null
        }
    }
}

private class DocumentationRootDirectory constructor(
    directory: Directory
) : AbstractDocumentationDirectory(directory) {

    override fun calculateTargetPath(project: Project) {
        // nothing to do here, because already set
    }

    override fun calculateWeight(project: Project) {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val index = getFileIndex(generationInformation)
        val paddedIndex = HugoDirectoryWeightPadder.padIndex(index)
        generationInformation.weightPrefix = paddedIndex

        val fillingZeros = fillRemainingLevelsWithZeros(generationInformation, project)
        generationInformation.weight = "$paddedIndex$fillingZeros"
    }

    override fun getWeighPrefix(): String {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val absolutePath = directory.absolutePath()
        return generationInformation.weightPrefix
            ?: throw IllegalArgumentException("Weight prefix for directory $absolutePath was not generated.")
    }

    override fun resolveInTargetPath(name: String): Path {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val targetPath = generationInformation.targetPath ?: throw IllegalStateException(
            "Can not resolve target path, because own target path has not been set in ${directory.absolutePath()}")
        return targetPath.resolve(name)
    }
}

private class NormalDocumentationDirectory constructor(
    directory: Directory
) : AbstractDocumentationDirectory(directory) {

    override fun calculateTargetPath(project: Project) {
        val parentDocumentationDirectory = getParent(project)
        val targetPath = parentDocumentationDirectory.resolveInTargetPath(directory.name())
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        generationInformation.targetPath = targetPath
    }

    override fun calculateWeight(project: Project) {
        val parentDocumentationDirectory = getParent(project)
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val parentWeightPrefix = parentDocumentationDirectory.getWeighPrefix()
        val index = getFileIndex(generationInformation)
        val paddedIndex = HugoDirectoryWeightPadder.padIndex(index)
        generationInformation.weightPrefix = "$parentWeightPrefix$paddedIndex"

        val fillingZeros = fillRemainingLevelsWithZeros(generationInformation, project)
        generationInformation.weight = "$parentWeightPrefix$paddedIndex$fillingZeros"
    }

    override fun getWeighPrefix(): String {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val absolutePath = directory.absolutePath()
        return generationInformation.weightPrefix
            ?: throw IllegalArgumentException("Weight prefix for directory $absolutePath was not generated.")
    }

    override fun resolveInTargetPath(name: String): Path {
        val generationInformation = directory.getData(DOCUMENTATION_GEN_INFO_KEY)
        val targetPath = generationInformation.targetPath ?: throw IllegalStateException(
            "Can not resolve target path, because own target path has not been set in ${directory.absolutePath()}")
        return targetPath.resolve(name)
    }
}
