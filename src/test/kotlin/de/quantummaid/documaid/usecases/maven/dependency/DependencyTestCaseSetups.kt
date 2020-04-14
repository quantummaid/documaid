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
package de.quantummaid.documaid.usecases.maven.dependency

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithADifferentGeneratedDependency
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAnAlreadyGeneratedDependency
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithOneDependency
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithOneDependencyAtTheEndOfFileWithoutNewline

fun aFileWithASingleFullyDefinedDependency(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithASingleFullyDefinedDependency")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneDependency("dependency.md",
                    groupId = "local", artifactId = "test", version = "1.0.0", scope = "compile")
            )
    }
}

fun aFileWithADependencyWithoutAnythingDefined(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithADependencyWithoutAnythingDefined")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneDependency("dependency.md")
            )
    }
}

fun aFileWithAWrongGeneratedDependency(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithAWrongGeneratedDependency")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithADifferentGeneratedDependency("dependency.md", groupId = "local", version = "1.0.0")
            )
    }
}

fun aFileWithUnparsableDependencyOptionsString(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithUnparsableDependencyOptionsString")

    val incorrectGroupId = "not correct"
    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithADifferentGeneratedDependency("dependency.md", groupId = incorrectGroupId)
            )
    }
}

fun aFileWithACorrectlyGeneratedDependency(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithACorrectlyGeneratedDependency")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithAnAlreadyGeneratedDependency("dependency.md",
                    groupId = "com.local", artifactId = "test-project", version = "2.4-SNAPSHOT", scope = "provided")
            )
    }
}

fun aFileWithDependencyWithMissingCode(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithDependencyWithMissingCode")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneDependency("dependency.md", artifactId = "test", version = "1", scope = "compile"))
    }
}

fun aFileWithASingleDependencyAtTheEndOfFileWithoutNewLine(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithASingleFullyDefinedDependency")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneDependencyAtTheEndOfFileWithoutNewline("dependency.md",
                    groupId = "local", artifactId = "test", version = "1.0.0", scope = "compile"))
    }
}
fun aFileWithAWrongDependencyAtTheEndOfFileWithoutNewLine(basePath: String): SetupUpdate {
    val testDir = TemporaryTestDirectory.aTemporyTestDirectory(basePath, "aFileWithASingleFullyDefinedDependency")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneDependencyAtTheEndOfFileWithoutNewline("dependency.md",
                    groupId = "local", artifactId = "test", version = "1.0.0", scope = "compile"))
    }
}
