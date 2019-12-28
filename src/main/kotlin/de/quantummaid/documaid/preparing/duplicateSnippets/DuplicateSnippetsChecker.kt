package de.quantummaid.documaid.preparing.duplicateSnippets

import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.domain.markdown.codeSnippet.CodeSnippetDirective.Companion.CODE_SNIPPET_TAG
import de.quantummaid.documaid.domain.snippet.SnippetId
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.preparing.PreparingVisitor

class DuplicateSnippetsChecker : PreparingVisitor {
    private val snippetsMap = HashMap<SnippetId, MutableList<ProjectFile>>()

    override fun prepareFile(file: ProjectFile, project: Project): List<VerificationError> {
        file.snippets()
            .forEach {
                if (snippetsMap.containsKey(it.id)) {
                    val list = snippetsMap.get(it.id)
                    list!!.add(file)
                } else {
                    snippetsMap.put(it.id, mutableListOf(file))
                }
            }
        return emptyList()
    }

    override fun finishPreparation(project: Project): List<VerificationError> {
        return snippetsMap
            .filter { it.value.size >= 2 }
            .toSortedMap(compareBy { it.value })
            .map {
                val filesListing = it.value
                    .sortedBy { projectFile -> projectFile.absolutePath().toString() }
                    .joinToString(separator = ", ") { projectFile -> projectFile.absolutePath().toString() }
                val message = "Found [$CODE_SNIPPET_TAG] tags with duplicate snippet '${it.key.value}': $filesListing"
                VerificationError(message, null)
            }
    }
}
