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

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationDirectory.Companion.aDocumentationDirectory
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationDirectory.Companion.isDocumentationDirectory
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationMarkdownFile.Companion.aDocumentationMarkdownFile
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationMarkdownFile.Companion.isDocumentationMarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.processing.ProcessingResult
import de.quantummaid.documaid.processing.ProcessingVisitorAdapter

class HugoDocumentationProcessor : ProcessingVisitorAdapter() {

    override fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
        if (isDocumentationDirectory(directory)) {
            val documentationDirectory = aDocumentationDirectory(directory)
            documentationDirectory.calculateTargetPath(project)
            documentationDirectory.calculateWeight(project)
        }
    }

    override fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
        if (isDocumentationMarkdownFile(file)) {
            val documentationMarkdownFile = aDocumentationMarkdownFile(file as MarkdownFile)
            documentationMarkdownFile.calculateTargetPath(project)
            documentationMarkdownFile.calculateWeight(project)
        }
    }

    override fun afterDirectoryProcessing(
        directory: Directory,
        project: Project,
        goal: Goal,
        directoryProcessingResults: MutableList<ProcessingResult>
    ) {
        if (isDocumentationDirectory(directory)) {
            val documentationDirectory = aDocumentationDirectory(directory)
            val processingResultIfFileCreated = documentationDirectory.generateHugoIndexFileIfNotPresent()
            if (processingResultIfFileCreated != null) {
                directoryProcessingResults.add(processingResultIfFileCreated)
            }
        }
    }
}
