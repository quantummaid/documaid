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

package de.quantummaid.documaid.usecases.tableOfContents.toc

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.HugoPlatformProvider
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths

@ExtendWith(HugoPlatformProvider::class)
class HugoTableOfContentSpecs : TableOfContentSpecs {

    @Test
    override fun tocVerificationWithMissingToc(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aTocInReadmeWithMissingToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    override fun tocVerificationForValidToc(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aTocInReadmeWithCorrectToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [TOC] tag with incorrect TOC (in path ${absPath("aTocInReadmeWithCorrectToc/README.md")})"))
    }

    companion object {
        private val BASE_PATH = Paths.get("target/tempTestDirs/tableOfContents/")
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }
}
