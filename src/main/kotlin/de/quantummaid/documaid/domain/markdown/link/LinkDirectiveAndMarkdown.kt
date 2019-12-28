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

package de.quantummaid.documaid.domain.markdown.link

import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.link.LinkDirective.Companion.LINK_TAG
import de.quantummaid.documaid.errors.VerificationError

class LinkDirectiveAndMarkdown(val linkDirective: LinkDirective, val linkMarkdown: LinkMarkdown) {

    companion object {
        fun create(linkDirective: LinkDirective, file: MarkdownFile, project: Project): Pair<LinkDirectiveAndMarkdown?, List<VerificationError>> {
            val rootRelativeTargetPath = linkDirective.options.rootDirRelativePath
            val lookUpTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
            if (!lookUpTable.fileExists(rootRelativeTargetPath)) {
                return Pair(null, listOf(VerificationError.create("Found [$LINK_TAG] tag to not existing file '$rootRelativeTargetPath'", file)))
            }
            val linkMarkdown = LinkMarkdown(linkDirective.options.name, linkDirective.options.originalPathString)
            val directiveAndMarkdownLink = LinkDirectiveAndMarkdown(linkDirective, linkMarkdown)
            return Pair(directiveAndMarkdownLink, emptyList())
        }
    }

    fun generateMarkdown(): String {
        return "${linkDirective.completeString()}\n${linkMarkdown.markdownString()}"
    }
}
