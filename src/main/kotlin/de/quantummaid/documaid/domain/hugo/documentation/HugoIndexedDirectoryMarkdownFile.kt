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
package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.hugoGenerationInformationForFile
import de.quantummaid.documaid.domain.hugo.documentationWeights.HugoWeight
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.heading.HugoHeadingMarkdown
import java.nio.file.Path

class HugoIndexedDirectoryMarkdownFile {

    companion object {
        fun create(path: Path, directoryName: String, hugoWeight: HugoWeight): Pair<MarkdownFile, String> {
            val hugoHeadingMarkdown = HugoHeadingMarkdown.create(directoryName, hugoWeight)
            val markdown = hugoHeadingMarkdown.generateMarkdownWithSkipParam()
            val markdownFile = MarkdownFile.createFromGeneratedFile(path)
            val generationInformation = hugoGenerationInformationForFile(markdownFile)
            generationInformation.targetPath = path
            markdownFile.setData(DOCUMENTATION_GEN_INFO_KEY, generationInformation)
            return Pair(markdownFile, markdown)
        }
    }
}
