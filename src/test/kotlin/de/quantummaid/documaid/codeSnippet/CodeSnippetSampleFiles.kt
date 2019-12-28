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

package de.quantummaid.documaid.codeSnippet

import de.quantummaid.documaid.givenWhenThen.SampleFile

fun singleCodeSnippetSampleFiles(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "someOtherText"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText"
    val fileName = "fileWithOneCodeSnippet.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun twoCodeSnippetsSampleFiles(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (second)-->\n" +
            "\n" +
            "adsadsad\n"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (second)-->\n" +
            "```java\n" +
            "if (Math.random() % 2 == 0) {\n" +
            "    System.out.println(\"Success\");\n" +
            "} else {\n" +
            "    System.out.println(\"Nope\");\n" +
            "}\n" +
            "```\n" +
            "\n" +
            "adsadsad\n"
    val fileName = "fileWithTwoCodeSnippets.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun sameCodeSnippetTwice(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "\n" +
            "adsadsad\n"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "\n" +
            "adsadsad\n"
    val fileName = "fileWithSameCodeSnippetTwice.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun nonJavaCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (example)-->\n" +
            "someOtherText"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (example)-->\n" +
            "```xml\n" +
            "<configuration>\n" +
            "    <propA>A</propA>\n" +
            "    <propB>\n" +
            "        <internal>internal</internal>\n" +
            "    </propB>\n" +
            "</configuration>\n" +
            "```\n" +
            "someOtherText"
    val fileName = "nonJavaCodeSnippet.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun snippetWithComments(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (third)-->\n" +
            "someOtherText"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (third)-->\n" +
            "```java\n" +
            "final Object o = new Object();//our first object\n" +
            "\n" +
            "// we create a     second    object\n" +
            "final Object o2 = new Object();\n" +
            "/*\n" +
            "and no we check on equality\n" +
            " */\n" +
            "o.equals(o2);\n" +
            "```\n" +
            "someOtherText"
    val fileName = "snippetWithComments.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun fullClassSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] ( file=./FullClassCodeSnippet.java)-->\n" +
            "someOtherText"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] ( file=./FullClassCodeSnippet.java)-->\n" +
            "```java\n" +
            "package de.quantummaid.documaid.codeSnippet;\n" +
            "\n" +
            "public class FullClassCodeSnippet {\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        Object o = new Object();\n" +
            "        log(o);\n" +
            "    }\n" +
            "\n" +
            "    private static void log(Object o) {\n" +
            "\n" +
            "    }\n" +
            "}\n\n" +
            "```\n" +
            "someOtherText"
    val fileName = "fullClassSnippet.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun incorrectlyGeneratedCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "something completely different" +
            "something completely different" +
            "something completely different" +
            "```\n" +
            "someOtherText\n" +
            "adsadsad\n"
    val expectedContentOutput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "adsadsad\n"
    val fileName = "fileWithIncorrectlyGeneratedCodeSnippet.md"
    return SampleFile.sampleFile(contentInput, expectedContentOutput, fileName)
}

fun correctlyGeneratedFileWithOneCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText"
    val fileName = "correctlyGeneratedFileWithOneCodeSnippet.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun aFileWithTwoOutdatedCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n"
    val expectedContent = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "final List<String> strings = new ArrayList<>();\n" +
            "strings.add(\"A\");\n" +
            "strings.add(\"B\");\n" +
            "strings.remove(1);\n" +
            "```\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n" +
            "someOtherText\n"
    val fileName = "aFileWithTwoOutdatedCodeSnippet.md"
    return SampleFile.sampleFile(contentInput, expectedContent, fileName)
}

fun wrongCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```java\n" +
            "somthing differently here" +
            "```\n" +
            "and the end of the text here" +
            "and the end of the text here" +
            "and the end of the text here" +
            "and the end of the text here" +
            "and the end of the text here" +
            "and the end of the text here"
    val fileName = "wrongCodeSnippet.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun missingCodeSnippet(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (notExisting)-->\n" +
            "endOfFile"
    val fileName = "missingCodeSnippet.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}

fun multipleCodeSnippetErrors(): SampleFile {
    val contentInput = "#Test File\n" +
            "<!---[CodeSnippet] (first)-->\n" +
            "```\n" +
            "somthing differently here" +
            "```\n" +
            "\"<!---[CodeSnippet] (first)-->\\n\""
    val fileName = "multipleCodeSnippetErrors.md"
    return SampleFile.inputOnlySampleFile(contentInput, fileName)
}
