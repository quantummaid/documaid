package de.quantummaid.documaid.domain

import de.quantummaid.documaid.collecting.structure.ProjectFile

class IndexedFile private constructor(val name: String, val index: Int, val file: ProjectFile) {

    companion object {
        private val INDEXED_FILE_PATTERN = Regex("(?<index>[\\d]+)_+(?<name>.*)")

        fun anIndexedFile(file: ProjectFile): IndexedFile {
            val parsingErrorMessage = "Cannot extract index from file ${file.absolutePath()}"
            val matchEntire = INDEXED_FILE_PATTERN.matchEntire(file.name())
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val index = matchEntire.groups["index"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val name = matchEntire.groups["name"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val indexInt = Integer.parseInt(index)
            return IndexedFile(name, indexInt, file)
        }
    }

}
