package de.quantummaid.documaid.domain.paths

import de.quantummaid.documaid.collecting.structure.ProjectFile
import java.nio.file.Path

class IndexedPath private constructor(val name: String, val index: Int) {

    companion object {
        private val INDEXED_FILE_PATTERN = Regex("(?<index>[\\d]+)_+(?<name>.*)")

        fun anIndexedPath(file: ProjectFile): IndexedPath {
            return anIndexedPath(file.name(), file.absolutePath())
        }

        fun anIndexedPath(path: Path): IndexedPath {
            val fileName = path.fileName.toString()
            return anIndexedPath(fileName, path.toAbsolutePath())
        }

        fun anIndexedPath(fileName: String, filePath: Path?): IndexedPath {
            val parsingErrorMessage = "Cannot extract index from file $${filePath ?: fileName}"
            val matchEntire = INDEXED_FILE_PATTERN.matchEntire(fileName)
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val index = matchEntire.groups["index"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val name = matchEntire.groups["name"]?.value
                ?: throw IllegalArgumentException(parsingErrorMessage)
            val indexInt = Integer.parseInt(index)
            return IndexedPath(name, indexInt)
        }

        fun isIndexedPath(file: ProjectFile): Boolean {
            return isIndexedPath(file.name())
        }

        fun isIndexedPath(path: Path): Boolean {
            val fileName = path.fileName.toString()
            return isIndexedPath(fileName)
        }

        fun isIndexedPath(fileName: String): Boolean {
            val matchEntire = INDEXED_FILE_PATTERN.matchEntire(fileName)
            return matchEntire != null
        }
    }
}
