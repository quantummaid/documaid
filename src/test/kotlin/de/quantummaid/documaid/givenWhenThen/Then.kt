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
package de.quantummaid.documaid.givenWhenThen

import de.quantummaid.documaid.DocuMaid
import de.quantummaid.documaid.config.DocuMaidConfigurationBuilder
import de.quantummaid.documaid.shared.filesystem.SutFileStructure

class Then internal constructor(
    private val dokuMaidTestBuilder: DokuMaidTestBuilder,
    private val dokuMaidActionTestBuilder: DokuMaidActionTestBuilder
) {

    fun then(docuMaidTestValidationBuilder: DocuMaidTestValidationBuilder) {
        val testEnvironment = dokuMaidTestBuilder.build()
        try {

            val sutFileStructure: SutFileStructure =
                testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
            val fileStructureForDocuMaidToProcess = sutFileStructure.generateFileStructureForDocuMaidToProcess()
            val configBuilder: DocuMaidConfigurationBuilder =
                testEnvironment.getPropertyAsType(TestEnvironmentProperty.DOCU_MAID_CONFIG_BUILDER)
            configBuilder.withBasePath(fileStructureForDocuMaidToProcess.baseDirectory.path)
            val docuMaidConfiguration = configBuilder.build()
            testEnvironment.setProperty(TestEnvironmentProperty.DOCU_MAID_CONFIG, docuMaidConfiguration)
            val docuMaid = DocuMaid.docuMaid(docuMaidConfiguration)

            val testAction = dokuMaidActionTestBuilder.build()
            testAction.invoke(docuMaid)
        } catch (e: Exception) {
            testEnvironment.setProperty(TestEnvironmentProperty.EXCEPTION, e)
        } finally {
            try {
                val testValidation = docuMaidTestValidationBuilder.build()
                testValidation.invoke(testEnvironment)
            } finally {
                cleanUp(testEnvironment)
            }
        }
    }

    private fun cleanUp(testEnvironment: TestEnvironment) {
        val sutFileStructure: SutFileStructure =
            testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
        sutFileStructure.cleanUp()
    }
}
