package de.quantummaid.documaid.errors

import de.quantummaid.documaid.collecting.structure.ProjectFile
import java.nio.file.Path

class DocuMaidException private constructor(message: String) : RuntimeException(message) {

    companion object {
        fun create(message: String, file: ProjectFile): DocuMaidException {
            return DocuMaidException.create(message, file.absolutePath())
        }

        fun create(message: String, path: Path): DocuMaidException {
            val completeMessage = "$message (in path $path)"
            return DocuMaidException(completeMessage)
        }

        fun createWithoutFileOrigin(message: String): DocuMaidException {
            return DocuMaidException(message)
        }
    }
}
