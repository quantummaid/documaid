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

package de.quantummaid.documaid.domain.navigation

import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.markdown.link.LinkMarkdown

class NavigationMarkdown(val fileWithDirective: MarkdownFile, val previousFile: MarkdownFile?, val overviewFile: MarkdownFile, val nextFile: MarkdownFile?) {

    fun generateMarkdown(): String {
        return "${previousFileLink()}${overviewLink()}${nextFileLink()}"
    }

    private fun previousFileLink(): String {
        return if (previousFile != null) {
            val relativePath = fileWithDirective.absolutePath().parent.relativize(previousFile.absolutePath())
            val linkMarkdown = LinkMarkdown("&larr;", relativePath.toString())
            "${linkMarkdown.markdownString()}&nbsp;&nbsp;&nbsp;"
        } else {
            ""
        }
    }

    private fun overviewLink(): String {
        val relativePath = fileWithDirective.absolutePath().parent.relativize(overviewFile.absolutePath())
        val linkMarkdown = LinkMarkdown("Overview", relativePath.toString())
        return linkMarkdown.markdownString()
    }

    private fun nextFileLink(): String {
        return if (nextFile != null) {
            val relativePath = fileWithDirective.absolutePath().parent.relativize(nextFile.absolutePath())
            val linkMarkdown = LinkMarkdown("&rarr;", relativePath.toString())
            "&nbsp;&nbsp;&nbsp;${linkMarkdown.markdownString()}"
        } else {
            ""
        }
    }
}
