package de.quantummaid.documaid.usecases.maven.archetype

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileAlreadyGeneratedArchetype
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithAlreadyGeneratedArchetypeAtEndOfFile
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithOneArchetype
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithOneArchetypeAtEndOfFile
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongArchetype
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongArchetypeAtEndOfFile

fun aFileWithASingleFullyDefinedArchetype(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithASingleFullyDefinedArchetype")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneArchetype("archetype.md",
                    archetypeGroupId = "com.quantummaid", archetypeArtifactId = "archetype", archetypeVersion = "1.0.0",
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java", os = "linux")
            )
    }
}

fun aFileWithASingleMinimalDefinedArchetype(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithASingleMinimalDefinedArchetype")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneArchetype("archetype.md",
                    null, null, null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java")
            )
    }
}

fun aFileWithAnArchetypeAtEndOfFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAnArchetypeAtEndOfFile")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneArchetypeAtEndOfFile("archetype.md",
                    null, "test", null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java")
            )
    }
}

fun aFileWithAnArchetypeForWindows(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAnArchetypeForWindows")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneArchetypeAtEndOfFile("archetype.md",
                    null, "test", null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java",
                    os = "windows", lineBreakingChar = "^")
            )
    }
}

fun aFileWithAWrongArchetype(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAWrongArchetype")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithWrongArchetype("archetype.md",
                    null, "test", null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java")
            )
    }
}

fun aFileWithAWrongArchetypeAtEndOfFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAWrongArchetypeAtEndOfFile")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithWrongArchetypeAtEndOfFile("archetype.md",
                    null, "test", null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java")
            )
    }
}

fun aFileWithAlreadyGeneratedArchetype(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAlreadyGeneratedArchetype")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileAlreadyGeneratedArchetype("archetype.md",
                    archetypeGroupId = "com.quantummaid", archetypeArtifactId = null, archetypeVersion = "1.0.0",
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "pom", os = "linux")
            )
    }
}

fun aFileWithAlreadyGeneratedArchetypeAtEndOfFile(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAlreadyGeneratedArchetypeAtEndOfFile")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithAlreadyGeneratedArchetypeAtEndOfFile("archetype.md",
                    archetypeGroupId = "com.quantummaid", archetypeArtifactId = "test", archetypeVersion = null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "pom",
                    os = "windows", lineBreakingChar = "^")
            )
    }
}

fun aFileWithAMissingArchetype(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithAMissingArchetype")

    return { (_, sutFileStructure) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithOneArchetype("archetype.md",
                    archetypeGroupId = null, archetypeArtifactId = null, archetypeVersion = null,
                    groupId = "local", artifactId = "test", version = "1.0.0", packaging = "java")
            )
    }
}
