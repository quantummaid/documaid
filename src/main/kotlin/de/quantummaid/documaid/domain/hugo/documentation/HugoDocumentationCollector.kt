package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.DOCUMENTATION_DIRECTORY
import de.quantummaid.documaid.collecting.structure.CollectedInformationKey
import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.FileObjectVisitorAdapter
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_GEN_INFO_KEY
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.DOCUMENTATION_MAX_LEVEL
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.hugoGenerationInformationForDirectory
import de.quantummaid.documaid.domain.hugo.documentation.HugoDocumentationGenerationInformation.Companion.hugoGenerationInformationForFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.paths.IndexedPath
import de.quantummaid.documaid.domain.paths.makeRelativeTo
import de.quantummaid.documaid.domain.paths.pathMatchesFileNameExactly
import java.nio.file.Path

class HugoDocumentationCollector(
    private val docuMaidConfiguration: DocuMaidConfiguration
) : FileObjectVisitorAdapter() {
    private var maxLevelOfIndexedDocumentationSubDirectories = 0

    companion object {
        val DOCUMENTATION_ROOT_DIRECTORY = CollectedInformationKey<Directory>("DOCUMENTATION_ROOT_DIRECTORY")
    }

    override fun fileVisited(file: ProjectFile) {
        val absolutePath = file.absolutePath()
        if (file is MarkdownFile && isWithinDocuDirButNotLegacy(absolutePath)) {
            file.setData(DOCUMENTATION_GEN_INFO_KEY, hugoGenerationInformationForFile(file))
        }
    }

    override fun directoryVisited(directory: Directory) {
        val absolutePath = directory.absolutePath()
        if (isWithinDocuDirButNotLegacy(absolutePath)) {
            val levelWithinDocu = calculateLevelWithinDocumentation(absolutePath)
            val generationInformation = hugoGenerationInformationForDirectory(directory, levelWithinDocu)
            directory.setData(DOCUMENTATION_GEN_INFO_KEY, generationInformation)

            maxLevelOfIndexedDocumentationSubDirectories =
                Math.max(levelWithinDocu, maxLevelOfIndexedDocumentationSubDirectories)
        }
    }

    private fun isWithinDocuDirButNotLegacy(absolutePath: Path): Boolean {
        return if (isWithinDocumentationDirectory(absolutePath, docuMaidConfiguration)) {
            !isWithinLegacyDirectory(absolutePath, docuMaidConfiguration)
        } else {
            false
        }
    }

    private fun calculateLevelWithinDocumentation(absolutePath: Path): Int {
        val rootRelativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
        var level = 0
        for (path in rootRelativePath) {
            if (IndexedPath.isIndexedPath(path)) {
                level++
            }
        }

        if (IndexedPath.isIndexedPath(docuMaidConfiguration.hugoOutputPath)) {
            level++
        }
        return level
    }

    override fun finishTreeWalk(project: Project) {
        val documentationDirectory = project.rootDirectory.children()
            .firstOrNull { pathMatchesFileNameExactly(it.absolutePath(), DOCUMENTATION_DIRECTORY) }
        if (documentationDirectory != null) {
            project.setInformation(DOCUMENTATION_ROOT_DIRECTORY, documentationDirectory)

            val generationInformation = documentationDirectory.getData(DOCUMENTATION_GEN_INFO_KEY)
            generationInformation.targetPath = docuMaidConfiguration.absoluteHugoOutputPath()

            project.setInformation(DOCUMENTATION_MAX_LEVEL, maxLevelOfIndexedDocumentationSubDirectories)
        }
    }
}
