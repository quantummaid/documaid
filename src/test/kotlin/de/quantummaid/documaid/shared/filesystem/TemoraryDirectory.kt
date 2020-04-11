package de.quantummaid.documaid.shared.filesystem

import java.nio.file.Path
import java.nio.file.Paths

class TemporaryTestDirectory(val path: Path) {

    companion object {
        fun aTemporyTestDirectory(basePath: Path, testDirectoryName: String): TemporaryTestDirectory {
            return aTemporyTestDirectory(basePath.toString(), testDirectoryName)
        }
        fun aTemporyTestDirectory(basePath: String, testDirectoryName: String): TemporaryTestDirectory {
            val path = Paths.get(basePath, testDirectoryName)
            return TemporaryTestDirectory(path)
        }
    }
}
