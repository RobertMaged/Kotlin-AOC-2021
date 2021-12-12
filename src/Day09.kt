import java.util.*


private data class Point(val row: Int, val col: Int)

fun main() {
    /**
     * @param destinationLowPoints lowest points will add to this list if provided.
     */
    fun part1(heightMap: Array<IntArray>, destinationLowPoints: MutableList<Point>? = null): Int {

        var riskLevel = 0

        for (row in heightMap.indices) {

            heightMap[row].forEachIndexed { col, currHeight ->

                val lowestAdjacent = minOf(
                    heightMap.getOrNull(row - 1)?.get(col) ?: 9, //top
                    heightMap[row].getOrNull(col - 1) ?: 9, //left
                    heightMap[row].getOrNull(col + 1) ?: 9, //right
                    heightMap.getOrNull(row + 1)?.get(col) ?: 9  //bottom
                )

                if (currHeight < lowestAdjacent) {
                    destinationLowPoints?.add(Point(row, col))
                    riskLevel += (currHeight + 1)
                }

            }
        }
        return riskLevel
    }

    fun part2(heightMap: Array<IntArray>, lowPoints: List<Point>): Int {

        val basins = lowPoints.map { point ->

            val searchQueue: Queue<Point> = LinkedList(listOf(point))

            val countedPoints = mutableListOf<Point>()

            while (searchQueue.isNotEmpty()) {
                val currPoint = searchQueue.poll() ?: continue
                //make sure not using out of bound point
                heightMap.getOrNull(currPoint.row)?.getOrNull(currPoint.col) ?: continue
                //make sure if already point counted
                if (currPoint in countedPoints) continue


                val validAdjacent = arrayOf(
                    Point(currPoint.row - 1, currPoint.col), //top
                    Point(currPoint.row, currPoint.col - 1), //left
                    Point(currPoint.row, currPoint.col + 1), //right
                    Point(currPoint.row + 1, currPoint.col)   //bottom
                ).filterNot { heightMap.getOrNull(it.row)?.getOrNull(it.col) in listOf(null,9) }


                searchQueue.addAll(validAdjacent)
                countedPoints.add(currPoint)
            }

            return@map countedPoints.size
        }

        return basins.sortedDescending().take(3).reduce { acc, i -> acc * i }
    }


    // test if implementation meets criteria from the description, like:
    val testHeightMap = readInput("Day09_test").map { row -> row.toCharArray().map { char -> char.digitToInt() }.toIntArray() }.toTypedArray()
    val testLowPoints = mutableListOf<Point>()
    check(part1(testHeightMap, testLowPoints) == 15)
    check(part2(testHeightMap, testLowPoints) == 1134)


    val heightMap = readInput("Day09").map { row -> row.toCharArray().map { char -> char.digitToInt() }.toIntArray() }.toTypedArray()
    val lowPoints = mutableListOf<Point>()

    println( part1(heightMap, lowPoints))
    println(part2(heightMap, lowPoints))
}
