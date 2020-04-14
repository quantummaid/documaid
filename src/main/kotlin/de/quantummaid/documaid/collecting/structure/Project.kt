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

class Project private constructor(
    val rootDirectory: Directory,
    private val collectedInformationMap: CollectedInformationMap
) {

    companion object {
        fun create(rootDirectory: Directory): Project {
            return Project(rootDirectory, CollectedInformationMap())
        }
    }

    fun <T> addInformation(key: CollectedInformationKey<T>, value: T) {
        collectedInformationMap.put(key, value as Any)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getInformation(key: CollectedInformationKey<T>): T {
        return collectedInformationMap[key] as T
    }
}
