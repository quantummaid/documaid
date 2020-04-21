package de.quantummaid.documaid.domain.hugo.documentationWeights

class HugoDirectoryWeightPadder {
    companion object {
        fun padIndex(index: Int): String {
            val indexString = index.toString()
            return when {
                indexString.length == 1 -> "0$indexString"
                indexString.length == 2 -> indexString
                indexString.length > 2 -> throw IllegalArgumentException("Only indices between 1 and 99 allowed")
                else -> throw IllegalArgumentException("Illegal index $indexString")
            }
        }
    }
}
