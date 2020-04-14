/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

fun aMarkdownFileWithOneDependency(
    fileName: String,
    groupId: String? = null,
    artifactId: String? = null,
    version: String? = null,
    scope: String? = null
): ProcessedFile {
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope)
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

fun aMarkdownFileWithADifferentGeneratedDependency(
    fileName: String,
    groupId: String? = null,
    artifactId: String? = null,
    version: String? = null,
    scope: String? = null
): ProcessedFile {
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope)

    val differentDependencyMarkdown = createMarkdownDependency("different", "something", null, null)
    val contentInput = "Something\n" +
        dependencyDirective +
        differentDependencyMarkdown +
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

fun aMarkdownFileWithAnAlreadyGeneratedDependency(
    fileName: String,
    groupId: String? = null,
    artifactId: String? = null,
    version: String? = null,
    scope: String? = null
): ProcessedFile {
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope)
    val dependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope)
    val contentInput = "Something\n" +
        dependencyDirective +
        dependencyMarkdown +
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

fun aMarkdownFileWithOneDependencyAtTheEndOfFileWithoutNewline(
    fileName: String,
    groupId: String? = null,
    artifactId: String? = null,
    version: String? = null,
    scope: String? = null
): ProcessedFile {
    val dependencyDirective = dependencyDirectiveMarkdown(groupId, artifactId, version, scope, lastChar = "")
    val dependencyMarkdown = createMarkdownDependency(groupId, artifactId, version, scope, lastChar = "")

    val contentInput = "Something\n" +
        dependencyDirective
    val expectedContent = "Something\n" +
        dependencyDirective + "\n" +
        dependencyMarkdown
    return ProcessedFileBuilder.anExpectedFile()
        .withOriginalNameAndContent(fileName, contentInput)
        .withProcessedNameAndContent(fileName, expectedContent)
        .withProcessedNameAndContentInHugoFormat(fileName, expectedContent)
        .build()
}

internal fun dependencyDirectiveMarkdown(
    groupId: String?,
    artifactId: String?,
    version: String?,
    scope: String?,
    lastChar: String = "\n"
): String {
    val optionsString = createOptionsString(groupId, artifactId, version, scope)
    return "<!---[Dependency]$optionsString-->$lastChar"
}

private fun createOptionsString(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val groupIdString = if (groupId != null) "groupId=$groupId " else "groupId "
    val artifactIdString = if (artifactId != null) "artifactId=$artifactId " else "artifactId "
    val versionString = if (version != null) "version=$version " else "version "
    val scopeString = if (scope != null) "scope=$scope " else ""
    return "($groupIdString$artifactIdString$versionString$scopeString)"
}

internal fun createMarkdownDependency(
    groupId: String?,
    artifactId: String?,
    version: String?,
    scope: String?,
    lastChar: String = "\n"
): String {
    val groupIdString = "    <groupId>${groupId ?: SAMPLE_GROUP_ID}</groupId>\n"
    val artifactIdString = "    <artifactId>${artifactId ?: SAMPLE_ARTIFACT_ID}</artifactId>\n"
    val versionString = "    <version>${version ?: SAMPLE_VERSION_ID}</version>\n"
    val scopeString = if (scope != null) "    <scope>$scope</scope>\n" else ""
    return "```xml\n<dependency>\n$groupIdString$artifactIdString$versionString$scopeString</dependency>\n```$lastChar"
}
