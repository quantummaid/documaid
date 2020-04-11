package de.quantummaid.documaid.usecases.hugo.heading

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.config.Platform
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAllFilesToBeCorrect
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.given
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class HugoHeadingSpecs {

    @Test
    fun canGenerateH1Heading() {
        given(aDokuMaid()
            .configuredWith(aFileWithH1Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canIgnoreOtherHeadings() {
        given(aDokuMaid()
            .configuredWith(aFileWithH2Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun canReplaceHeadingsInMultipleFiles() {
        given(aDokuMaid()
            .configuredWith(multipleFilesWithHeadings(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun ignoresHeadingWhenTextOccursBefore() {
        given(aDokuMaid()
            .configuredWith(aFileWithTextBeforeHeading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAllFilesToBeCorrect())
    }

    @Test
    fun failsIfFileHasNoIndex() {
        given(aDokuMaid()
            .configuredWith(aFileWithNoIndex(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.GENERATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Cannot extract index from file ${absPath("aFileWithNoIndex/Introduction.md")}"))
    }

    @Test
    fun succeedsForValidH1Heading() {
        given(aDokuMaid()
            .configuredWith(aFileWithExistingH1Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    fun succeedsForNoHeading() {
        given(aDokuMaid()
            .configuredWith(aFileWithH2Heading(BASE_PATH))
            .configuredWith(Platform.HUGO)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/hugoHeading/"
    }

    fun absPath(fileName: String): String {
        return Paths.get(BASE_PATH).resolve(fileName).toAbsolutePath().toString()
    }
}
