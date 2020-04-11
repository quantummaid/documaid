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
import de.quantummaid.documaid.errors.VerificationError
import de.quantummaid.documaid.processing.ProcessingResult.Companion.contentNotChangedProcessingResult
import de.quantummaid.documaid.processing.ProcessingResult.Companion.erroneousProcessingResult

class ProcessingStep private constructor(private val visitors: List<ProcessingVisitor>) {

    companion object {
        fun create(): ProcessingStep {
            return ProcessingStep(emptyList())
        }
    }

    fun process(project: Project, goal: Goal): List<ProcessingResult> {
        val methodCaller = FileObjectMethodCaller.create(goal)
        val directory = project.rootDirectory
        return processDirectory(directory, methodCaller, project, goal)
    }

    private fun processDirectory(directory: Directory, methodCaller: FileObjectMethodCaller, project: Project, goal: Goal): List<ProcessingResult> {
        visitors.forEach { it.beforeDirectoryProcessing(directory, project, goal) }
        val processingResults = directory.children()
                .flatMap {
                    when (it) {
                        is Directory -> processDirectory(it, methodCaller, project, goal)
                        is ProjectFile -> processFile(it, methodCaller, project, goal)
                        else -> emptyList()
                    }
                }
        visitors.forEach { it.afterDirectoryProcessing(directory, project, goal) }
        return processingResults
    }

    private fun processFile(file: ProjectFile, methodCaller: FileObjectMethodCaller, project: Project, goal: Goal): List<ProcessingResult> {
        visitors.forEach { it.beforeFileProcessing(file, project, goal) }
        val processingResult = methodCaller.call(file, project)
        visitors.forEach { it.afterFileProcessing(file, project, goal) }
        return listOf(processingResult)
    }
}

internal interface FileObjectMethodCaller {

    companion object {
        fun create(goal: Goal): FileObjectMethodCaller {
            return when (goal) {
                Goal.GENERATE -> object : FileObjectMethodCaller {
                    override fun call(file: ProjectFile, project: Project): ProcessingResult {
                        return try {
                            file.process(project)
                        } catch (e: Exception) {
                            val errors = listOf(VerificationError.createFromException(e, file))
                            erroneousProcessingResult(file, errors)
                        }
                    }
                }
                Goal.VALIDATE -> object : FileObjectMethodCaller {
                    override fun call(file: ProjectFile, project: Project): ProcessingResult {
                        return try {
                            val errors = file.validate(project)
                            if(errors.isEmpty()){
                                contentNotChangedProcessingResult(file)
                            }else{
                                erroneousProcessingResult(file, errors)
                            }
                        } catch (e: Exception) {
                            val errors = listOf(VerificationError.createFromException(e, file))
                            erroneousProcessingResult(file, errors)
                        }
                    }
                }
            }
        }
    }

    fun call(file: ProjectFile, project: Project): ProcessingResult
}
