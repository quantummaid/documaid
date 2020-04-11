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

package de.quantummaid.documaid.domain.markdown.tagBased.link

import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.link.LinkDirective.Companion.LINK_TAG
import de.quantummaid.documaid.errors.DocuMaidException
import java.nio.file.Path
import java.nio.file.Paths

class LinkDirective private constructor(val directive: RawMarkdownDirective, val options: LinkDirectiveOptions) {

    companion object {
        val LINK_TAG = DirectiveTag("Link")

        fun create(directive: RawMarkdownDirective, file: MarkdownFile, project: Project): LinkDirective {
            val options = LinkDirectiveOptions.create(directive, file)
            val rootRelativeTargetPath = options.rootDirRelativePath
            val lookUpTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
            if (!lookUpTable.fileExists(rootRelativeTargetPath)) {
                throw DocuMaidException.create("Found [$LINK_TAG] tag to not existing file '$rootRelativeTargetPath'", file)
            }
            return LinkDirective(directive, options)
        }
    }
}

data class LinkDirectiveOptions(val rootDirRelativePath: Path, val originalPathString: String, val name: String) {
    companion object {
        fun create(directive: RawMarkdownDirective, file: MarkdownFile): LinkDirectiveOptions {
            val optionsRegex = " *\\(?[\\s]*(?<path>[^ ]*)[\\s]*(?<name>([\\w]*|\"[^\"]*\"))[\\s]*\\)? *".toRegex()
            val options = directive.optionsString.value
            val matchResult = optionsRegex.matchEntire(options)
            if (matchResult != null) {
                val pathString = matchResult.groups["path"]!!.value
                val path = Paths.get(pathString)
                val directory = file.absolutePath()
                    .parent
                val absolutePath = directory.resolve(path)
                    .normalize()
                    .toAbsolutePath()

                var name = matchResult.groups["name"]!!.value
                if (name.startsWith("\"")) {
                    name = name.substring(1, name.length - 1)
                }
                return LinkDirectiveOptions(absolutePath, pathString, name)
            } else {
                throw DocuMaidException.create("Found [$LINK_TAG] directive with not parsable options '${directive.completeString}'", file)
            }
        }
    }
}
