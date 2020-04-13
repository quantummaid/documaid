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

package de.quantummaid.documaid.domain.markdown.tagBased

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.domain.markdown.tagBased.codeSnippet.SnippetMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.dependency.DependencyMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.link.GithubLinkMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.link.HugoLinkMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.GithubNavigationMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.HugoNavigationMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.plugin.PluginMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.GithubTableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tagBased.tableOfContents.HugoTableOfContentsMarkdownTagHandler

class MarkdownTagHandlerFactory private constructor() {

    companion object {
        fun obtainMarkdownHandlersFor(docuMaidConfiguration: DocuMaidConfiguration): List<MarkdownTagHandler> {
            return when (docuMaidConfiguration.platform) {
                Platform.GITHUB -> listOf(
                    SnippetMarkdownHandler(),
                    GithubLinkMarkdownTagHandler(),
                    GithubTableOfContentsMarkdownTagHandler(),
                    GithubNavigationMarkdownHandler(),
                    DependencyMarkdownHandler(),
                    PluginMarkdownHandler()
                )
                Platform.HUGO -> listOf(
                    SnippetMarkdownHandler(),
                    HugoLinkMarkdownTagHandler(),
                    HugoTableOfContentsMarkdownTagHandler(),
                    HugoNavigationMarkdownHandler(),
                    DependencyMarkdownHandler(),
                    PluginMarkdownHandler()
                )
            }
        }
    }
}
