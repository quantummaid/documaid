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

import de.quantummaid.documaid.logging.Logger

class NoopTestLogger private constructor() : Logger {

    override val isDebugEnabled: Boolean
        get() = true

    override val isInfoEnabled: Boolean
        get() = true

    override val isWarnEnabled: Boolean
        get() = true

    override val isErrorEnabled: Boolean
        get() = false

    override fun debug(message: CharSequence) {
        // intentionally left blank
    }

    override fun debug(message: CharSequence, cause: Throwable) {
        // intentionally left blank
    }

    override fun debug(cause: Throwable) {
        // intentionally left blank
    }

    override fun info(message: CharSequence) {
        // intentionally left blank
    }

    override fun info(message: CharSequence, cause: Throwable) {
        // intentionally left blank
    }

    override fun info(cause: Throwable) {
        // intentionally left blank
    }

    override fun warn(message: CharSequence) {
        // intentionally left blank
    }

    override fun warn(message: CharSequence, cause: Throwable) {
        // intentionally left blank
    }

    override fun warn(cause: Throwable) {
        // intentionally left blank
    }

    override fun error(message: CharSequence) {
        // intentionally left blank
    }

    override fun error(message: CharSequence, cause: Throwable) {
        // intentionally left blank
    }

    override fun error(cause: Throwable) {
        // intentionally left blank
    }

    companion object {

        fun noopTestLogger(): NoopTestLogger {
            return NoopTestLogger()
        }
    }
}
