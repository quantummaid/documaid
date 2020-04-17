package de.quantummaid.documaid.domain.command

import de.quantummaid.documaid.domain.os.OsType

class MultiLineCommandNewLine private constructor(val value: String) {

    companion object {
        fun forOsType(osType: OsType): MultiLineCommandNewLine {
            val value = when (osType) {
                OsType.LINUX -> "\\\n"
                OsType.WINDOWS -> "^\n"
            }
            return MultiLineCommandNewLine(value)
        }
    }
}
