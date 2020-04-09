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

package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.shared.SutFileStructure

class Then internal constructor(private val dokuMaidTestBuilder: DokuMaidTestBuilder, private val dokuMaidActionTestBuilder: DokuMaidActionTestBuilder) {

    fun then(dokuMaidTestValidationBuilder: DokuMaidTestValidationBuilder) {
        val testEnvironment = dokuMaidTestBuilder.build()
        try {
            val setupSteps = getSetupSteps(testEnvironment)
            for (setupStep in setupSteps) {
                setupStep.invoke()
            }

            val sutFileStructure: SutFileStructure  = testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
            sutFileStructure.generateFileStructureForDocuMaidToProcess()

            val dokuMaid = testEnvironment.getPropertyAsType<de.quantummaid.documaid.DocuMaid>(TestEnvironmentProperty.DOKU_MAID_INSTANCE)
            val testAction = dokuMaidActionTestBuilder.build()
            testAction.invoke(dokuMaid)
        } catch (e: Exception) {
            testEnvironment.setProperty(TestEnvironmentProperty.EXCEPTION, e)
        } finally {
            try {
                val testValidation = dokuMaidTestValidationBuilder.build()
                testValidation.invoke(testEnvironment)
            } finally {
                cleanUp(testEnvironment)
            }
        }
    }

    private fun getSetupSteps(testEnvironment: TestEnvironment): List<() -> Unit> {
        @Suppress("UNCHECKED_CAST")
        return testEnvironment.getProperty(TestEnvironmentProperty.SETUP_STEPS) as List<() -> Unit>
    }

    private fun cleanUp(testEnvironment: TestEnvironment) {
        @Suppress("UNCHECKED_CAST")
        val cleanupSteps = testEnvironment.getProperty(TestEnvironmentProperty.CLEAN_UP_STEPS) as List<() -> Unit>
        for (cleanupStep in cleanupSteps) {
            cleanupStep.invoke()
        }
        val sutFileStructure: SutFileStructure  = testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
        sutFileStructure.cleanUp()
    }
}
