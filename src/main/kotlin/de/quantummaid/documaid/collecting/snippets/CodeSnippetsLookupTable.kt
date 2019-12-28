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

import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.errors.DocuMaidException
import java.nio.file.Path

class CodeSnippetsLookupTable {
    private val map = HashMap<SnippetId, MutableList<Path>>()

    companion object {
        val SNIPPETS_LOOKUP_TABLE_KEY = CollectedInformationKey<CodeSnippetsLookupTable>("SNIPPETS_LOOKUP_TABLE_KEY")
    }

    fun registerSnippet(snippetId: SnippetId, path: Path) {
        if (map.containsKey(snippetId)) {
            map[snippetId]?.add(path)
        } else {
            map[snippetId] = mutableListOf(path)
        }
    }

    fun uniqueSnippetExists(snippetId: SnippetId): Boolean {
        val list = map[snippetId]
        return list?.size == 1
    }

    fun getUniqueSnippet(snippetId: SnippetId): Path {
        val list = map[snippetId]
        if (list!!.size == 1) {
            return list[0]
        } else {
            throw DocuMaidException.createWithoutFileOrigin("Snippet $snippetId was not unique")
        }
    }
}