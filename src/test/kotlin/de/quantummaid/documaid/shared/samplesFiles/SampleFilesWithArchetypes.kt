/**
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.ProcessedFile
import de.quantummaid.documaid.shared.filesystem.ProcessedFileBuilder
import de.quantummaid.documaid.shared.samplesFiles.SampleMavenProjectProperties.Companion.SAMPLE_ARTIFACT_ID
import de.quantummaid.documaid.shared.samplesFiles.SampleMavenProjectProperties.Companion.SAMPLE_GROUP_ID
import de.quantummaid.documaid.shared.samplesFiles.SampleMavenProjectProperties.Companion.SAMPLE_VERSION_ID

fun aMarkdownFileWithOneArchetype(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val dependencyMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )

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

fun aMarkdownFileWithOneArchetypeAtEndOfFile(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val dependencyMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )

    val contentInput = "Something\n" +
        dependencyDirective
    val expectedContent = "Something\n" +
        dependencyDirective +
        dependencyMarkdown
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aMarkdownFileWithWrongArchetype(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val wrongMarkdown = createMarkdownArchetype(
        "different",
        "test",
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )

    val contentInput = "Something\n" +
        dependencyDirective +
        wrongMarkdown +
        "\nsome Text"

    val correctMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )
    val expectedContent = "Something\n" +
        dependencyDirective +
        correctMarkdown +
        "\nsome Text"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aMarkdownFileWithWrongArchetypeAtEndOfFile(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val wrongMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        "56.12.2",
        packaging,
        lineBreakingChar
    )

    val contentInput = "Something\n" +
        dependencyDirective +
        wrongMarkdown

    val correctMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )
    val expectedContent = "Something\n" +
        dependencyDirective +
        correctMarkdown
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aMarkdownFileAlreadyGeneratedArchetype(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val dependencyMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )

    val expectedContent = "Something\n" +
        dependencyDirective +
        dependencyMarkdown +
        "someText"
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

fun aMarkdownFileWithAlreadyGeneratedArchetypeAtEndOfFile(
    fileName: String,
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null,
    lineBreakingChar: String = "\\"
): ProcessedFile {
    val dependencyDirective = archetypeDirectiveMarkdown(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    val dependencyMarkdown = createMarkdownArchetype(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        lineBreakingChar
    )

    val expectedContent = "Something\n" +
        dependencyDirective +
        dependencyMarkdown
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

private fun archetypeDirectiveMarkdown(
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null
): String {
    val optionsString = createOptionsString(
        archetypeGroupId,
        archetypeArtifactId,
        archetypeVersion,
        groupId,
        artifactId,
        version,
        packaging,
        os
    )
    return "<!---[Archetype]$optionsString-->\n"
}

private fun createOptionsString(
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String,
    artifactId: String,
    version: String,
    packaging: String,
    os: String? = null
): String {
    val archetypeGroupIdString = if (archetypeGroupId != null) {
        "archetypeGroupId=$archetypeGroupId "
    } else {
        "archetypeGroupId "
    }
    val archetypeArtifactIdString = if (archetypeArtifactId != null) {
        "archetypeArtifactId=$archetypeArtifactId "
    } else {
        "archetypeArtifactId "
    }
    val archetypeVersionString = if (archetypeVersion != null) {
        "archetypeVersion=$archetypeVersion "
    } else {
        "archetypeVersion "
    }
    val groupIdString = "groupId=$groupId "
    val artifactIdString = "artifactId=$artifactId "
    val versionString = "version=$version "
    val packagingString = "packaging=$packaging "
    val osString = if (os != null) "os=$os " else ""
    return "($archetypeGroupIdString$archetypeArtifactIdString$archetypeVersionString" +
        "$groupIdString$artifactIdString$versionString$packagingString$osString)"
}

private fun createMarkdownArchetype(
    archetypeGroupId: String?,
    archetypeArtifactId: String?,
    archetypeVersion: String?,
    groupId: String?,
    artifactId: String,
    version: String,
    packaging: String,
    lineBreakChar: String = "\\"
): String {
    val indentation = "    "
    val archetypeGroupIdString = "$indentation-DarchetypeGroupId=${archetypeGroupId ?: SAMPLE_GROUP_ID}"
    val archetypeArtifactIdString = "$indentation-DarchetypeArtifactId=${archetypeArtifactId ?: SAMPLE_ARTIFACT_ID}"
    val archetypeVersionString = "$indentation-DarchetypeVersion=${archetypeVersion ?: SAMPLE_VERSION_ID}"
    val groupIdString = "$indentation-DgroupId=$groupId"
    val artifactIdString = "$indentation-DartifactId=$artifactId"
    val versionString = "$indentation-Dversion=$version"
    val packagingString = "$indentation-Dpackaging=$packaging"
    return "```xml\n" +
        "mvn archetype:generate $lineBreakChar\n" +
        "$indentation--batch-mode $lineBreakChar\n" +
        "$archetypeGroupIdString $lineBreakChar\n" +
        "$archetypeArtifactIdString $lineBreakChar\n" +
        "$archetypeVersionString $lineBreakChar\n" +
        "$groupIdString $lineBreakChar\n" +
        "$artifactIdString $lineBreakChar\n" +
        "$versionString $lineBreakChar\n" +
        "$packagingString $lineBreakChar\n" +
        "\n" +
        "cd ./$artifactId\n" +
        "```\n"
}
