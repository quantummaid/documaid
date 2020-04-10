package de.quantummaid.documaid.usecases.maven.dependency

import de.quantummaid.documaid.shared.testparams.GithubPlatformProvider
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(GithubPlatformProvider::class)
class GithubDependencySpecs : DependencySpecs