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

package de.quantummaid.documaid.preparing.tableOfContents

import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.domain.tableOfContents.TableOfContents

class TableOfContentsLookupData {

    companion object {
        val TOC_LOOKUP_KEY = CollectedInformationKey<TableOfContentsLookupData>("TOC_LOOKUP_KEY")
    }

    private var tableOfContents: TableOfContents? = null

    fun registerTableOfContents(tableOfContents: TableOfContents) {
        this.tableOfContents = tableOfContents
    }

    fun tableOfContentsAvailable(): Boolean {
        return tableOfContents != null
    }

    fun getTableOfContents(): TableOfContents {
        return tableOfContents ?: throw IllegalStateException("Not table of contents was generated")
    }
}
