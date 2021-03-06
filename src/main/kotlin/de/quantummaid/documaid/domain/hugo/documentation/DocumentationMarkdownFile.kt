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

import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentationWeights.HugoDirectoryWeightPadder
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.paths.IndexedPath

class DocumentationMarkdownFile private constructor(private val file: MarkdownFile) {
    companion object {

        fun isDocumentationMarkdownFile(file: ProjectFile): Boolean {
            return file.hasDataFor(DOCUMENTATION_GEN_INFO_KEY)
        }

        fun aDocumentationMarkdownFile(file: MarkdownFile): DocumentationMarkdownFile {
            return DocumentationMarkdownFile(file)
        }
    }

    fun calculateWeight(project: Project) {
        val parentDocumentationDirectory = getParent(project)
        val generationInformation = file.getData(DOCUMENTATION_GEN_INFO_KEY)

        val levelWithinDocumentationHierarchy = parentDocumentationDirectory.getLevelWithinDocumentationHierarchy()
        generationInformation.levelWithinDocumentation = levelWithinDocumentationHierarchy

        val parentWeightPrefix = parentDocumentationDirectory.getWeighPrefix()
        val index = getFileIndex()
        val paddedIndex = HugoDirectoryWeightPadder.padIndex(index)
        val fillingZeros = fillRemainingLevelsWithZeros(generationInformation, project)
        generationInformation.weight = "$parentWeightPrefix$paddedIndex$fillingZeros"
    }

    fun calculateTargetPath(project: Project) {
        val parentDocumentatinDirectory = getParent(project)
        val targetPath = parentDocumentatinDirectory.resolveInTargetPath(file.name())
        val generationInformation = file.getData(DOCUMENTATION_GEN_INFO_KEY)
        generationInformation.targetPath = targetPath
    }

    private fun getParent(project: Project): DocumentationDirectory {
        val absolutePath = file.absolutePath()
        val lookUpTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
        val parent = lookUpTable.getFileObject(absolutePath.parent)
        if (parent != null) {
            return DocumentationDirectory.aDocumentationDirectory(parent as Directory)
        } else {
            throw IllegalArgumentException("Could not obtain parent ${absolutePath.parent} for $absolutePath")
        }
    }

    private fun getFileIndex(): Int {
        val absolutePath = file.absolutePath()
        return if (IndexedPath.isIndexedPath(absolutePath)) {
            val indexedPath = IndexedPath.anIndexedPath(absolutePath)
            indexedPath.index
        } else {
            0
        }
    }

    private fun fillRemainingLevelsWithZeros(
        generationInformation: HugoDocumentationGenerationInformation,
        project: Project
    ): String {
        val zeroPadded = HugoDirectoryWeightPadder.padIndex(0)
        val docuMaxLevel = project.getInformation(HugoDocumentationGenerationInformation.DOCUMENTATION_MAX_LEVEL)
        val levelsToFillUp = docuMaxLevel - generationInformation.levelWithinDocumentation!!
        return zeroPadded.repeat(levelsToFillUp)
    }
}
