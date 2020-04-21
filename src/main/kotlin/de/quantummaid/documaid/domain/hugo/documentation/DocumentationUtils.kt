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
package de.quantummaid.documaid.domain.hugo.documentation

import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.DOCUMENTATION_DIRECTORY
import de.quantummaid.documaid.assumptions.HugoDocumentationAssumptions.Companion.DOCUMENTATION_LEGACY_DIRECTORY
import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.paths.makeRelativeTo
import de.quantummaid.documaid.domain.paths.pathUnderTopLevelDirectory
import java.nio.file.Path

fun isWithinDocumentationDirectory(absolutePath: Path, docuMaidConfiguration: DocuMaidConfiguration): Boolean {
    val relativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
    return pathUnderTopLevelDirectory(relativePath, DOCUMENTATION_DIRECTORY)
}

fun isWithinLegacyDirectory(absolutePath: Path, docuMaidConfiguration: DocuMaidConfiguration): Boolean {
    val relativePath = makeRelativeTo(absolutePath, docuMaidConfiguration.basePath)
    return pathUnderTopLevelDirectory(relativePath, DOCUMENTATION_LEGACY_DIRECTORY)
}

fun isDocumentationDirectory(absolutePath: Path, docuMaidConfiguration: DocuMaidConfiguration): Boolean {
    val docuMaidDirectory = docuMaidConfiguration.basePath.resolve(DOCUMENTATION_DIRECTORY)
    return absolutePath == docuMaidDirectory
}
