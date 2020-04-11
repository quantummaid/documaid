package de.quantummaid.documaid.shared.testparams

import de.quantummaid.documaid.config.DocuMaidConfigurationBuilder
import de.quantummaid.documaid.config.Platform
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver

abstract class AbstractTargetPlatformProvider : ParameterResolver {

    @Throws(ParameterResolutionException::class)
    override fun supportsParameter(parameterContext: ParameterContext,
                                   extensionContext: ExtensionContext): Boolean {
        val parameter = parameterContext.parameter
        val type = parameter.annotatedType.type
        return type.equals(PlatformConfiguration::class.java)
    }


    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(parameterContext: ParameterContext,
                                  extensionContext: ExtensionContext): Any {
        return platformConfiguration()
    }

    protected abstract fun platformConfiguration(): PlatformConfiguration
}

class GithubPlatformProvider : AbstractTargetPlatformProvider(){

    override fun platformConfiguration(): PlatformConfiguration {
        return object: PlatformConfiguration {
            override fun apply(configurationBuilder: DocuMaidConfigurationBuilder) {
                configurationBuilder.forPlatform(Platform.GITHUB)
            }
        }
    }
}

class HugoPlatformProvider : AbstractTargetPlatformProvider(){

    override fun platformConfiguration(): PlatformConfiguration {
        return object: PlatformConfiguration {
            override fun apply(configurationBuilder: DocuMaidConfigurationBuilder) {
                configurationBuilder.forPlatform(Platform.HUGO)
            }
        }
    }
}

interface PlatformConfiguration{

    fun apply(configurationBuilder: DocuMaidConfigurationBuilder)
}