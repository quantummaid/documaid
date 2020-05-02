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
package de.quantummaid.documaid.processing

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.Goal

interface ProcessingVisitor {

    fun beforeProcessingStart(project: Project, goal: Goal)

    fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal)

    fun afterDirectoryProcessing(
        directory: Directory,
        project: Project,
        goal: Goal,
        directoryProcessingResults: MutableList<ProcessingResult>
    )

    fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal)

    fun afterFileProcessing(
        file: ProjectFile,
        project: Project,
        goal: Goal,
        fileProcessingResults: MutableList<ProcessingResult>
    )

    fun afterProcessingFinish(project: Project, goal: Goal)
}

open class ProcessingVisitorAdapter : ProcessingVisitor {

    override fun beforeProcessingStart(project: Project, goal: Goal) {
        // left blank because of adapter pattern
    }

    override fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
        // left blank because of adapter pattern
    }

    override fun afterDirectoryProcessing(
        directory: Directory,
        project: Project,
        goal: Goal,
        directoryProcessingResults: MutableList<ProcessingResult>
    ) {
        // left blank because of adapter pattern
    }

    override fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
        // left blank because of adapter pattern
    }

    override fun afterFileProcessing(
        file: ProjectFile,
        project: Project,
        goal: Goal,
        fileProcessingResults: MutableList<ProcessingResult>
    ) {
        // left blank because of adapter pattern
    }

    override fun afterProcessingFinish(project: Project, goal: Goal) {
        // left blank because of adapter pattern
    }
}
