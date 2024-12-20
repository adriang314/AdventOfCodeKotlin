package common

data class Cell<T>(val position: Position, val value: T) {
    var n: Cell<T>? = null
    var s: Cell<T>? = null
    var e: Cell<T>? = null
    var w: Cell<T>? = null
    var nw: Cell<T>? = null
    var ne: Cell<T>? = null
    var sw: Cell<T>? = null
    var se: Cell<T>? = null
}

class Grid<T>(input: String, valueMapper: (Grid<T>, Char, Position) -> T) {
    val cells: Map<Position, Cell<T>>

    init {
        val lines = input.lines()
        val tempCells = mutableMapOf<Position, Cell<T>>()
//   ------X----->
//   |
//   |
//   |
//   Y
//   |
//   |
//   |
//   v
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                val position = Position(x, y)
                val name = lines[y][x]
                tempCells[position] = Cell(position, valueMapper(this, name, position))
            }
        }

        tempCells.values.forEach { cell ->
            cell.n = tempCells[cell.position.n()]
            cell.s = tempCells[cell.position.s()]
            cell.e = tempCells[cell.position.e()]
            cell.w = tempCells[cell.position.w()]
            cell.nw = tempCells[cell.position.nw()]
            cell.ne = tempCells[cell.position.ne()]
            cell.sw = tempCells[cell.position.sw()]
            cell.se = tempCells[cell.position.se()]
        }

        cells = tempCells
    }

    fun getCell(position: Position): Cell<T>? {
        return cells[position]
    }
}