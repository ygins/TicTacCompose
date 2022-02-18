import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

enum class Screen {
    MENU,
    GAME,
    HELP
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.MENU) }
    val changeTo: (Screen) -> Unit = { currentScreen = it }
    MaterialTheme {
        Common(onClick = { currentScreen = Screen.MENU }) {
            when (currentScreen) {
                Screen.MENU -> Menu(changeTo)
                Screen.GAME -> Game()
                Screen.HELP -> Help()
            }
        }
    }
}

@Composable
fun Common(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            IconButton(onClick = onClick) {
                Icon(Icons.Filled.Home, contentDescription = "Back")
            }
        }
        content()
    }
}

@Composable
fun Menu(changeTo: (Screen) -> Unit) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        MenuButton("Game") { changeTo(Screen.GAME) }
        MenuButton("Help") { changeTo(Screen.HELP) }
    }
}

@Composable
fun MenuButton(name: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.width(200.dp)) {
        Text(name)
    }
}

@Composable
fun Help() {
    Surface(
        color = MaterialTheme.colors.secondary,
        modifier = Modifier.fillMaxSize().padding(50.dp).wrapContentSize(align = Alignment.Center)
    ) {
        Text(
            "Click a box to place a symbol (X if you're player 1, or O if you're player 2). Three of your symbols in a row" +
                    " constitutes a win. If no more symbols can be placed, the game ends.", textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Game(gameState: GameState = rememberGameState()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 75.dp)
    ) {
        TopText(gameState)
        Spacer(modifier = Modifier.size(10.dp))
        PlayerIndicators(gameState.currentPlayer)
        Spacer(modifier = Modifier.size(10.dp))
        TicTacToeGrid(gameState)
        if (gameState.isWon()) {
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedButton(onClick = { gameState.reset() }) {
                Text("Restart")
            }
        }
    }
}

@Composable
fun TopText(gameState: GameState) {
    val win = gameState.winner != null
    val player = "Player ${if (gameState.currentPlayer == Player.ONE) 1 else 2}"
    val text = "$player${if (win) " won the game!" else ", it's your turn!"}"
    Text(text, fontWeight = FontWeight.Bold)
}

@Composable
fun TicTacToeGrid(gridState: GameState) {
    Row(modifier = Modifier.size(300.dp, 300.dp)) {
        repeat(3) { column ->
            Column(modifier = Modifier.size(100.dp, 300.dp)) {
                repeat(3) { row ->
                    TicTacToeBox(gridState, (row * 3) + column)
                }
            }
        }
    }
}

@Composable
fun TicTacToeBox(gridState: GameState, index: Int) {
    Box(
        modifier = Modifier.size(100.dp).border(2.dp, Color.Black)
            .background(Color.Yellow).clickable {
                val result = gridState.place(index)
                println(result)
            }.wrapContentSize(Alignment.Center)
    ) {
        if (gridState[index] != SquareContent.EMPTY) {
            Text(
                if (gridState[index] == SquareContent.ONE) "X" else "O",
                textAlign = TextAlign.Center,
                fontSize = 5.em,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun PlayerIndicators(currentPlayer: Player) {
    Row {
        PlayerIndicator("Player 1", Color.Red, currentPlayer == Player.ONE)
        Spacer(modifier = Modifier.size(8.dp))
        PlayerIndicator("Player 2", Color.Yellow, currentPlayer == Player.TWO)
    }
}

@Composable
fun PlayerIndicator(name: String, color: Color, enabled: Boolean) {
    Column {
        Card(
            modifier = Modifier.size(30.dp, 10.dp),
            backgroundColor = if (enabled) Color.White else color,
            border = if (enabled) BorderStroke(1.dp, Color.Black) else null
        ) {}
        Text(
            name,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = if (enabled) Modifier.border(1.dp, Color.Black) else Modifier
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "TicTacCompose") {
        App()
    }
}
