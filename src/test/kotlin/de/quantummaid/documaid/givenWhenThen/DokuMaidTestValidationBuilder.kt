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

import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.errors.ErrorsEncounteredInDokuMaidException
import de.quantummaid.documaid.shared.SutFileStructure
import de.quantummaid.documaid.shared.assertFileWithContent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail

class DokuMaidTestValidationBuilder private constructor(private val testValidation: (testEnvironment: TestEnvironment) -> Unit) {

    fun build(): (testEnvironment: TestEnvironment) -> Unit {
        return testValidation
    }

    companion object {

        fun expectAllFilesToBeCorrect(): DokuMaidTestValidationBuilder {
            return DokuMaidTestValidationBuilder { testEnvironment ->
                assertNoExceptionThrown(testEnvironment)
                val sutFileStructure: SutFileStructure = testEnvironment.getPropertyAsType(TestEnvironmentProperty.SUT_FILE_STRUCTURE)
                val platform: Platform = testEnvironment.getPropertyAsType(TestEnvironmentProperty.PLATFORM)
                val expectedFileStructure = when (platform) {
                    Platform.GITHUB -> sutFileStructure.generateExpectedFileStructureForGithub()
                    Platform.HUGO -> sutFileStructure.generateExpectedFileStructureForHugo()
                }
                TestFileStructureCorrectnessChecker.checkForCorrectness(expectedFileStructure)
            }
        }

        fun expectAnExceptionWithMessage(expectedMessage: String): DokuMaidTestValidationBuilder {
            return DokuMaidTestValidationBuilder { testEnvironment -> assertExceptionWithMessage(expectedMessage, testEnvironment) }
        }


        fun expectADokuMaidExceptionCollectingTheFollowingErrors(vararg expectedMessages: String): DokuMaidTestValidationBuilder {
            return DokuMaidTestValidationBuilder { testEnvironment ->
                if (testEnvironment.has(TestEnvironmentProperty.EXCEPTION)) {
                    val exception = testEnvironment.getPropertyAsType<Exception>(TestEnvironmentProperty.EXCEPTION)
                    assertTrue(exception is ErrorsEncounteredInDokuMaidException)
                    val dokuMaidException = exception as ErrorsEncounteredInDokuMaidException
                    val errors = dokuMaidException.errors
                    assertEquals(expectedMessages.size, errors.size, "Sizes of expected errors and actual errors do not match")
                    for (i in expectedMessages.indices) {
                        val expectedMessage = expectedMessages[i]
                        val error = errors[i]
                        val message = error.message()
                        assertEquals(expectedMessage, message)
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
