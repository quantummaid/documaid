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
package de.quantummaid.documaid.usecases.hugo.heading

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.*

fun aFileWithH1Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH1Heading")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH1Heading("1_Introduction.md", "1")
            )
    }
}

fun aFileWithH2Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH2Heading")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH2Heading("1_Introduction.md")
            )
    }
}

fun multipleFilesWithHeadings(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "multipleFilesWithHeadings")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH1Heading("5_Introduction.md", "5"),
                aMarkdownFileWithADifferentH1Heading("1_Introduction.md", "1"),
                aMarkdownFileWithH1Heading("0123_Introduction.md", "123"),
                aMarkdownFileWithH1Heading("1_TwiceA.md", "1"),
                aMarkdownFileWithADifferentH1Heading("0_Introduction.md", "0"),
                aMarkdownFileWithH1Heading("1_TwiceB.md", "1")
            )
    }
}

fun aFileWithTextBeforeHeading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTextBeforeHeading")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTextBeforeHeading("1_Introduction.md", "1")
            )
    }
}

fun aFileWithNoIndex(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithNoIndex")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithADifferentH1Heading("Introduction.md", "0")
            )
    }
}

fun aFileWithExistingH1Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH1Heading")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithExistingH1Heading("1_Introduction.md", "10")
            )
    }
}
