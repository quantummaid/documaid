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

fun singleLinkSampleFiles(): SampleFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java file)-->" +
        "someOtherText"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java file)-->\n" +
        "[file](./ReferencedCodeFile.java)" +
        "someOtherText"
    val fileName = "fileWithOneLink.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun twoLinksSampleFiles(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n" +
        "text with the link <!---[Link] ( ./ReferencedCodeFile.java \"name with %A(H!3js@\")--> inside\n"
    val expectedContentOutput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n" +
        "[longer Name](./ReferencedCodeFile.java)\n" +
        "text with the link <!---[Link] ( ./ReferencedCodeFile.java \"name with %A(H!3js@\")-->\n" +
        "[name with %A(H!3js@](./ReferencedCodeFile.java) inside\n"
    val fileName = "fileWithTwoLinks.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun sameLinkTwiceSampleFiles(): SampleFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->" +
        "someOtherText\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->" +
        "\n" +
        "adsadsad\n"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n" +
        "[longer Name](./ReferencedCodeFile.java)" +
        "someOtherText\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n" +
        "[longer Name](./ReferencedCodeFile.java)" +
        "\n" +
        "adsadsad\n"
    val fileName = "fileWithSameLinkTwice.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun fileWithWrongLink(): SampleFile {
    val contentInput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java name) -->\n" +
        "[name](./differentFile.java)" +
        "someOtherText\n"
    val expectedContentOutput = "#Test File\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java name) -->" +
        "\n[name](./ReferencedCodeFile.java)" +
        "someOtherText\n"
    val fileName = "fileWithWrongLink.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun twoMissingLinksFileSampleFiles(): SampleFile {
    val contentInput = "<!---[Link] ( ./someWhere/notExistingFile.java linkName)-->\n" +
        "<!---[Link] ( differentNotExistingFile.java linkName)-->"
    val fileName = "missingLinksFile.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun correctlyGeneratedFileWithTwoLinks(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n[longer Name](./ReferencedCodeFile.java)\n" +
        "text with the link <!---[Link] ( ./ReferencedCodeFile.java \"name with %A(H!3js@\")-->\n" +
        "[name with %A(H!3js@](./ReferencedCodeFile.java) inside\n"
    val fileName = "correctlyGeneratedFileWithTwoLinks.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun oneMissingLinkFileSampleFiles(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"longer Name\")-->\n[longer Name](./ReferencedCodeFile.java)\n" +
        "text with the link <!---[Link] ( ./ReferencedCodeFile.java name)-->"
    val fileName = "oneMissingLinkFileSampleFiles.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun wrongLinkFileSampleFiles(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java \"name\")-->\n[name](./differentTarget.java)\n" +
        "restOfTheText"
    val fileName = "wrongLinkFileSampleFiles.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun aLinkToANotExistingFile(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" + "<!---[Link] ( ./someWhere/notExistingFile.java name)-->\n[mame](./someWhere/notExistingFile.java)"
    val fileName = "aLinkToANotExistingFile.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun multipleLinkErrors(): SampleFile {
    val contentInput = "Some Text with isolated Link\n" +
        "<!---[Link] ( ./ReferencedCodeFile.java name)-->[longer Name](./notExistingFile.java)\n" +
        "text with the link <!---[Link] ( ./ReferencedCodeFile.java name)-->"
    val fileName = "multipleLinkErrors.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}
