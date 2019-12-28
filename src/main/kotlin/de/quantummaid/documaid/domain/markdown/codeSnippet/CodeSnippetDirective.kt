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

package de.quantummaid.documaid.domain.markdown.codeSnippet

import de.quantummaid.documaid.domain.markdown.DirectiveTag
import de.quantummaid.documaid.domain.markdown.RawMarkdownDirective
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.snippet.SnippetId
import java.nio.file.Path
import java.nio.file.Paths

class CodeSnippetDirective(val rawMarkdownDirective: RawMarkdownDirective, val options: CodeSnippetDirectiveOptions) {

    companion object {
        val CODE_SNIPPET_TAG = DirectiveTag("CodeSnippet")

        fun create(rawMarkdownDirective: RawMarkdownDirective): CodeSnippetDirective {
            val options = CodeSnippetDirectiveOptions.create(rawMarkdownDirective)
            return CodeSnippetDirective(rawMarkdownDirective, options)
        }
    }
}

class CodeSnippetDirectiveOptions(val snippetId: SnippetId?, val filePath: Path?) {

    companion object {
        val OPTIONS_WITH_SNIPPET_ID_REGEX = """ *\(? *(?<id>[\w]*) *\)? *""".toRegex()
        val OPTIONS_WITH_FILE = """ *\(? *file=(?<filePath>[^ )]+) *\)? *""".toRegex()

        fun create(directive: RawMarkdownDirective): CodeSnippetDirectiveOptions {
            val options = directive.optionsString.value
            val matchResult = OPTIONS_WITH_SNIPPET_ID_REGEX.matchEntire(options)
            if (matchResult != null) {
                val snippetId = SnippetId(matchResult.groups["id"]!!.value)
                return CodeSnippetDirectiveOptions(snippetId, null)
            } else {
                val matchResult2 = OPTIONS_WITH_FILE.matchEntire(options)
                if (matchResult2 != null) {
                    val filePathString = matchResult2.groups["filePath"]!!.value
                    val filePath = Paths.get(filePathString)
                    return CodeSnippetDirectiveOptions(null, filePath)
                } else {
                    throw IllegalArgumentException("Found [$CODE_SNIPPET_TAG] tag with not parsable options '${directive.completeString}'")
                }
            }
        }
    }
}
