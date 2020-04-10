package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.ProcessedFile
import de.quantummaid.documaid.shared.ProcessedFileBuilder
import de.quantummaid.documaid.shared.SampleMavenProjectProperties

fun aXmlFileWithOneDependency(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null,
                           scope: String? = null): ProcessedFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)
    val dependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope)

    val contentInput = "Something\n" +
        dependencyDirective +
        "someText"
    val expectedContent = "Something\n" +
        dependencyDirective +
        dependencyMarkdown +
        "someText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aXmlFileWithADifferentGeneratedDependency(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null,
                                              scope: String? = null): ProcessedFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)

    val differentDependencyMarkdown = createMarkdownDependency("different", "something", null, null)
    val contentInput = "Something\n" +
        dependencyDirective +
        differentDependencyMarkdown+
        "someText"

    val expectedDependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope)
    val expectedContent = "Something\n" +
        dependencyDirective +
        expectedDependencyMarkdown +
        "someText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aXmlFileWithAnAlreadyGeneratedDependency(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null,
                                              scope: String? = null): ProcessedFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)
    val dependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope)
    val contentInput = "Something\n" +
        dependencyDirective +
        dependencyMarkdown+
        "someText"

    val expectedContent = "Something\n" +
        dependencyDirective +
        dependencyMarkdown +
        "someText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

private fun dependencyDirective(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val optionsString = createOptionsString(groupId, artifactId, version, scope)
    return "<!---[Dependency]$optionsString-->\n"
}

private fun createOptionsString(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val groupIdString = if (groupId != null) "groupId=$groupId " else "groupId "
    val artifactIdString = if (artifactId != null) "artifactId=$artifactId " else "artifactId "
    val versionString = if (version != null) "version=$version " else "version "
    val scopeString = if (scope != null) "scope=$scope " else ""
    return "($groupIdString$artifactIdString$versionString$scopeString)"
}

private fun createMarkdownDependency(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val groupIdString = "    <groupId>${groupId ?: SampleMavenProjectProperties.SAMPLE_GROUP_ID}</groupId>\n"
    val artifactIdString = "    <artifactId>${artifactId ?: SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID}</artifactId>\n"
    val versionString = "    <version>${version ?: SampleMavenProjectProperties.SAMPLE_VERSION_ID}</version>\n"
    val scopeString = if (scope != null) "    <scope>$scope</scope>\n" else ""
    return "```xml\n<dependency>\n$groupIdString$artifactIdString$versionString$scopeString</dependency>\n```\n"
}