package de.quantummaid.documaid.usecases.maven.archetype

import de.quantummaid.documaid.shared.testparams.HugoPlatformProvider
import de.quantummaid.documaid.shared.testparams.PlatformConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(HugoPlatformProvider::class)
class HugoArchetypeSpecs : ArchetypeSpecs {

    @Test
    override fun canValidateCorrectArchetype(platformConfiguration: PlatformConfiguration) {

    }

    @Test
    override fun canValidateCorrectArchetypeAtEndOfFile(platformConfiguration: PlatformConfiguration) {

    }

    @Test
    override fun failsForMissingArchetype(platformConfiguration: PlatformConfiguration) {

    }

    @Test
    override fun failsForIncorrectArchetype(platformConfiguration: PlatformConfiguration) {

    }

}