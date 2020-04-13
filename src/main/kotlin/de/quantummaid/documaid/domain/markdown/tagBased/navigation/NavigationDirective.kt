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

package de.quantummaid.documaid.domain.markdown.tagBased.navigation

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.preparing.tableOfContents.TableOfContentsLookupData

class NavigationDirective private constructor(val directive: RawMarkdownDirective, val file: MarkdownFile, val previousFile: MarkdownFile?, val overviewFile: MarkdownFile, val nextFile: MarkdownFile?) {

    companion object {
        val NAV_TAG = DirectiveTag("Nav")

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): NavigationDirective {
            val tableOfContentsLookupData = project.getInformation(TableOfContentsLookupData.TOC_LOOKUP_KEY)
            val tableOfContents = tableOfContentsLookupData.getTableOfContents()
            val overviewFile = tableOfContents.getFileWithToc()
            val previousFile = tableOfContents.getFilePredecessor(file)
            val nextFile = tableOfContents.getFileSuccessor(file)
            return NavigationDirective(rawMarkdownDirective, file, previousFile, overviewFile, nextFile)
        }
    }
}
