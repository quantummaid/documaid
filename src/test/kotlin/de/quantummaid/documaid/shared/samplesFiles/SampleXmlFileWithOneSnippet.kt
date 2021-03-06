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
package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.NotProcessedSourceFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileBuilder

class SampleXmlFileWithOneSnippet private constructor(
    val fileName: String,
    val snippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aXmlFileWithOneSnippet(fileName: String, snippetId: String): SampleXmlFileWithOneSnippet {
            val snippet =
                """
<configuration>
    <propA>A</propA>
    <propB>
        <internal>internal</internal>
    </propB>
</configuration>
""".trim()
            val content =
                """
<!-- Showcase start $snippetId -->
$snippet
<!-- Showcase end $snippetId -->
"""
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleXmlFileWithOneSnippet(fileName, snippet, fileBuilder)
        }
    }
}
