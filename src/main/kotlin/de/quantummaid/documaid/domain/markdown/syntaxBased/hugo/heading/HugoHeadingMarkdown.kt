package de.quantummaid.documaid.domain.markdown.syntaxBased.hugo.heading

import de.quantummaid.documaid.domain.IndexedFile
import de.quantummaid.documaid.domain.IndexedFile.Companion.anIndexedFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile

class HugoHeadingMarkdown private constructor(private val title: String, private val indexedFile: IndexedFile) {
    companion object {

        private val RAW_HEADING_PATTERN = Regex("# ?(?<title>[^\n]+)\n")
        fun create(rawHeadingString: String, file: MarkdownFile): HugoHeadingMarkdown {
            val matchEntire = RAW_HEADING_PATTERN.matchEntire(rawHeadingString)
            val errorMessage = "Could not parse title of heading: '$rawHeadingString'"
            matchEntire ?: throw IllegalArgumentException(errorMessage)
            val title = matchEntire.groups["title"]?.value ?: throw IllegalArgumentException(errorMessage)

            val indexedFile = anIndexedFile(file)
            return HugoHeadingMarkdown(title, indexedFile)
        }

    }

    fun generateMarkdown(): String {
        val weight = indexedFile.index * 10
        return """
            ---
            title: "$title"
            weight: $weight
            ---
            """.trimIndent() + "\n"
    }
}
