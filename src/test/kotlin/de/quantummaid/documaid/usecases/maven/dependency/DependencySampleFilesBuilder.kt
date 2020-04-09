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

package de.quantummaid.documaid.usecases.maven.dependency

import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.TestEnvironmentProperty
import de.quantummaid.documaid.shared.SampleMavenProjectProperties
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.PhysicalFileSystemStructureBuilder
import java.nio.file.Path
import java.nio.file.Paths

class DependencySampleFilesBuilder {

    companion object {
        fun aFileWithASingleFullyDefinedDependency(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "singleFullyDefinedDep")
            val sampleFile = aXmlFileWithDependencyTag("dependency.md", groupId = "local", artifactId = "test", version = "1.0.0", scope = "compile")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        fun aFileWithADependencyWithoutAnythingDefined(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "depWithoutAnythingDefined")
            val sampleFile = aXmlFileWithDependencyTag("dependency.md")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        fun aFileWithAWrongGeneratedDependency(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "depWithWrongCode")
            val sampleFile = aXmlFileWithDependencyTagWithDifferentCode("dependency.md", artifactId = "test", version = "1.0.0")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        fun aFileWithUnparsableDependencyOptionsString(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "depWithWrongCode")
            val sampleFile = aXmlFileWithDependencyTagWithDifferentCode("dependency.md", groupId = "not correct")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        fun aFileWithACorrectDependency(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "depWithWrongCode")
            val sampleFile = aXmlFileWithAlreadyGeneratedCorrectCode("dependency.md", artifactId = "different")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        fun aFileWithDependencyWithMissingCode(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "depWithWrongCode")
            val sampleFile = aXmlFileWithDependencyTag("dependency.md", version = "1.0.0", scope = "compile")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        private fun testDirectoryWithFile(sampleFile: SampleFile, testBaseDir: Path): SetupUpdate {
            return { (testEnvironment, configurationBuilder, _, _, cleanupSteps) ->
                testEnvironment.setProperty(TestEnvironmentProperty.BASE_PATH, testBaseDir.toString())
                configurationBuilder.withBasePath(testBaseDir)

                val testFileStructure = PhysicalFileSystemStructureBuilder.createAPhysicalFileSystemStructureIn(testBaseDir)
                    .with(
                        sampleFile.asBuilder()
                    )
                    .build()
                cleanupSteps.add { testFileStructure.cleanUp() }
                testEnvironment.setProperty(TestEnvironmentProperty.SAMPLE_FILE, sampleFile)
            }
        }
    }
}

private fun aXmlFileWithDependencyTag(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null, scope: String? = null): SampleFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)
    val content = contentWithDependencyDirective(dependencyDirective)
    val expectedContent = contentWithDirectiveAndCode(dependencyDirective, groupId, artifactId, version, scope)
    return SampleFile.sampleFile(content, expectedContent, fileName)
}

private fun aXmlFileWithDependencyTagWithDifferentCode(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null, scope: String? = null): SampleFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)
    val content = contentWithDirectiveAndCode(dependencyDirective, "old", "wrong", "10.5.2", null)
    val expectedContent = contentWithDirectiveAndCode(dependencyDirective, groupId, artifactId, version, scope)
    return SampleFile.sampleFile(content, expectedContent, fileName)
}

private fun aXmlFileWithAlreadyGeneratedCorrectCode(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null, scope: String? = null): SampleFile {
    val dependencyDirective = dependencyDirective(groupId, artifactId, version, scope)
    val content = contentWithDirectiveAndCode(dependencyDirective, groupId, artifactId, version, scope)
    val expectedContent = contentWithDirectiveAndCode(dependencyDirective, groupId, artifactId, version, scope)
    return SampleFile.sampleFile(content, expectedContent, fileName)
}

private fun dependencyDirective(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val optionsString = createOptionsString(groupId, artifactId, version, scope)
    return "<!---[Dependency]$optionsString-->"
}

private fun createOptionsString(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val groupIdString = if (groupId != null) "groupId=$groupId " else "groupId "
    val artifactIdString = if (artifactId != null) "artifactId=$artifactId " else "artifactId "
    val versionString = if (version != null) "version=$version " else "version "
    val scopeString = if (scope != null) "scope=$scope " else ""
    return "($groupIdString$artifactIdString$versionString$scopeString)"
}

private fun contentWithDependencyDirective(dependencyDirective: String): String {
    return "Something\n" +
        "$dependencyDirective\n" +
        "\n" +
        "someText"
}

private fun contentWithDirectiveAndCode(dependencyDirective: String, groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val expectedContent = "Something\n" +
        "$dependencyDirective\n" +
        "${createMarkdownDependency(groupId, artifactId, version, scope)}\n" +
        "someText"
    return expectedContent
}

private fun createMarkdownDependency(groupId: String?, artifactId: String?, version: String?, scope: String?): String {
    val groupIdString = "    <groupId>${groupId ?: SampleMavenProjectProperties.SAMPLE_GROUP_ID}</groupId>\n"
    val artifactIdString = "    <artifactId>${artifactId ?: SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID}</artifactId>\n"
    val versionString = "    <version>${version ?: SampleMavenProjectProperties.SAMPLE_VERSION_ID}</version>\n"
    val scopeString = if (scope != null) "    <scope>$scope</scope>\n" else ""
    return "```xml\n<dependency>\n$groupIdString$artifactIdString$versionString$scopeString</dependency>\n```\n"
}
