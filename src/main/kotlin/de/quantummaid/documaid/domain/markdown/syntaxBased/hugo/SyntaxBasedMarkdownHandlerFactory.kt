package de.quantummaid.documaid.domain.markdown.syntaxBased.hugo

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.domain.markdown.syntaxBased.SyntaxBasedMarkdownHandler
import de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.heading.HugoHeadingMarkdownHandler

class SyntaxBasedMarkdownHandlerFactory private constructor() {

    companion object {
        fun obtainMarkdownHandlersFor(docuMaidConfiguration: DocuMaidConfiguration): List<SyntaxBasedMarkdownHandler> {
            return when (docuMaidConfiguration.platform) {
                Platform.GITHUB -> emptyList()
                Platform.HUGO -> listOf(
                    HugoHeadingMarkdownHandler()
                )
            }
        }
    }
}
