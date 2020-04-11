package de.quantummaid.documaid.config

interface Repository {

    fun urlToFile(relativePath: String): String
}

class GithubRepository private constructor(val baseUrl: String) : Repository {

    companion object {
        fun create(baseUrl: String): GithubRepository {
            return GithubRepository(baseUrl)
        }
    }

    override fun urlToFile(relativePath: String): String {
        return "$baseUrl/blob/master/$relativePath"
    }
}