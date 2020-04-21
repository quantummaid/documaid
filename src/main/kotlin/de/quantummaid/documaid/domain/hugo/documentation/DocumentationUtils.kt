package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.paths.makeRelativeTo
import de.quantummaid.documaid.domain.paths.pathUnderTopLevelDirectory
import java.nio.file.Path

fun isWithinDocumentationDirectory(absolutePath: Path, docuMaidConfiguration: DocuMaidConfiguration): Boolean {
    val relativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
    return pathUnderTopLevelDirectory(relativePath, HugoDocumentationAssumptions.DOCUMENTATION_DIRECTORY)
}

fun isWithinLegacyDirectory(absolutePath: Path, docuMaidConfiguration: DocuMaidConfiguration): Boolean {
    val relativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
    return pathUnderTopLevelDirectory(relativePath, HugoDocumentationAssumptions.DOCUMENTATION_LEGACY_DIRECTORY)
}