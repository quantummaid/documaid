package de.quantummaid.documaid.usecases.codeSnippet

import de.quantummaid.documaid.shared.NotProcessedSourceFile
import de.quantummaid.documaid.shared.PhysicalFileBuilder

class SampleJavaFileWithOneSnippet(javaFileBuilder: PhysicalFileBuilder, val snippet: String) : NotProcessedSourceFile(javaFileBuilder) {
    companion object {
        fun aJavaFileWithOneSnippet(fileName: String, snippetId: String): SampleJavaFileWithOneSnippet {
            val snippet = "public class SampleCodeSnippets {}"
            val content = "//Showcase start $snippetId\n" +
                snippet +
                "\n//Showcase end $snippetId\n\n"
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithOneSnippet(fileBuilder, snippet)
        }
    }
}

class SampleJavaFileWithADifferentSnippet(javaFileBuilder: PhysicalFileBuilder, val snippet: String) : NotProcessedSourceFile(javaFileBuilder) {
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
                "\n//Showcase end $snippetId\n\n" //TODO: hier die Leerzeilen weg
            val fileBuilder = PhysicalFileBuilder.aFile(fileName)
                .withContent(content)
            return SampleJavaFileWithADifferentSnippet(fileBuilder, snippet)
        }
    }
}


class SampleJavaFileWithACommentsInSnippet(javaFileBuilder: PhysicalFileBuilder, val snippet: String) : NotProcessedSourceFile(javaFileBuilder) {
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
            return SampleJavaFileWithACommentsInSnippet(fileBuilder, snippet)
        }
    }
}

