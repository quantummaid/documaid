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
package de.quantummaid.documaid.preparing.duplicateSnippets

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.tagBased.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.PreparingVisitor

class DuplicateSnippetsChecker : PreparingVisitor {
    private val snippetsMap = HashMap<SnippetId, MutableList<ProjectFile>>()

    override fun prepareFile(file: ProjectFile, project: Project): List<VerificationError> {
        file.snippets()
            .forEach {
                if (snippetsMap.containsKey(it.id)) {
                    val list = snippetsMap.get(it.id)
                    list!!.add(file)
                } else {
                    snippetsMap.put(it.id, mutableListOf(file))
                }
            }
        return emptyList()
    }

    override fun finishPreparation(project: Project): List<VerificationError> {
        return snippetsMap
            .filter { it.value.size >= 2 }
            .toSortedMap(compareBy { it.value })
            .map {
                val filesListing = it.value
                    .sortedBy { projectFile -> projectFile.absolutePath().toString() }
                    .joinToString(separator = ", ") { projectFile -> projectFile.absolutePath().toString() }
                val message = "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet '${it.key.value}': $filesListing"
                VerificationError.createWithoutFileOrigin(message)
            }
    }
}
