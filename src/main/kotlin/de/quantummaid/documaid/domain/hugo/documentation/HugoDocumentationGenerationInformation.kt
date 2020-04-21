package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileObject
import de.quantummaid.documaid.collecting.structure.FileObjectDataKey
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import java.nio.file.Path

class HugoDocumentationGenerationInformation private constructor(
    val originalPath: Path,
    var originalFileObject: FileObject,
    var targetPath: Path? = null,
    var levelWithinDocumentation: Int? = null,
    var weight: String? = null,
    var weightPrefix: String? = null
) {

    companion object {
        val DOCUMENTATION_GEN_INFO_KEY =
            FileObjectDataKey<HugoDocumentationGenerationInformation>("DOCUMENTATION_GEN_INFO_KEY")
        val DOCUMENTATION_MAX_LEVEL = CollectedInformationKey<Int>("DOCUMENTATION_MAX_LEVEL")

        fun hugoGenerationInformationForFile(file: MarkdownFile): HugoDocumentationGenerationInformation {
            val absolutePath = file.absolutePath()
            return HugoDocumentationGenerationInformation(absolutePath, file)
        }

        fun hugoGenerationInformationForDirectory(
            directory: Directory,
            levelWithinDocu: Int
        ): HugoDocumentationGenerationInformation {
            val absolutePath = directory.absolutePath()
            return HugoDocumentationGenerationInformation(absolutePath,
                directory, levelWithinDocumentation = levelWithinDocu)
        }
    }
}
