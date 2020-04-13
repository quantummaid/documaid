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

package de.quantummaid.documaid.usecases.hugo.flavor

import de.quantummaid.documaid.shared.filesystem.EmptySutFile.Companion.aFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileBuilder
import de.quantummaid.documaid.shared.filesystem.PhysicalFileSystemStructureBuilder
import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.SutDirectory
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithH1Heading

fun aTypicalQuantumMaidProjectStructure(basePath: String, hugoOutputPath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aTypicalQuantumMaidProjectStructure")

    val readme = aMarkdownFileWithH1Heading("README.md", "20")
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                SutDirectory.aDirectory("documentation")
                    .with(
                        aFile("1_Introduction.md"),
                        aFile("2_GettingStarted.md"),
                        aFile("3_UseCases.md"),
                        aFile("4_Something.md")
                    ),
                readme
            )
        val expectedOutputPath = testDir.path.resolve(hugoOutputPath).toString()
        sutFileStructure.overrideExpectedFileStructure(
            PhysicalFileSystemStructureBuilder.aPhysicalFileStructureIn(expectedOutputPath)
                .with(
                    PhysicalFileBuilder.aFile("1_Introduction.md"),
                    PhysicalFileBuilder.aFile("2_GettingStarted.md"),
                    PhysicalFileBuilder.aFile("3_UseCases.md"),
                    PhysicalFileBuilder.aFile("4_Something.md"),
                    PhysicalFileBuilder.aFile("_index.md")
                        .withContent(readme.processedFileInHugoFormat().content)
                )
                .construct()
        )
    }
}
