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

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.generating.GenerationFlavorType
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class QuantumMaidFlavorSpecs {

    @Test
    fun quantumMaidFlavorWorksForTypicalQuantumMaidDirectoryStructure() {
        val hugoOutputPath = "2_TestMaid"
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aTypicalQuantumMaidProjectStructure(BASE_PATH, hugoOutputPath))
            .configuredWith(Platform.HUGO)
            .configuredWithHugoOutputPath(hugoOutputPath)
            .configuredWithFlavorType(GenerationFlavorType.QUANTUMMAID.name)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/flavors/quantummaid"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
