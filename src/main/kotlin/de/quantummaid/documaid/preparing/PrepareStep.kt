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
package de.quantummaid.documaid.preparing

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.duplicateSnippets.DuplicateSnippetsChecker
import de.quantummaid.documaid.preparing.navigation.NavigationPreparer
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsPreparer

class PrepareStep private constructor(private val visitors: List<PreparingVisitor>) {

    companion object {
        fun create(): PrepareStep {
            val visitors = listOf(
                TableOfContentsPreparer(),
                NavigationPreparer(),
                DuplicateSnippetsChecker()
            )
            return PrepareStep(visitors)
        }
    }

    fun prepare(project: Project): List<VerificationError> {
        visitors.forEach { it.startPreparation(project) }
        val directory = project.rootDirectory
        val errors = prepareDirectory(directory, project)
        val finishErrors = visitors.flatMap { it.finishPreparation(project) }
        return errors.plus(finishErrors)
    }

    private fun prepareDirectory(directory: Directory, project: Project): List<VerificationError> {
        val preErrors = visitors.flatMap { it.beforeDirectoryPreparing(directory, project) }
        if (preErrors.isNotEmpty()) {
            return preErrors
        }

        val errors = directory.children()
            .flatMap {
                when (it) {
                    is Directory -> prepareDirectory(it, project)
                    is ProjectFile -> prepareFile(it, project)
                    else -> emptyList()
                }
            }
        if (errors.isNotEmpty()) {
            return errors
        }
        return visitors.flatMap { it.afterDirectoryPreparing(directory, project) }
    }

    private fun prepareFile(file: ProjectFile, project: Project): List<VerificationError> {
        return visitors.flatMap { it.prepareFile(file, project) }
    }
}
