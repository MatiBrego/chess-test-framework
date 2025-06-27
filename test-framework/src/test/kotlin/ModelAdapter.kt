import edu.austral.dissis.chess.factory.createBishop
import edu.austral.dissis.chess.factory.createKing
import edu.austral.dissis.chess.factory.createKnight
import edu.austral.dissis.chess.factory.createPawn
import edu.austral.dissis.chess.factory.createQueen
import edu.austral.dissis.chess.factory.createRook
import edu.austral.dissis.chess.piece.ChessPieceType
import edu.austral.dissis.chess.test.TestBoard
import edu.austral.dissis.chess.test.TestPiece
import edu.austral.dissis.chess.test.TestPosition
import edu.austral.dissis.chess.test.TestSize
import edu.austral.dissis.chess.test.game.BlackCheckMate
import edu.austral.dissis.chess.test.game.TestMoveFailure
import edu.austral.dissis.chess.test.game.TestMoveResult
import edu.austral.dissis.chess.test.game.TestMoveSuccess
import edu.austral.dissis.chess.test.game.WhiteCheckMate
import edu.austral.dissis.common.board.Board
import edu.austral.dissis.common.board.BoardSize
import edu.austral.dissis.common.board.Coordinate
import edu.austral.dissis.common.piece.Team
import edu.austral.dissis.common.result.move.EndOfGameResult
import edu.austral.dissis.common.result.move.MoveResult
import edu.austral.dissis.common.result.move.SuccessfulResult
import edu.austral.dissis.common.result.move.UnsuccessfulResult

class ModelAdapter {

    private fun pieceToTestPiece(piece: edu.austral.dissis.common.piece.Piece): TestPiece {
        return when (piece.pieceType) {
            ChessPieceType.KING ->  TestPiece('K', teamToTestColor(piece.team))
            ChessPieceType.QUEEN -> TestPiece('Q', teamToTestColor(piece.team))
            ChessPieceType.ROOK -> TestPiece('R', teamToTestColor(piece.team))
            ChessPieceType.BISHOP -> TestPiece('B', teamToTestColor(piece.team))
            ChessPieceType.KNIGHT -> TestPiece('N', teamToTestColor(piece.team))
            ChessPieceType.PAWN -> TestPiece('P', teamToTestColor(piece.team))
            else -> throw IllegalArgumentException("Invalid piece type")
        }
    }

    private fun teamToTestColor(team: Team): Char {
        return when (team) {
            Team.BLACK -> 'B'
            Team.WHITE -> 'W'
        }
    }

    fun boardToTestBoard(board: Board): TestBoard {
        val boardMap = board.getPositions()

        val testBoardMap = HashMap<TestPosition, TestPiece>()

        for (entry in boardMap.entries) {
            testBoardMap.put(key = toTestPosition(entry.key), value=pieceToTestPiece(entry.value))
        }

        val testBoard = TestBoard(
            size = TestSize(
                cols = board.getBoardSize().getColumns(),
                rows = board.getBoardSize().getRows(),
            ),
            pieces = testBoardMap
        )

        return testBoard
    }

    private fun toTestPosition(coordinate: Coordinate) = TestPosition(row = coordinate.row, col = coordinate.column)

    fun testPieceToPiece(piece: TestPiece): edu.austral.dissis.common.piece.Piece {
        return when (piece) {
            TestPiece('K', 'W') -> createKing(Team.WHITE)
            TestPiece('Q', 'W') -> createQueen(Team.WHITE)
            TestPiece('R', 'W') -> createRook(Team.WHITE)
            TestPiece('B', 'W') -> createBishop(Team.WHITE)
            TestPiece('N', 'W') -> createKnight(Team.WHITE)
            TestPiece('P', 'W') -> createPawn(Team.WHITE)
            TestPiece('K', 'B') -> createKing(Team.BLACK)
            TestPiece('Q', 'B') -> createQueen(Team.BLACK)
            TestPiece('R', 'B') -> createRook(Team.BLACK)
            TestPiece('B', 'B') -> createBishop(Team.BLACK)
            TestPiece('N', 'B') -> createKnight(Team.BLACK)
            TestPiece('P', 'B') -> createPawn(Team.BLACK)
            else -> throw IllegalArgumentException("Invalid piece type")
        }
    }

    fun testPositionToCoordinate(position: TestPosition): Coordinate {
        return Coordinate(row = position.row, column = position.col)
    }

    fun testBoardToBoard(board: TestBoard): Board {
        val testBoardMap = board.pieces

        val boardMap = HashMap<Coordinate, edu.austral.dissis.common.piece.Piece>()

        for (entry in testBoardMap.entries) {
            boardMap.put(key = testPositionToCoordinate(entry.key), value=testPieceToPiece(entry.value))
        }

        val board = Board(
            boardSize = BoardSize(
                columns = board.size.cols,
                rows = board.size.rows,
            ),
            positions = boardMap
        )

        return board
    }

    fun toTestResult(result: MoveResult, runner: TestGameRunnerImpl): TestMoveResult {
        return when (result) {
            is SuccessfulResult -> TestMoveSuccess(
                runner
                    .withGame(result.game)
                    .withHistory(history = runner.history.add(runner))
            )
            is EndOfGameResult ->
                if (result.winner == Team.WHITE)
                    WhiteCheckMate(boardToTestBoard(result.finalBoard))
                else
                    BlackCheckMate(boardToTestBoard(result.finalBoard))
            is UnsuccessfulResult -> TestMoveFailure(runner.getBoard())
        }
    }
}