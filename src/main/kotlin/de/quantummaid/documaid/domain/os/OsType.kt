package de.quantummaid.documaid.domain.os

enum class OsType {
    LINUX,
    WINDOWS;

    companion object {
        fun forString(string: String): OsType {
            return when (string.toLowerCase()) {
                "linux" -> LINUX
                "windows" -> WINDOWS
                else -> throw IllegalArgumentException("Unknown os type '$string'")
            }
        }
    }
}
