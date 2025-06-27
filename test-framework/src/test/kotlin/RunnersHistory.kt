import edu.austral.dissis.chess.test.game.TestMoveFailure
import edu.austral.dissis.chess.test.game.TestMoveResult
import edu.austral.dissis.chess.test.game.TestMoveSuccess

data class RunnersHistory(
    val history: List<TestGameRunnerImpl> = emptyList(),
    val undone: List<TestGameRunnerImpl> = emptyList()
) {

    fun add(runner: TestGameRunnerImpl): RunnersHistory {
        return RunnersHistory(history + runner, emptyList())
    }

    fun undo(current: TestGameRunnerImpl): TestMoveResult {
        return if (history.isNotEmpty()) {
            val last = history.last()
            TestMoveSuccess(last.withHistory(RunnersHistory(history.dropLast(1), undone + current)))
        } else {
            return TestMoveFailure(history.last().getBoard())
        }
    }

    fun redo(): TestMoveResult {
        if (undone.isNotEmpty()) {
            val lastUndone = undone.last()
            val newUndone = undone.dropLast(1)
            return TestMoveSuccess(lastUndone.withHistory(RunnersHistory(history + lastUndone, newUndone)))
        } else {
            return TestMoveFailure(history.last().getBoard())
        }
    }
}