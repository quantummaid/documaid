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
package de.quantummaid.documaid.collecting.structure

import de.quantummaid.documaid.config.DocuMaidConfiguration
import de.quantummaid.documaid.domain.java.JavaFile
import de.quantummaid.documaid.domain.kotlin.KotlinFile
import de.quantummaid.documaid.domain.markdown.MarkdownFile
import de.quantummaid.documaid.domain.unclassifiedFile.UnclassifiedFile
import de.quantummaid.documaid.domain.xml.XmlFile
import java.nio.file.Path

class FileCreator private constructor() {

    companion object {
        fun create(path: Path, docuMaidConfig: DocuMaidConfiguration): ProjectFile {
            val stringPath = path.toString()
            return when {
                stringPath.endsWith(".java") -> JavaFile.create(path)
                stringPath.endsWith(".kt") -> KotlinFile.create(path)
                stringPath.endsWith(".xml") -> XmlFile.create(path)
                stringPath.endsWith(".md") -> MarkdownFile.create(path, docuMaidConfig)
                else -> UnclassifiedFile.create(path)
            }
        }
    }
}
