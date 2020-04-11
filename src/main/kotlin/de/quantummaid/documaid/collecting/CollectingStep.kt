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

package de.quantummaid.documaid.collecting

import de.quantummaid.documaid.collecting.fastLookup.FastFileLookupTableCollector
import de.quantummaid.documaid.collecting.snippets.SnippetsCollector
import de.quantummaid.documaid.collecting.structure.FileObjectVisitorAdapter
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.traversaldecision.CollectingTraversalDecision
import de.quantummaid.documaid.config.DocuMaidConfiguration

class CollectingStep private constructor(private val collectors: List<FileObjectVisitorAdapter>) {

    companion object {
        fun create(): CollectingStep {
            val collectors = listOf(
                FastFileLookupTableCollector(),
                SnippetsCollector()
            )
            return CollectingStep(collectors)
        }
    }

    fun collect(docuMaidConfig: DocuMaidConfiguration, collectingTraversalDecision: CollectingTraversalDecision): Project {
        val collector = FullCollector()
        return collector.collectData(docuMaidConfig, collectors, collectingTraversalDecision)
    }
}
