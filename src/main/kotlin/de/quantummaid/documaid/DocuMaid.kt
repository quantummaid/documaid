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

import de.quantummaid.documaid.collecting.CollectingStep
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.errors.ErrorsEncounteredInDokuMaidException
import de.quantummaid.documaid.preparing.PrepareStep
import de.quantummaid.documaid.processing.ProcessingStep

class DocuMaid private constructor(private val docuMaidConfiguration: DocuMaidConfiguration) {

    fun pimpMyDocu() {

        val goal = docuMaidConfiguration.goal
        val project = CollectingStep.create()
            .collect(docuMaidConfiguration.basePath)
        project.addInformation(DocuMaidConfiguration.DOCUMAID_CONFIGURATION_KEY, docuMaidConfiguration)

        val preparationErrors = PrepareStep.create()
            .prepare(project)
        if (preparationErrors.isNotEmpty()) {
            throw ErrorsEncounteredInDokuMaidException.fromVerificationErrors(preparationErrors)
        }

        val processingErrors = ProcessingStep
            .create().process(project, goal)
        if (processingErrors.isNotEmpty()) {
            throw ErrorsEncounteredInDokuMaidException.fromVerificationErrors(processingErrors)
        }
    }

    companion object {
        fun dokuMaid(configuration: DocuMaidConfiguration): DocuMaid {
            return DocuMaid(configuration)
        }
    }
}
