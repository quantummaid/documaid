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

package de.quantummaid.documaid.link

import de.quantummaid.documaid.givenWhenThen.SampleFile
import de.quantummaid.documaid.givenWhenThen.SampleFilesBuilder

class LinkSampleFilesBuilder private constructor(private val sampleFile: SampleFile) : SampleFilesBuilder {

    override fun build(): SampleFile {
        return sampleFile
    }

    companion object {

        fun aFileWithALink(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(singleLinkSampleFiles())
        }

        fun aFileWithTwoLinks(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(twoLinksSampleFiles())
        }

        fun aFileWithTheSameLinksTwice(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(sameLinkTwiceSampleFiles())
        }

        fun aFileWithWrongLink(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(fileWithWrongLink())
        }

        fun aFileWithTheLinksToMissingFiles(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(twoMissingLinksFileSampleFiles())
        }

        fun aCorrectlyGeneratedFileWithTwoLinks(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(correctlyGeneratedFileWithTwoLinks())
        }

        fun aFileWithAMissingLink(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(oneMissingLinkFileSampleFiles())
        }

        fun aFileWithAWrongLink(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(wrongLinkFileSampleFiles())
        }

        fun aFileWithALinkToANotExistingFile(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(aLinkToANotExistingFile())
        }

        fun aFileWithMultipleLinkErrors(): LinkSampleFilesBuilder {
            return LinkSampleFilesBuilder(multipleLinkErrors())
        }
    }
}
