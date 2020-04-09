package de.quantummaid.documaid.shared

import de.quantummaid.documaid.config.DocuMaidConfigurationBuilder
import de.quantummaid.documaid.givenWhenThen.TestEnvironment

typealias SetupUpdate = (setup:Setup) -> Unit

data class Setup (
    var testEnvironment: TestEnvironment,
    var configurationBuilder: DocuMaidConfigurationBuilder,
    var sutFileStructure: SutFileStructure,
    var setupSteps: MutableCollection<() -> Unit>,
    var cleanupSteps: MutableCollection<() -> Unit>)
