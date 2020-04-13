package de.quantummaid.documaid.processing

import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.errors.VerificationError

class ProcessingResult private constructor(val file: ProjectFile, val contentChanged: Boolean, val newContent: String, val errors: List<VerificationError>) {

    companion object {
        fun successfulProcessingResult(file: ProjectFile, newContent: String): ProcessingResult {
            return ProcessingResult(file, true, newContent, emptyList())
        }

        fun contentNotChangedProcessingResult(file: ProjectFile): ProcessingResult {
            return ProcessingResult(file, false, "", emptyList())
        }

        fun erroneousProcessingResult(file: ProjectFile, errors: List<VerificationError>): ProcessingResult {
            return ProcessingResult(file, false, "", errors)
        }
    }
}
