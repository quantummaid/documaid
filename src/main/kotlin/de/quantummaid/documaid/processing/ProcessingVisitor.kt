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

package de.quantummaid.documaid.processing

import de.quantummaid.documaid.collecting.structure.Directory
import de.quantummaid.documaid.collecting.structure.Project
import de.quantummaid.documaid.collecting.structure.ProjectFile
import de.quantummaid.documaid.config.Goal

interface ProcessingVisitor {

    fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
    }

    fun afterDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
    }

    fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
    }

    fun afterFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
    }
}

open class GoalSpecificVisitor(private val targetGoal: Goal) : ProcessingVisitor {

    override fun beforeDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
        if (goal != targetGoal) return
    }

    override fun afterDirectoryProcessing(directory: Directory, project: Project, goal: Goal) {
        if (goal != targetGoal) return
    }

    override fun beforeFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
        if (goal != targetGoal) return
    }

    override fun afterFileProcessing(file: ProjectFile, project: Project, goal: Goal) {
        if (goal != targetGoal) return
    }
}

open class GenerationVisitor : GoalSpecificVisitor(Goal.GENERATE)
open class VerificationVisitor : GoalSpecificVisitor(Goal.VALIDATE)
