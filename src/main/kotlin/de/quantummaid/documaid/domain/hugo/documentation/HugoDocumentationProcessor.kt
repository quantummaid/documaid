package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationDirectory.Companion.aDocumentationDirectory
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationDirectory.Companion.isDocumentationDirectory
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationMarkdownFile.Companion.aDocumentationMarkdownFile
import de.quantummaid.documaid.domain.hugo.documentation.DocumentationMarkdownFile.Companion.isDocumentationMarkdownFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.processing.ProcessingResult
import de.quantummaid.documaid.processing.ProcessingVisitorAdapter

class HugoDocumentationProcessor : ProcessingVisitorAdapter() {

    override fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
        if (isDocumentationDirectory(directory)) {
            val documentationDirectory = aDocumentationDirectory(directory)
            documentationDirectory.calculateTargetPath(project)
            documentationDirectory.calculateWeight(project)
        }
    }

    override fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
        if (isDocumentationMarkdownFile(file)) {
            val documentationMarkdownFile = aDocumentationMarkdownFile(file as MarkdownFile)
            documentationMarkdownFile.calculateTargetPath(project)
            documentationMarkdownFile.calculateWeight(project)
        }
    }

    override fun afterDirectoryProcessing(
        directory: Directory,
        project: Project,
        goal: Goal,
        directoryProcessingResults: MutableList<ProcessingResult>
    ) {
        if (isDocumentationDirectory(directory)) {
            val documentationDirectory = aDocumentationDirectory(directory)
            val processingResultIfFileCreated = documentationDirectory.generateHugoIndexFileIfNotPresent()
            if (processingResultIfFileCreated != null) {
                directoryProcessingResults.add(processingResultIfFileCreated)
            }
        }
    }
}
