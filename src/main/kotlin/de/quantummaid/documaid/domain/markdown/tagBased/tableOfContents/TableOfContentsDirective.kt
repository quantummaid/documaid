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

package de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents

import de.quantummaid.documaid.collecting.fastLookup.FileObjectsFastLookUpTable
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.tagBased.DirectiveTag
import de.quantummaid.documaid.domain.markdown.tagBased.OptionsString
import de.quantummaid.documaid.domain.markdown.tagBased.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.TableOfContentsDirective.Companion.TOC_TAG
import de.quantummaid.documaid.errors.DocuMaidException
import java.nio.file.Files
import java.nio.file.Path

class TableOfContentsDirective private constructor(
    val options: TableOfContentsDirectiveOptions,
    val file: MarkdownFile,
    val scanBaseDirectory: Directory
) {

    companion object {
        val TOC_TAG = DirectiveTag("TOC")

        fun create(rawMarkdownDirective: RawMarkdownDirective, file: MarkdownFile, project: Project): TableOfContentsDirective {
            val directoryBasePath = file.absolutePath().parent
            val options = TableOfContentsDirectiveOptions.create(rawMarkdownDirective.optionsString, directoryBasePath, file)
            val tocScanBaseDirectory = options.tocScanBaseDirectory
            val lookUpTable = project.getInformation(FileObjectsFastLookUpTable.FILES_LOOKUP_TABLE_KEY)
            val scanBaseDirectory = lookUpTable.getFileObject(tocScanBaseDirectory) as Directory
            return TableOfContentsDirective(options, file, scanBaseDirectory)
        }
    }
}

class TableOfContentsDirectiveOptions(val tocScanBaseDirectory: Path) {

    companion object {
        fun create(optionsString: OptionsString, directoryBasePath: Path, file: MarkdownFile): TableOfContentsDirectiveOptions {
            val optionsRegex = """ *\(? *(?<baseDir>[^ )]+) *\)? *""".toRegex()
            val matchResult = optionsRegex.matchEntire(optionsString.value)
            if (matchResult != null) {
                val baseDirectoryPathString = matchResult.groups["baseDir"]!!.value
                val tocScanBaseDirectory = directoryBasePath.resolve(baseDirectoryPathString)
                    .normalize()
                if (!Files.exists(tocScanBaseDirectory)) {
                    throw DocuMaidException.create("[$TOC_TAG] Cannot create TOC for not existing directory '$baseDirectoryPathString'", file)
                }
                return TableOfContentsDirectiveOptions(tocScanBaseDirectory)
            } else {
                throw DocuMaidException.create("[$TOC_TAG] Could not match TOC options '${optionsString.value}'", file)
            }
        }
    }
}
