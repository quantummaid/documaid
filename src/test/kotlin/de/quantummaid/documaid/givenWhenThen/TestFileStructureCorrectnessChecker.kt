package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.shared.filesystem.PhysicalDirectory
import de.quantummaid.documaid.shared.filesystem.PhysicalFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileSystemStructure
import de.quantummaid.documaid.shared.filesystem.assertDirectoryExists
import de.quantummaid.documaid.shared.filesystem.assertFileWithContent
import kotlin.test.fail

class TestFileStructureCorrectnessChecker {

    companion object {
        fun checkForCorrectness(physicalFileSystemStructure: PhysicalFileSystemStructure) {
            val objectsToCheck = mutableListOf(physicalFileSystemStructure.baseDirectory)
            while (objectsToCheck.isNotEmpty()) {
                val objectToCheck = objectsToCheck.removeAt(0)
                val childDirectories = checkDirectory(objectToCheck)
                objectsToCheck.addAll(childDirectories)
            }
        }

        private fun checkDirectory(directory: PhysicalDirectory): List<PhysicalDirectory> {
            val path = directory.path
            assertDirectoryExists(path)

            val expectedChildrenNames = directory.children.map { it.name }
            val actualChildrenNames = path.toFile().list().toList()
            if (expectedChildrenNames.sorted() == actualChildrenNames.sorted()) {
                directory.children
                    .filterIsInstance(PhysicalFile::class.java)
                    .forEach { checkFileContent(it) }
                return directory.children
                    .filterIsInstance(PhysicalDirectory::class.java)
            } else {
                val notFoundChildrenNames = expectedChildrenNames.toMutableSet()
                notFoundChildrenNames.removeAll(actualChildrenNames)
                if (notFoundChildrenNames.isNotEmpty()) {
                    fail("Expected the following entries in $path: ${notFoundChildrenNames.joinToString(",")}")
                } else {
                    val unexpectedChildrenNames = actualChildrenNames.toMutableSet()
                    unexpectedChildrenNames.removeAll(expectedChildrenNames)
                    fail("Found unexpected entries in $path: ${unexpectedChildrenNames.joinToString(",")}")
                }
            }
        }

        private fun checkFileContent(file: PhysicalFile) {
            assertFileWithContent(file.path, file.content)
        }
    }
}
