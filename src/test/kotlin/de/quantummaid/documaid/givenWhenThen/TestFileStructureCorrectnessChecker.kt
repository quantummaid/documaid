package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.shared.PhysicalDirectory
import de.quantummaid.documaid.shared.PhysicalFile
import de.quantummaid.documaid.shared.PhysicalFileObject
import de.quantummaid.documaid.shared.PhysicalFileSystemStructure
import de.quantummaid.documaid.shared.assertDirectoryExists
import de.quantummaid.documaid.shared.assertFileWithContent

class TestFileStructureCorrectnessChecker {

    companion object {
        fun checkForCorrectness(physicalFileSystemStructure: PhysicalFileSystemStructure) {
            val objectsToCheck = mutableListOf<PhysicalFileObject>(physicalFileSystemStructure.baseDirectory)
            while (objectsToCheck.isNotEmpty()) {
                val objectToCheck = objectsToCheck.removeAt(0)
                when (objectToCheck) {
                    is PhysicalFile -> checkFileContent(objectToCheck)
                    is PhysicalDirectory -> {
                        checkDirectoryPath(objectToCheck)
                        objectsToCheck.addAll(objectToCheck.children)
                    }
                    else -> throw IllegalArgumentException("Unknown PhysicalFileObject $objectToCheck")
                }
            }
        }

        private fun checkDirectoryPath(directory: PhysicalDirectory) {
            assertDirectoryExists(directory.path)
        }

        private fun checkFileContent(file: PhysicalFile) {
            assertFileWithContent(file.path, file.content)
        }
    }
}
