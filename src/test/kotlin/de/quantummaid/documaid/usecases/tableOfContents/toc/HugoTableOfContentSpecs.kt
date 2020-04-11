package de.quantummaid.documaid.usecases.tableOfContents.toc

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectAnExceptionWithMessage
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.HugoPlatformProvider
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths

@ExtendWith(HugoPlatformProvider::class)
class HugoTableOfContentSpecs : TableOfContentSpecs {

    @Test
    override fun tocVerificationWithMissingToc(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aTocInReadmeWithMissingToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    @Test
    override fun tocVerificationForValidToc(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aTocInReadmeWithCorrectToc(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [TOC] tag with incorrect TOC (in path ${absPath("aTocInReadmeWithCorrectToc/README.md")})"))
    }

    companion object {
        private val BASE_PATH = Paths.get("target/tempTestDirs/tableOfContents/")
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }
}