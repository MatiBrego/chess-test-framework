import edu.austral.dissis.chess.factory.createNormalChessGame
import edu.austral.dissis.chess.test.TestBoard
import edu.austral.dissis.chess.test.TestPosition
import edu.austral.dissis.chess.test.game.*
import edu.austral.dissis.common.game.Game

class TestGameRunnerImpl : TestGameRunner {

    private val adapter = ModelAdapter()
    private var game = createNormalChessGame(null)
    var history = RunnersHistory()

    constructor() {}

    private constructor(game: Game, history: RunnersHistory) : this() {
        this.game = game
        this.history = history
    }

    fun withHistory(history: RunnersHistory): TestGameRunnerImpl {
        return TestGameRunnerImpl(this.game, history)
    }

    fun withGame(game: Game): TestGameRunnerImpl {
        return TestGameRunnerImpl(game, history)
    }

    override fun executeMove(
        from: TestPosition,
        to: TestPosition
    ): TestMoveResult {
        val result = game.move(adapter.testPositionToCoordinate(from), adapter.testPositionToCoordinate(to))
        return adapter.toTestResult(result, this)
    }

    override fun getBoard(): TestBoard {
        return adapter.boardToTestBoard(game.getBoard())
    }

    override fun withBoard(board: TestBoard): TestGameRunner {
        val myBoard = adapter.testBoardToBoard(board)
        return TestGameRunnerImpl(createNormalChessGame(myBoard), history)
    }

    override fun redo(): TestMoveResult {
        return this.history.redo()
    }

    override fun undo(): TestMoveResult {
        return this.history.undo(this)
    }
}



