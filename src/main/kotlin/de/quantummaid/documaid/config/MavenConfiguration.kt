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

package de.quantummaid.documaid.config

import de.quantummaid.documaid.domain.maven.ArtifactId
import de.quantummaid.documaid.domain.maven.GroupId
import de.quantummaid.documaid.domain.maven.Version
import de.quantummaid.documaid.errors.DocuMaidException

class MavenConfiguration(
    private val groupId: GroupId?,
    private val artifactId: ArtifactId?,
    private val version: Version?
) {

    fun getGroupId(): GroupId {
        return groupId ?: throw DocuMaidException.createWithoutFileOrigin("Required groupId to be defined")
    }

    fun getArtifactId(): ArtifactId {
        return artifactId ?: throw DocuMaidException.createWithoutFileOrigin("Required artifactId to be defined")
    }

    fun getVersion(): Version {
        return version ?: throw DocuMaidException.createWithoutFileOrigin("Required version to be defined")
    }
}
