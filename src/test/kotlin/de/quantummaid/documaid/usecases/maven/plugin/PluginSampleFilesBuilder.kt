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

package de.quantummaid.documaid.usecases.maven.plugin

import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.TestEnvironmentProperty
import de.quantummaid.documaid.shared.SampleMavenProjectProperties
import de.quantummaid.documaid.shared.SetupUpdate
import de.quantummaid.documaid.shared.PhysicalFileSystemStructureBuilder
import java.nio.file.Path
import java.nio.file.Paths

class PluginSampleFilesBuilder {

    companion object {
        fun aFileWithASingleFullyDefinedPlugin(basePath: String): SetupUpdate {
            val testBaseDir = Paths.get(basePath, "singleFileWithFullyDefinedPlugin")
            val sampleFile = aXmlFileWithPluginTag("dependency.md", groupId = "local", artifactId = "test", version = "1.0.0", goal = "generate", phase = "Verify")
            return testDirectoryWithFile(sampleFile, testBaseDir)
        }

        // TODO: might be redundant
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

private fun aXmlFileWithPluginTag(fileName: String, groupId: String? = null, artifactId: String? = null, version: String? = null, goal: String, phase: String): SampleFile {
    val dependencyDirective = pluginDirective(groupId, artifactId, version, goal, phase)
    val content = contentWithPluginDirective(dependencyDirective)
    val expectedContent = contentWithDirectiveAndCode(dependencyDirective, groupId, artifactId, version, goal, phase)
    return SampleFile.sampleFile(content, expectedContent, fileName)
}

private fun pluginDirective(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val optionsString = createOptionsString(groupId, artifactId, version, goal, phase)
    return "<!---[Plugin]$optionsString-->"
}

private fun createOptionsString(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val groupIdString = if (groupId != null) "groupId=$groupId " else "groupId "
    val artifactIdString = if (artifactId != null) "artifactId=$artifactId " else "artifactId "
    val versionString = if (version != null) "version=$version " else "version "
    val goalString = "goal=$goal "
    val phaseString = "phase=$phase "
    return "($groupIdString$artifactIdString$versionString$goalString$phaseString)"
}

private fun contentWithPluginDirective(pluginDirective: String): String {
    return "Something\n" +
        "$pluginDirective\n" +
        "\n" +
        "someText"
}

private fun contentWithDirectiveAndCode(pluginDirective: String, groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val expectedContent = "Something\n" +
        "$pluginDirective\n" +
        "${createMarkdownPlugin(groupId, artifactId, version, goal, phase)}\n" +
        "someText"
    return expectedContent
}

private fun createMarkdownPlugin(groupId: String?, artifactId: String?, version: String?, goal: String, phase: String): String {
    val groupIdString = "    <groupId>${groupId ?: SampleMavenProjectProperties.SAMPLE_GROUP_ID}</groupId>\n"
    val artifactIdString = "    <artifactId>${artifactId ?: SampleMavenProjectProperties.SAMPLE_ARTIFACT_ID}</artifactId>\n"
    val versionString = "    <version>${version ?: SampleMavenProjectProperties.SAMPLE_VERSION_ID}</version>\n"

    return "```xml\n" +
        "<plugin>\n" +
        "$groupIdString" +
        "$artifactIdString" +
        "$versionString" +
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
