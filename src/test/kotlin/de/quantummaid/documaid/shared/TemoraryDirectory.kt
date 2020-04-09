package de.quantummaid.documaid.shared

import java.nio.file.Path
import java.nio.file.Paths

class TemporaryTestDirectory(val path: Path) {

    companion object {
        fun createWithName(basePath: String, testDirectoryName: String): TemporaryTestDirectory {
            val path = Paths.get(basePath, testDirectoryName)
            return TemporaryTestDirectory(path)
        }
    }
}

//TODO: remove
fun createTemporaryDirectoryPath(basePath: String, testDirectoryName: String): Path {
    return Paths.get(basePath, testDirectoryName)
}