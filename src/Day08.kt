

private typealias InputSignals = MutableSet<Set<Char>>
private typealias OutputSignals = List<Set<Char>>

private data class Entry(val inputSignals: InputSignals, val outputSignals: OutputSignals)

fun main() {
    fun part1(input: List<String>): Int {
        val signals = input.map { it.substringAfter(" | ") }

        return signals.sumOf { massage ->
            massage.split(" ").count { word ->
                word.length !in listOf(5, 6)
            }
        }
    }

    fun part2(input: List<String>): Int {
        val signals = input.map { one ->
            val (tenInputList, fourOutputList) =
                one.split(" | ", limit = 2).map { it.split(" ").map { word -> word.toSet() } }

            return@map Entry(inputSignals = tenInputList.toMutableSet(), outputSignals = fourOutputList)
        }

        return signals.sumOf { entry ->
            val finalSegments = drawSegments(inputSignals = entry.inputSignals)

            return@sumOf buildString {
                entry.outputSignals.forEach { append(finalSegments[it]) }
            }.toInt()
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}


private fun drawSegments(inputSignals: InputSignals): Map<Set<Char>, Int> {

    val signalWires = mutableMapOf(
        1 to inputSignals.first { it.size == 2 },
        4 to inputSignals.first { it.size == 4 },
        7 to inputSignals.first { it.size == 3 },
        8 to inputSignals.first { it.size == 7 }
    ).also {
        inputSignals.removeAll(it.values.toSet())
    }

    /**
     * Order is very important here.
     * Each saved segment is removed from entry list.
     */
    // 9 must have [4] segments
    signalWires[9] =
        inputSignals.first { it.size == 6 && it.containsAll(signalWires.getValue(4)) }.also { inputSignals.remove(it) }

    // 0 must have [7] segments after filtering [9] value
    signalWires[0] =
        inputSignals.first { it.size == 6 && it.containsAll(signalWires.getValue(7)) }.also { inputSignals.remove(it) }

    // 6 is the last value has length 6 after filtering [9, 0] values
    signalWires[6] = inputSignals.first { it.size == 6 }.also { inputSignals.remove(it) }

    // 3 must have [7] segments
    signalWires[3] =
        inputSignals.first { it.size == 5 && it.containsAll(signalWires.getValue(7)) }.also { inputSignals.remove(it) }

    // 5 segments must less than [6] segments by one segment
    signalWires[5] = inputSignals.first { it.size == 5 && signalWires.getValue(6).minus(it).size == 1 }
        .also { inputSignals.remove(it) }

    // 2 is the last value has length 5 after filtering [3, 5] values
    signalWires[2] = inputSignals.first { it.size == 5 }.also { inputSignals.remove(it) }


    return signalWires.map { (number, segments) -> segments to number }.toMap()
}


private fun part2OtherWay(input: List<String>): Int {
    val signals = input.map { one ->
        val (tenInputList, fourOutputList) = one.split(" | ", limit = 2)
            .map { it.split(" ").map { word -> word.toSet() } }

        return@map Entry(inputSignals = tenInputList.toMutableSet(), outputSignals = fourOutputList)
    }

    return signals.sumOf { entry ->
        val inputSignals = entry.inputSignals
        val signalWires = mutableMapOf(
            1 to inputSignals.first { it.size == 2 },
            4 to inputSignals.first { it.size == 4 },
            7 to inputSignals.first { it.size == 3 },
            8 to inputSignals.first { it.size == 7 }
        )

        /**
         * Order is very important here.
         */
        // 9 must have [4] segments
        signalWires[9] = inputSignals.first { it.size == 6 && it.containsAll(signalWires.getValue(4)) }

        // 0 must have [7] segments after filtering [9] value
        signalWires[0] =
            inputSignals.first { it.size == 6 && it.containsAll(signalWires.getValue(7)) && it != signalWires[9] }

        // 6 is the last value has length 6 after filtering [9, 0] values
        signalWires[6] = inputSignals.first { it.size == 6 && it != signalWires[0] && it != signalWires[9] }

        // 3 must have [7] segments
        signalWires[3] = inputSignals.first { it.size == 5 && it.containsAll(signalWires.getValue(7)) }

        // 5 segments must less than [6] segments by one segment
        signalWires[5] = inputSignals.first { it.size == 5 && signalWires.getValue(6).minus(it).size == 1 }

        // 2 is the last value has length 5 after filtering [3, 5] values
        signalWires[2] = inputSignals.first { it.size == 5 && it != signalWires[3] && it != signalWires[5] }


        val c = signalWires.toList().associate { it.second to it.first }

        return@sumOf buildString { entry.outputSignals.forEach { append(c.getValue(it)) } }.toInt()
    }
}