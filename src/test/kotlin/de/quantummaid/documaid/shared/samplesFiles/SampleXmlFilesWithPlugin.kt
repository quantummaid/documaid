package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder

fun aMarkdownFileWithAPluginDirective(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null, goal: String, phase: String): ProcessedFile {
    val pluginDirective = pluginDirective(groupId, artifactId, version, goal, phase)
    val contentInput = "Something\n" +
        pluginDirective +
        "someText"

    val pluginMarkdown = createMarkdownPlugin(groupId, artifactId, version, goal, phase)
    val expectedContentOutput = "Something\n" +
        pluginDirective +
        pluginMarkdown +
        "someText"

    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContentOutput)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContentOutput)
        .build()
}

internal fun pluginDirective(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val optionsString = createOptionsString(groupId, artifactId, version, goal, phase)
    return "<!---[Plugin]$optionsString-->\n"
}

private fun createOptionsString(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val groupIdString = if (groupId != null) "groupId=$groupId " else "groupId "
    val artifactIdString = if (artifactId != null) "artifactId=$artifactId " else "artifactId "
    val versionString = if (version != null) "version=$version " else "version "
    val goalString = "goal=$goal "
    val phaseString = "phase=$phase "
    return "($groupIdString$artifactIdString$versionString$goalString$phaseString)"
}


internal fun createMarkdownPlugin(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val groupIdString = "    <groupId>${groupId ?: SampleMavenProjectProperties.SAMPLE_GROUP_ID}</groupId>\n"
    val artifactIdString = "    <artifactId>${artifactId ?: SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID}</artifactId>\n"
    val versionString = "    <version>${version ?: SampleMavenProjectProperties.SAMPLE_VERSION_ID}</version>\n"

    return "```xml\n" +
        "<plugin>\n" +
        groupIdString +
        artifactIdString +
        versionString +
        "    <executions>\n" +
        "        <execution>\n" +
        "            <goals>\n" +
        "                <goal>$goal</goal>\n" +
        "            </goals>\n" +
        "            <phase>$phase</phase>\n" +
        "        </execution>\n" +
        "    </executions>\n" +
        "</plugin>\n```\n"
}