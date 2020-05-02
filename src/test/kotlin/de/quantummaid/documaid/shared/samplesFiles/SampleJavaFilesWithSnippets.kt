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
package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.filesystem.NotProcessedSourceFile
import de.quantummaid.documaid.shared.filesystem.PhysicalFileBuilder

class SampleJavaFileWithOneSnippet private constructor(
    val fileName: String,
    val snippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aJavaFileWithOneSnippet(fileName: String, snippetId: String): SampleJavaFileWithOneSnippet {
            val snippet = "public class SampleCodeSnippets {}"
            val content = "//Showcase start $snippetId\n" +
                snippet +
                "\n//Showcase end $snippetId\n\n"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithOneSnippet(fileName, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithADifferentSnippet private constructor(
    val fileName: String,
    val snippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aJavaFileWithADifferentSnippet(fileName: String, snippetId: String): SampleJavaFileWithADifferentSnippet {
            val snippet = "final List<String> strings = new ArrayList<>();\n" +
                "strings.add(\"A\");\n" +
                "strings.add(\"B\");\n" +
                "strings.remove(1);\n" +
                "if (Math.random() % 2 == 0) {\n" +
                "    System.out.println(\"Success\");\n" +
                "} else {\n" +
                "    System.out.println(\"Nope\");\n" +
                "}"
            val content = "//Showcase start $snippetId\n" +
                snippet +
                "\n//Showcase end $snippetId"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithADifferentSnippet(fileName, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithACommentsInSnippet private constructor(
    val fileName: String,
    val snippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aJavaFileWithACommentsInSnippet(fileName: String, snippetId: String): SampleJavaFileWithACommentsInSnippet {
            val snippet = "final Object o = new Object();//our first object\n" +
                "\n" +
                "// we create a     second    object\n" +
                "final Object o2 = new Object();\n" +
                "/*\n" +
                "and no we check on equality\n" +
                " */\n" +
                "o.equals(o2);\n"
            val content = "//Showcase start $snippetId\n" +
                snippet +
                "\n//Showcase end $snippetId"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithACommentsInSnippet(fileName, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithFullClassSnippet private constructor(
    val fileName: String,
    val snippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aJavaFileWithOneFullSnippet(fileName: String): SampleJavaFileWithFullClassSnippet {
            val snippet = """
                package de.quantummaid.documaid.usecases.codeSnippet;

                public class FullClassCodeSnippet {

                public static void main(String[] args) {
                    Object o = new Object();
                    log(o);
                }

                private static void log(Object o) {

                }
             }
            """.trimIndent()
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(snippet)
            return SampleJavaFileWithFullClassSnippet(fileName, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithTwoSnippets private constructor(
    val fileName: String,
    val snippet1: String,
    val snippet2: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun aJavaFileWithTwoSnippets(
            fileName: String,
            snippetId1: String,
            snippetId2: String
        ): SampleJavaFileWithTwoSnippets {
            val snippet = "public class SampleCodeSnippets {}"
            val content = "//Showcase start $snippetId1\n" +
                snippet +
                "\n//Showcase end $snippetId1\n" +
                "\n" +
                "//Showcase start $snippetId2\n" +
                snippet +
                "\n//Showcase end $snippetId2\n" +
                "\n"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithTwoSnippets(fileName, snippet, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithSnippetWithSuppressWarning private constructor(
    val fileName: String,
    val expectedStrippedSnippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun sampleJavaFileWithSnippetWithSuppressWarning(
            fileName: String,
            snippetId: String,
            suppressedWarning: String = "'all'"
        ): SampleJavaFileWithSnippetWithSuppressWarning {
            val content = "Something else" +
                "//Showcase start $snippetId\n" +
                "public class SampleCodeSnippets {\n\n" +
                "    @SuppressWarnings($suppressedWarning)\n" +
                "    public static void main(){\n" +
                "    }\n" +
                "}\n" +
                "//Showcase end $snippetId"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            val expectedStrippedSnippet = "public class SampleCodeSnippets {\n\n" +
                "    public static void main(){\n" +
                "    }\n" +
                "}"

            return SampleJavaFileWithSnippetWithSuppressWarning(fileName, expectedStrippedSnippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithSnippetWithMultipleSuppressWarning private constructor(
    val fileName: String,
    val expectedStrippedSnippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun sampleJavaFileWithSnippetWithTwoSuppressWarning(
            fileName: String,
            snippetId1: String
        ): SampleJavaFileWithSnippetWithMultipleSuppressWarning {
            val content = "Something else" +
                "//Showcase start $snippetId1\n" +
                "public class SampleCodeSnippets {\n\n" +
                "    @SuppressWarnings({\"unused\", 'unchecked', 'sf:UnusedPrivateField'})\n" +
                "    public static void main(){\n" +
                "    }\n" +
                "}\n" +
                "//Showcase end $snippetId1"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            val expectedStrippedSnippet = "public class SampleCodeSnippets {\n\n" +
                "    public static void main(){\n" +
                "    }\n" +
                "}"

            return SampleJavaFileWithSnippetWithMultipleSuppressWarning(fileName, expectedStrippedSnippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithSnippetWithNoSonarComments private constructor(
    val fileName: String,
    val expectedStrippedSnippet: String,
    javaFileBuilder: PhysicalFileBuilder
) : NotProcessedSourceFile(javaFileBuilder) {

    companion object {
        fun sampleJavaFileWithSnippetWithNoSonarComments(
            fileName: String,
            snippetId1: String
        ): SampleJavaFileWithSnippetWithNoSonarComments {
            val content = "Something else" +
                "//Showcase start $snippetId1\n" +
                "public class SampleCodeSnippets {\n\n" +
                "    //NOSONAR\n" +
                "    public static void main(){\n" +
                "    final int x = 1 + 2;//NOSONAR\n" +
                "    final int y = 1 + 2;// NOSONAR\n" +
                "    final int z = 1 + 2; //NOSONAR\n" +
                "    }\n" +
                "}\n" +
                "//Showcase end $snippetId1"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            val expectedStrippedSnippet = "public class SampleCodeSnippets {\n\n" +
                "    public static void main(){\n" +
                "    final int x = 1 + 2;\n" +
                "    final int y = 1 + 2;\n" +
                "    final int z = 1 + 2; \n" +
                "    }\n" +
                "}"
            return SampleJavaFileWithSnippetWithNoSonarComments(fileName, expectedStrippedSnippet, fileBuilder)
        }
    }
}
