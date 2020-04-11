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
