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
package de.quantummaid.documaid.domain.kotlin

import de.quantummaid.documaid.collecting.structure.FileType
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.snippet.RawSnippet
import de.quantummaid.documaid.domain.snippet.SnippetExtractor
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.processing.ProcessingResult
import de.quantummaid.documaid.processing.ProcessingResult.Companion.contentNotChangedProcessingResult
import java.nio.file.Path

class KotlinFile private constructor(private val path: Path, private val snippets: List<RawSnippet>) : ProjectFile() {

    companion object {
        object KotlinSnippetFormat {
            private const val startTagRegex = "// *Showcase *start *(?<id>\\w+) *\\n"
            private const val endTagRegex = " *// *Showcase *end *\\k<id> *"
            val regex = Regex("(?s)$startTagRegex(?<snippet>.*?(?=$endTagRegex))$endTagRegex\\n?")
        }

        fun create(path: Path): KotlinFile {
            val snippets = SnippetExtractor.createExtractorFor(FileType.KOTLIN)
                .extractSnippets(path, KotlinSnippetFormat.regex)
            return KotlinFile(path, snippets)
        }
    }

    override fun fileType(): FileType {
        return FileType.KOTLIN
    }

    override fun absolutePath(): Path = path

    override fun snippets(): List<RawSnippet> = snippets

    override fun process(project: Project): ProcessingResult {
        return contentNotChangedProcessingResult(this)
    }

    override fun validate(project: Project): List<VerificationError> {
        return emptyList()
    }
}
