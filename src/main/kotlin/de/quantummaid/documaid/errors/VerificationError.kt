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
package de.quantummaid.documaid.errors

import de.quantummaid.documaid.collecting.structure.FileObject
import java.nio.file.Path

class VerificationError private constructor(val message: String, val path: Path?) : DokuMaidError {

    override fun message(): String = message

    companion object {
        fun createFromException(e: Exception, fileObject: FileObject?): VerificationError {
            val message = e.message ?: "[???] Obtained exception ${e.javaClass.canonicalName}"
            return VerificationError(message, fileObject?.absolutePath())
        }

        fun create(message: String, fileObject: FileObject): VerificationError {
            return createForPath(message, fileObject.absolutePath())
        }

        fun createForPath(message: String, path: Path): VerificationError {
            return VerificationError("$message (in path ${path.toAbsolutePath()})", path)
        }

        fun createWithoutFileOrigin(message: String): VerificationError {
            return VerificationError(message, null)
        }
    }
}
