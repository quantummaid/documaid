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

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.DOCUMENTATION_DIRECTORY
import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.INDEX_MD_FILE_NAME
import de.quantummaid.documaid.collecting.structure.*
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_MAX_LEVEL
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.hugoGenerationInformationForDirectory
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.hugoGenerationInformationForFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.paths.IndexedPath
import de.quantummaid.documaid.domain.paths.IndexedPath.Companion.isIndexedPath
import de.quantummaid.documaid.domain.paths.makeRelativeTo
import de.quantummaid.documaid.domain.paths.pathMatchesFileNameExactly
import java.nio.file.Path

class HugoDocumentationCollector(
    private val docuMaidConfiguration: DocuMaidConfiguration
) : FileObjectVisitorAdapter() {
    private var maxLevelOfIndexedDocumentationSubDirectories = docuMaidConfiguration.documentationDepth

    companion object {
        val DOCUMENTATION_ROOT_DIRECTORY = CollectedInformationKey<Directory>("DOCUMENTATION_ROOT_DIRECTORY")
    }

    override fun fileVisited(file: ProjectFile) {
        val absolutePath = file.absolutePath()
        if (isDocumentationFileOfInterest(file, absolutePath)) {
            file.setData(DOCUMENTATION_GEN_INFO_KEY, hugoGenerationInformationForFile(file as MarkdownFile))
        }
    }

    private fun isDocumentationFileOfInterest(file: ProjectFile, absolutePath: Path): Boolean {
        return if (file is MarkdownFile) {
            if (isWithinDocumentationDirectory(absolutePath, docuMaidConfiguration)) {
                if (pathMatchesFileNameExactly(absolutePath, INDEX_MD_FILE_NAME)) {
                    true
                } else {
                    !isWithinLegacyDirectory(absolutePath, docuMaidConfiguration) && isIndexedPath(absolutePath)
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun directoryVisited(directory: Directory) {
        val absolutePath = directory.absolutePath()
        if (isWithinDocuDirAndOfInterest(absolutePath)) {
            val levelWithinDocu = calculateLevelWithinDocumentation(absolutePath)
            val generationInformation = hugoGenerationInformationForDirectory(directory, levelWithinDocu)
            directory.setData(DOCUMENTATION_GEN_INFO_KEY, generationInformation)

            maxLevelOfIndexedDocumentationSubDirectories =
                Math.max(levelWithinDocu, maxLevelOfIndexedDocumentationSubDirectories)
        }
    }

    private fun isWithinDocuDirAndOfInterest(absolutePath: Path): Boolean {
        return if (isWithinDocumentationDirectory(absolutePath, docuMaidConfiguration)) {
            if (isDocumentationDirectory(absolutePath, docuMaidConfiguration)) {
                return true
            } else {
                !isWithinLegacyDirectory(absolutePath, docuMaidConfiguration) && isIndexedPath(absolutePath)
            }
        } else {
            false
        }
    }

    private fun calculateLevelWithinDocumentation(absolutePath: Path): Int {
        val rootRelativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
        var level = 0
        for (path in rootRelativePath) {
            if (IndexedPath.isIndexedPath(path)) {
                level++
            }
        }

        if (IndexedPath.isIndexedPath(docuMaidConfiguration.hugoOutputPath)) {
            level++
        }
        return level
    }

    override fun finishTreeWalk(project: Project) {
        val documentationDirectory = project.rootDirectory.children()
            .firstOrNull { pathMatchesFileNameExactly(it.absolutePath(), DOCUMENTATION_DIRECTORY) }
        if (documentationDirectory != null) {
            project.setInformation(DOCUMENTATION_ROOT_DIRECTORY, documentationDirectory)

            val generationInformation = documentationDirectory.getData(DOCUMENTATION_GEN_INFO_KEY)
            generationInformation.targetPath = docuMaidConfiguration.absoluteHugoOutputPath()

            project.setInformation(DOCUMENTATION_MAX_LEVEL, maxLevelOfIndexedDocumentationSubDirectories)
        }
    }
}
