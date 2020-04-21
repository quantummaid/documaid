package de.quantummaid.documaid.domain.hugo.documentationWeights

class HugoWeight(val value: Int) {

    companion object {
        fun createForMultiLevelWeight(value: String): HugoWeight {
            val weightInSafeFormForHugo = Integer.parseInt("1${value}1")
            return HugoWeight(weightInSafeFormForHugo)
        }

        fun createForIndividualWeight(value: String): HugoWeight {
            val weigh = Integer.parseInt(value)
            return HugoWeight(weigh)
        }
    }
}