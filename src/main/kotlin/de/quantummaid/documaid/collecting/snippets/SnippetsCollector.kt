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

package de.quantummaid.documaid.collecting.snippets

import de.quantummaid.documaid.collecting.snippets.CodeSnippetsLookupTable.Companion.SNIPPETS_LOOKUP_TABLE_KEY
import de.quantummaid.documaid.collecting.structure.FileObjectVisitorAdapter
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile

class SnippetsCollector : FileObjectVisitorAdapter() {
    private val snippetsLookupTable = CodeSnippetsLookupTable()

    override fun fileVisited(file: ProjectFile) {
        file.snippets()
                .forEach { snippetsLookupTable.registerSnippet(it.id, file.absolutePath()) }
    }

    override fun finishTreeWalk(project: Project) {
        project.addInformation(SNIPPETS_LOOKUP_TABLE_KEY, snippetsLookupTable)
    }
}
