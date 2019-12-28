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

package de.quantummaid.documaid.logging

import org.apache.maven.plugin.logging.Log

class MavenLogger private constructor(private val log: Log) : Logger {

    override val isDebugEnabled: Boolean
        get() = log.isDebugEnabled

    override val isInfoEnabled: Boolean
        get() = log.isInfoEnabled

    override val isWarnEnabled: Boolean
        get() = log.isWarnEnabled

    override val isErrorEnabled: Boolean
        get() = log.isErrorEnabled

    override fun debug(message: CharSequence) {
        log.debug(message)
    }

    override fun debug(message: CharSequence, cause: Throwable) {
        log.debug(message, cause)
    }

    override fun debug(cause: Throwable) {
        log.debug(cause)
    }

    override fun info(message: CharSequence) {
        log.info(message)
    }

    override fun info(message: CharSequence, cause: Throwable) {
        log.info(message, cause)
    }

    override fun info(cause: Throwable) {
        log.info(cause)
    }

    override fun warn(message: CharSequence) {
        log.warn(message)
    }

    override fun warn(message: CharSequence, cause: Throwable) {
        log.warn(message, cause)
    }

    override fun warn(cause: Throwable) {
        log.warn(cause)
    }

    override fun error(message: CharSequence) {
        log.error(message)
    }

    override fun error(message: CharSequence, cause: Throwable) {
        log.error(message, cause)
    }

    override fun error(cause: Throwable) {
        log.error(cause)
    }

    companion object {

        fun mavenLogger(log: Log): MavenLogger {
            return MavenLogger(log)
        }
    }
}
