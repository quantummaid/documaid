package de.quantummaid.documaid.domain.markdown

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.domain.markdown.codeSnippet.SnippetMarkdownHandler
import de.quantummaid.documaid.domain.markdown.dependency.DependencyMarkdownHandler
import de.quantummaid.documaid.domain.markdown.link.GithubLinkMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.link.HugoLinkMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.navigation.GithubNavigationMarkdownHandler
import de.quantummaid.documaid.domain.markdown.navigation.HugoNavigationMarkdownHandler
import de.quantummaid.documaid.domain.markdown.plugin.PluginMarkdownHandler
import de.quantummaid.documaid.domain.markdown.tableOfContents.GithubTableOfContentsMarkdownTagHandler
import de.quantummaid.documaid.domain.markdown.tableOfContents.HugoTableOfContentsMarkdownTagHandler

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
