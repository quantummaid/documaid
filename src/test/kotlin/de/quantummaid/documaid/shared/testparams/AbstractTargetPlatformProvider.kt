package de.quantummaid.documaid.shared.testparams

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
        return type.equals(Platform::class)
    }


    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(parameterContext: ParameterContext,
                                  extensionContext: ExtensionContext): Any {
        return platform()
    }

    protected abstract fun platform(): Platform
}

class GithubPlatformProvider : AbstractTargetPlatformProvider(){

    override fun platform(): Platform {
        return Platform.GITHUB
    }
}

class HugoPlatformProvider : AbstractTargetPlatformProvider(){

    override fun platform(): Platform {
        return Platform.HUGO
    }
}