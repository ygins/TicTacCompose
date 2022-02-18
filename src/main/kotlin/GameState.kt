import androidx.compose.runtime.*

class SimpleGameState : GameState {
    private var grid by mutableStateOf(Array(3) { Array(3) { SquareContent.EMPTY } })
    override var currentPlayer by mutableStateOf(Player.ONE)
        private set
    override var winner: Player? by mutableStateOf(null)
        private set
    override fun get(num: Int) = grid[num / 3][num % 3]
    override fun place(targetSquare: Int): PlaceResult {
        val value = currentPlayer.symbol
        val row = targetSquare / 3
        val column = targetSquare % 3
        if(grid[row][column]!=SquareContent.EMPTY||winner!=null){
            return PlaceResult.ILLEGAL
        }
        val newGrid = grid.clone()
        newGrid[row][column] = value
        grid = newGrid
        fun win():PlaceResult{
            this.winner=currentPlayer
            return PlaceResult.WIN
        }
        //check row
        if (newGrid[row].all { it == value }) return win()
        //check column
        else if ((0..2).all { newGrid[it][column] == value }) return win()
        //check diagonals
        val onDiagonal = !(((row == 0 || row == 2) && column == 1) || (row == 1 && (column == 0 || column == 2)))
        if (onDiagonal) {
            if ((0..2).all { newGrid[it][it] == value }) return win()
            else if ((0..2).all { newGrid[it][2 - it] == value }) return win()
        }
        //if full, stalemate
        if (newGrid.all { it.all { content -> content != SquareContent.EMPTY } }) return PlaceResult.STALEMATE
        //else, neutral placement
        currentPlayer=currentPlayer.switch()
        return PlaceResult.NEUTRAL
    }

    override fun reset() {
        this.grid=newClearBoard()
        this.currentPlayer=Player.ONE
        this.winner=null
    }
    private fun newClearBoard()=Array(3){Array(3){SquareContent.EMPTY} }
}

@Composable
fun rememberGameState() = remember { SimpleGameState() }


interface GameState {
    operator fun get(num: Int): SquareContent
    fun place(targetSquare: Int): PlaceResult
    fun reset()
    fun isWon()=winner!=null
    val currentPlayer: Player
    val winner: Player?
}

enum class PlaceResult {
    NEUTRAL,
    ILLEGAL,
    WIN,
    STALEMATE
}

enum class SquareContent {
    ONE,
    TWO,
    EMPTY
}

enum class Player(val symbol: SquareContent) {
    ONE(SquareContent.ONE),
    TWO(SquareContent.TWO);

    fun switch() = if (this == ONE) TWO else ONE
}