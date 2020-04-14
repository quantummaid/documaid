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
package de.quantummaid.documaid.givenWhenThen

import java.util.concurrent.ConcurrentHashMap

class TestEnvironment private constructor() {
    private val definedPropertiesMap = ConcurrentHashMap<String, Any>()

    fun setProperty(property: TestEnvironmentProperty, o: Any?) {
        setPropertyIfNotSet(property, o)
    }

    private fun setPropertyIfNotSet(property: TestEnvironmentProperty, o: Any?) {
        val name = property.name
        if (has(name)) {
            throw IllegalArgumentException("Property $name already set.")
        }
        if (o != null) {
            definedPropertiesMap[name] = o
        }
    }

    fun <T> getPropertyAsType(property: TestEnvironmentProperty): T {
        @Suppress("UNCHECKED_CAST")
        return getProperty(property) as T
    }

    fun getProperty(property: TestEnvironmentProperty): Any {
        return getProperty(property.name)
    }

    private fun getProperty(property: String): Any {
        val `object` = definedPropertiesMap[property]
        return `object` ?: throw RuntimeException("Property $property not set.")
    }

    fun has(property: TestEnvironmentProperty): Boolean {
        return has(property.name)
    }

    private fun has(property: String): Boolean {
        return definedPropertiesMap.containsKey(property)
    }

    companion object {

        fun emptyTestEnvironment(): TestEnvironment {
            return TestEnvironment()
        }
    }
}
