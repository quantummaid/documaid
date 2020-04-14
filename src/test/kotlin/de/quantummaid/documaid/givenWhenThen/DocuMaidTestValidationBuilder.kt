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

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.errors.ErrorsEncounteredInDokuMaidException
import de.quantummaid.documaid.shared.filesystem.SutFileStructure
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail

class DocuMaidTestValidationBuilder private constructor(
    private val testValidation: (testEnvironment: TestEnvironment) -> Unit
) {

    fun build(): (testEnvironment: TestEnvironment) -> Unit {
        return testValidation
    }

    companion object {

        fun expectAllFilesToBeCorrect(): DocuMaidTestValidationBuilder {
            return DocuMaidTestValidationBuilder { testEnvironment ->
                assertNoExceptionThrown(testEnvironment)
                assertAllFilesCorrectlyGenerated(testEnvironment)
            }
        }

        fun expectNoException(): DocuMaidTestValidationBuilder {
            return DocuMaidTestValidationBuilder { testEnvironment ->
                assertNoExceptionThrown(testEnvironment)
            }
        }

        fun expectAnExceptionWithMessage(expectedMessage: String): DocuMaidTestValidationBuilder {
            return DocuMaidTestValidationBuilder { assertExceptionWithMessage(expectedMessage, it) }
        }

        fun expectADocuMaidExceptionCollectingTheFollowingErrors(
            vararg expectedMessages: String
        ): DocuMaidTestValidationBuilder {
            return DocuMaidTestValidationBuilder { testEnvironment ->
                if (testEnvironment.has(TestEnvironmentProperty.EXCEPTION)) {
                    val exception = testEnvironment.getPropertyAsType<Exception>(TestEnvironmentProperty.EXCEPTION)
                    assertTrue(exception is ErrorsEncounteredInDokuMaidException)
                    val dokuMaidException = exception as ErrorsEncounteredInDokuMaidException
                    val errors = dokuMaidException.errors
                    val message = "Sizes of expected errors and actual errors do not match"
                    assertEquals(expectedMessages.size, errors.size, message)
                    for (i in expectedMessages.indices) {
                        val expectedMessage = expectedMessages[i]
                        val error = errors[i]
                        assertEquals(expectedMessage, error.message())
                    }
                } else {
                    fail<Any>("Expected an exception to be thrown, but none was found")
                }
            }
        }

        private fun assertNoExceptionThrown(testEnvironment: TestEnvironment) {
            if (testEnvironment.has(TestEnvironmentProperty.EXCEPTION)) {
                val exception = testEnvironment.getPropertyAsType<Exception>(TestEnvironmentProperty.EXCEPTION)
                fail<Any>(exception)
            }
        }

        private fun assertAllFilesCorrectlyGenerated(testEnvironment: TestEnvironment) {
            val sutFileStructure: SutFileStructure =
                testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
            val config: DocuMaidConfiguration =
                testEnvironment.getPropertyAsType(TestEnvironmentProperty.DOCU_MAID_CONFIG)
            val expectedFileStructure = when (config.platform) {
                Platform.GITHUB -> sutFileStructure.constructExpectedFileStructureForGithub()
                Platform.HUGO -> sutFileStructure.constructExpectedFileStructureForHugo(config)
            }
            TestFileStructureCorrectnessChecker.checkForCorrectness(expectedFileStructure)
        }

        private fun assertExceptionWithMessage(expectedMessage: String, testEnvironment: TestEnvironment) {
            if (testEnvironment.has(TestEnvironmentProperty.EXCEPTION)) {
                val exception = testEnvironment.getPropertyAsType<Exception>(TestEnvironmentProperty.EXCEPTION)
                val message = exception.message
                assertEquals(expectedMessage, message)
            } else {
                fail<Any>("Expected an exception to be thrown, but none was found")
            }
        }
    }
}
