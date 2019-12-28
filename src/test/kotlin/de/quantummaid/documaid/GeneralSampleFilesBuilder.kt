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

package de.quantummaid.documaid

import de.quantummaid.documaid.config.DocuMaidConfigurationBuilder
import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.SampleFilesBuilder
import de.quantummaid.documaid.givenWhenThen.TestEnvironment
import de.quantummaid.documaid.givenWhenThen.TestEnvironmentProperty
import de.quantummaid.documaid.shared.createFileWithContent
import de.quantummaid.documaid.shared.deleteFileIfExisting

typealias Configurator = (testEnvironment: TestEnvironment, configurationBuilder: DocuMaidConfigurationBuilder, setupSteps: MutableCollection<() -> Unit>, cleanupSteps: MutableCollection<() -> Unit>) -> Unit

class GeneralSampleFilesBuilder private constructor(private val sampleFile: SampleFile) : SampleFilesBuilder {

    override fun build(): SampleFile {
        return sampleFile
    }

    companion object {

        fun severalFilesWithLinksAndSnippets(): Configurator {
            return { testEnvironment, _, setupSteps, cleanupSteps ->
                val sampleFiles = ArrayList<SampleFile>()
                sampleFiles.add(generalSampleFile1())
                sampleFiles.add(generalSampleFile2())
                testEnvironment.setProperty(TestEnvironmentProperty.MULTIPLE_SAMPLE_FILES, sampleFiles)

                setupSteps.add {
                    val basePath = testEnvironment.getPropertyAsType<String>(TestEnvironmentProperty.BASE_PATH)
                    for (sampleFile in sampleFiles) {
                        val contentInput = sampleFile.contentInput
                        val fileName = sampleFile.fileName
                        createFileWithContent(basePath, fileName, contentInput)
                    }
                }
                cleanupSteps.add {
                    val basePath = testEnvironment.getPropertyAsType<String>(TestEnvironmentProperty.BASE_PATH)
                    for (sampleFile in sampleFiles) {
                        val fileName = sampleFile.fileName
                        deleteFileIfExisting(basePath, fileName)
                    }
                }
            }
        }

        fun aCorrectlyGeneratedFileWithLinksAndSnippets(): GeneralSampleFilesBuilder {
            return GeneralSampleFilesBuilder(multipleGoalsValidationFile())
        }
    }
}
