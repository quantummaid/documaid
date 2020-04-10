package de.quantummaid.documaid.shared.samplesFiles

import de.quantummaid.documaid.shared.NotProcessedSourceFile
import de.quantummaid.documaid.shared.PhysicalFileBuilder

class SampleJavaFileWithOneSnippet private constructor(val fileName: String,
                                                       val snippet: String,
                                                       javaFileBuilder: PhysicalFileBuilder)
    : NotProcessedSourceFile(javaFileBuilder) {

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

class SampleJavaFileWithADifferentSnippet private constructor(val fileName: String,
                                                              val snippet: String,
                                                              javaFileBuilder: PhysicalFileBuilder)
    : NotProcessedSourceFile(javaFileBuilder) {

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
                "}\n"
            val content = "//Showcase start $snippetId\n" +
                snippet +
                "\n//Showcase end $snippetId"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithADifferentSnippet(fileName, snippet, fileBuilder)
        }
    }
}


class SampleJavaFileWithACommentsInSnippet private constructor(val fileName: String,
                                                               val snippet: String,
                                                               javaFileBuilder: PhysicalFileBuilder)
    : NotProcessedSourceFile(javaFileBuilder) {

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
                "\n//Showcase end $snippetId\n\n" //TODO: hier die Leerzeilen weg
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithACommentsInSnippet(fileName, snippet, fileBuilder)
        }
    }
}

class SampleJavaFileWithFullClassSnippet private constructor(val fileName: String,
                                                       val snippet: String,
                                                       javaFileBuilder: PhysicalFileBuilder)
    : NotProcessedSourceFile(javaFileBuilder) {

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

