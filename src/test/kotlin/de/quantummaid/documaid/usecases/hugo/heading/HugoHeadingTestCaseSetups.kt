package de.quantummaid.documaid.usecases.hugo.heading

import de.quantummaid.documaid.shared.filesystem.SetupUpdate
import de.quantummaid.documaid.shared.filesystem.TemporaryTestDirectory.Companion.aTemporyTestDirectory
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithADifferentH1Heading
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithExistingH1Heading
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithH1Heading
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithH2Heading
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithTextBeforeHeading
import de.quantummaid.documaid.shared.samplesFiles.aMarkdownFileWithWrongH1Heading

fun aFileWithH1Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH1Heading")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH1Heading("1_Introduction.md", "10")
            )
    }
}

fun aFileWithH2Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH2Heading")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH2Heading("1_Introduction.md")
            )
    }
}

fun multipleFilesWithHeadings(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "multipleFilesWithHeadings")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithH1Heading("5_Introduction.md", "50"),
                aMarkdownFileWithADifferentH1Heading("1_Introduction.md", "10"),
                aMarkdownFileWithH1Heading("0123_Introduction.md", "1230"),
                aMarkdownFileWithH1Heading("1_TwiceA.md", "10"),
                aMarkdownFileWithADifferentH1Heading("0_Introduction.md", "0"),
                aMarkdownFileWithH1Heading("1_TwiceB.md", "10")
            )
    }
}

fun aFileWithTextBeforeHeading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithTextBeforeHeading")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithTextBeforeHeading("1_Introduction.md")
            )
    }
}

fun aFileWithNoIndex(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithNoIndex")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithADifferentH1Heading("Introduction.md", "-")
            )
    }
}

fun aFileWithExistingH1Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH1Heading")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithExistingH1Heading("1_Introduction.md", "10")
            )
    }
}

fun aFileWithWrongH1Heading(basePath: String): SetupUpdate {
    val testDir = aTemporyTestDirectory(basePath, "aFileWithH1Heading")

    return { (_, _, sutFileStructure, _, _) ->
        sutFileStructure.inDirectory(testDir)
            .with(
                aMarkdownFileWithWrongH1Heading("1_Introduction.md")
            )
    }
}