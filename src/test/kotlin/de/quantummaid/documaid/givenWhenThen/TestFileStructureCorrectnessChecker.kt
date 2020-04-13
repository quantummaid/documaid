package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.shared.filesystem.PhysicalDirectory
import de.quantummaid.documaid.shared.filesystem.PhysicalFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileObject
import de.quantummaid.documaid.shared.filesystem.PhysicalFileSystemStructure
import de.quantummaid.documaid.shared.filesystem.assertDirectoryExists
import de.quantummaid.documaid.shared.filesystem.assertFileWithContent

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
