package de.quantummaid.documaid.usecases.tableOfContents.nav

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.domain.markdown.tagBased.navigation.NavigationDirective
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder
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
class HugoTableOfContentWithNavigationSpecs : TableOfContentWithNavigationSpecs{

    @Test
    override fun navigationValidationWithCorrectNavigations(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aReadmeWithAMissingTocAndASingleWithCorrectNavigationForHugo(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(DokuMaidActionTestBuilder.theDokuIsPimped())
            .then(expectAnExceptionWithMessage(
                "Found [${NavigationDirective.NAV_TAG}] tag with wrong navigation (in path ${absPath("correctNav/docs/1_Introduction.md")})"
            ))
    }

    @Test
    override fun navigationValidationForWrongNavigation(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aReadmeWithTocAndAFileWithASingleWrongNavForHugo(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(DokuMaidActionTestBuilder.theDokuIsPimped())
            .then(expectAnExceptionWithMessage("Found [${NavigationDirective.NAV_TAG}] tag with wrong navigation (in path ${absPath("wrongNav/docs/1_Introduction.md")})"))
    }

    @Test
    override fun navigationValidationForMissingNavigation(platformConfiguration: PlatformConfiguration) {
        given(DokuMaidTestBuilder.aDokuMaid()
            .configuredWith(aReadmeWithTocAndAFileWithASingleMissingNavForHugo(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(DokuMaidActionTestBuilder.theDokuIsPimped())
            .then(expectNoException())
    }

    private fun absPath(fileName: String): String {
        return BASE_PATH.toAbsolutePath().resolve(fileName).toString()
    }

    companion object {
        private val BASE_PATH = Paths.get("target/tempTestDirs/nav/")
    }

}