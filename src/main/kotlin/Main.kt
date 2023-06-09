import kotlin.math.exp
import kotlin.math.pow


// With this parameter value, derivatives are calculated with an accuracy of 8 significant digits
const val ARG_SMALL_INCREMENT = 1.2E-4


/**
 * Calls function of calculating first and second derivatives values at [doubleVector]
 * @return map of names of first and second derivatives and its values.
 */
fun calculateDerivatives(doubleFunction: (DoubleArray) -> Double, doubleVector: (DoubleArray)): Map<String, Double> {

    val dim = doubleVector.size

    val firstPartialDerivatives: List<(DoubleArray) -> Double> = List(dim) { index ->
        { vector: DoubleArray -> partialDerivative(doubleFunction, index, vector) }
    }

    val namedMapOfFirstPartialDerivativesAtPoint: Map<String, Double> =
        firstPartialDerivatives.mapIndexed { index: Int, firstPartialDerivative: (DoubleArray) -> Double ->
            "∂f/∂x_${index + 1}" to firstPartialDerivative(doubleVector)
        }.toMap()


    val namedMapOfSecondPartialDerivativesAtPoint =
        firstPartialDerivatives.mapIndexed { firstDerivativeIndex: Int, firstDerivative: (DoubleArray) -> Double ->
            List(dim) { secondDerivativeIndex: Int ->
                "∂2f/∂x_${secondDerivativeIndex + 1}∂x_${firstDerivativeIndex + 1}" to
                        partialDerivative(firstDerivative, secondDerivativeIndex, doubleVector)
            }
        }.reduce { accListOfPairs, listOfPairs -> accListOfPairs + listOfPairs }.toMap()

    return namedMapOfFirstPartialDerivativesAtPoint + namedMapOfSecondPartialDerivativesAtPoint
}

/**
 * Print in console map in format "key = value" for each pair in [map]
 */
fun printMapOfValuesNamesToValue(map: Map<String, Double>) = map.forEach { println("${it.key} = ${it.value}") }

/**
 * Calculates derivative of [doubleFunction] at point [doubleVector] numerically
 * @return value of derivative at point.
 */
fun partialDerivative(doubleFunction: (DoubleArray) -> Double, index: Int, doubleVector: DoubleArray): Double {
    val dim = doubleVector.size

    val doubleVectorWithPartialIncrement = { dx: Double ->
        DoubleArray(dim) { if (it == index) doubleVector[it] + dx else doubleVector[it] }
    }

    val functionDoubledPartialIncrement = { dx: Double ->
        doubleFunction(doubleVectorWithPartialIncrement(dx)) - doubleFunction(doubleVectorWithPartialIncrement(-dx))
    }

    val derivativeApproximation = { dx: Double ->
        functionDoubledPartialIncrement(dx) / (2 * dx)
    }

    return derivativeApproximation(ARG_SMALL_INCREMENT)
}

/**
 * @return DoubleArray of values, which were read from console.
 */
fun readPointFromConsole(): DoubleArray = readln().split(" ").map { it.toDouble() }.toDoubleArray()


/**
 * Math function that should be investigated for the derivatives.
 */
fun userFun(doubleVector: DoubleArray): Double {
    return exp(doubleVector.sum().pow(-2) + exp(doubleVector[0] - 1).pow(3))
}


fun main() {

    val point = readPointFromConsole()
    val derivativesAtPoint = calculateDerivatives(::userFun, point)

    printMapOfValuesNamesToValue(derivativesAtPoint)
}
