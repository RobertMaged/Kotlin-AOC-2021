

fun main() {
    fun part1(input: List<String>): Int {
        val allLines = input.map { it.toCharArray() }.toTypedArray()

        val bracesPairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        val wrongChars = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)

        return allLines.sumOf { line ->
            val stack = ArrayDeque<Char>()
            return@sumOf line.sumOf { char ->
                when {
                    char in bracesPairs.keys -> stack.addLast(char).let { 0 }
                    char != bracesPairs[stack.removeLast()] -> wrongChars.getValue(char)
                    else -> 0
                }
            }
        }
    }


    fun part2(input: List<String>): Long {
        val allLines = input.map { it.toCharArray() }.toTypedArray()

        val bracesPairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        val closingChars = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

        val scores = allLines.map { line ->

            val stack = ArrayDeque<Char>()

            line.forEach { char ->
                if (char in bracesPairs.keys) stack.addLast(char)
                // means wrong line just ignore and continue to next line.
                else if (char != bracesPairs[stack.removeLast()]) return@map null
            }

            return@map stack.reversed().map { bracesPairs.getValue(it) }
                .fold(0L) { total, char -> total * 5 + closingChars.getValue(char) }

        }.filterNotNull().sorted()

        return scores[scores.size / 2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input)) // answer 215229
    println(part2(input)) // answer 1105996483
}



fun part1OtherComplicatedWay(input: List<String>): Int {
    val bracesMapping = mapOf('(' to 1, ')' to -1, '[' to 2, ']' to -2, '{' to 3, '}' to -3, '<' to 4, '>' to -4)
    val lines = input.map { it.map { char -> bracesMapping.getValue(char) } }
    val closingValues = bracesMapping.filterValues { it < 0 }.map {
        it.value to when (it.key) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }.toMap()

    fun getWrongClosing(list: List<Int>): Int? {
        if (list.size < 2) return null

        //base case to ignore incomplete
        val closingIndex = list.indexOfFirst { it < 0 }
        if (closingIndex == -1) return null

        val line = list[closingIndex] + list[closingIndex - 1]

        return if (line == 0) getWrongClosing(
            list.subList(0, closingIndex - 1) + list.subList(
                closingIndex + 1,
                list.size
            )
        )
        else list[closingIndex] //return wrong closing as required
    }


    val result = buildMap<Int, Int> {
        lines.forEach { line ->
            val wrongClose = getWrongClosing(line)

            wrongClose?.let { this[it] = this.getOrDefault(it, 0).plus(1) }

        }
    }

    return result.map { it.value * closingValues.getOrDefault(it.key, 1) }.sum()
}
