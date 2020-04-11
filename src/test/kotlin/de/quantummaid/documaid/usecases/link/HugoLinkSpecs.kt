package de.quantummaid.documaid.usecases.link

import de.quantummaid.documaid.config.Goal
import de.quantummaid.documaid.givenWhenThen.DokuMaidActionTestBuilder.Companion.theDokuIsPimped
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestBuilder.Companion.aDokuMaid
import de.quantummaid.documaid.givenWhenThen.DokuMaidTestValidationBuilder.Companion.expectNoException
import de.quantummaid.documaid.givenWhenThen.given
import de.quantummaid.documaid.shared.testparams.HugoPlatformProvider
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(HugoPlatformProvider::class)
class HugoLinkSpecs : LinkSpecs {

    @Test
    override fun canValidateCorrectLinks(platformConfiguration: PlatformConfiguration) {
        given(aDokuMaid()
            .configuredWith(aCorrectlyGeneratedFileWithTwoLinksForHugo(BASE_PATH))
            .configuredwith(platformConfiguration)
            .configuredWithGoal(Goal.VALIDATE))
            .`when`(theDokuIsPimped())
            .then(expectNoException())
    }

    companion object {
        private const val BASE_PATH = "target/tempTestDirs/link/"
    }
}