package io.github.ryangryota.gomoku.ui.screen.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ryangryota.gomoku.domain.model.Board
import io.github.ryangryota.gomoku.domain.model.Player

/**
 * GomokuBoardは五目並べの盤面を表示するComposable関数です。
 * 盤面のセルをタップするとonCellClickが呼ばれます。
 * showStoneColorsがtrueの場合は石の色でプレイヤーを区別し、falseの場合は全て白石で表示します。
 * Androidアプリ開発未経験者向けに、各記述の意味を日本語コメントで説明しています。
 *
 * @param board 盤面の2次元リスト
 * @param showStoneColors プレイヤーごとに石の色を分けて表示するかどうか
 * @param onCellClick セルをタップしたときに呼ばれるコールバック（引数は行番号・列番号）
 * @param enabled 盤面の操作を有効にするかどうか（勝敗決定時はfalseにする）
 */
@Composable
fun GomokuBoard(
    board: Board,
    showStoneColors: Boolean,
    onCellClick: (Int, Int) -> Unit,
    enabled: Boolean
) {
    val boardSize = board.size
    val padding = 16.dp
    val boardPx = 320.dp // 盤面の見た目サイズ（可変にしてもOK）

    // 星点（黒い丸）を描画する交点のリスト
    val starPoints: List<Pair<Int, Int>> = when (boardSize) {
        9 -> listOf(
            2 to 2, 2 to 6, 6 to 2, 6 to 6, 4 to 4 // 四隅＋天元
        )
        13 -> listOf(
            3 to 3, 3 to 9, 9 to 3, 9 to 9, 6 to 6 // 四隅＋天元
        )
        19 -> listOf(
            3 to 3, 3 to 9, 3 to 15,
            9 to 3, 9 to 9, 9 to 15,
            15 to 3, 15 to 9, 15 to 15,
            9 to 9 // 天元（中央）
        )
        else -> emptyList()
    }

    // Canvasサイズに合わせて座標変換するため、Layout座標を参照
    Box(
        modifier = Modifier
            .size(boardPx)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
            .pointerInput(enabled, boardSize) {
                if (enabled) {
                    detectTapGestures { offset ->
                        // Canvasサイズを取得してから計算
                        // ここでは一旦、後述するCanvasのサイズ計算と同じロジックで計算する必要あり
                        // なので、Canvasのサイズと同じboardPxを使う
                        val canvasSizePx = boardPx.toPx()
                        val gridStart = padding.toPx()
                        val gridStep = (canvasSizePx - padding.toPx() * 2) / (boardSize - 1)
                        val row = ((offset.y - gridStart + gridStep / 2) / gridStep).toInt()
                            .coerceIn(0, boardSize - 1)
                        val col = ((offset.x - gridStart + gridStep / 2) / gridStep).toInt()
                            .coerceIn(0, boardSize - 1)
                        onCellClick(row, col)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridStart = padding.toPx()
            val gridEnd = size.width - padding.toPx()
            val gridStep = (size.width - padding.toPx() * 2) / (boardSize - 1)

            // 縦線・横線を描画
            for (i in 0 until boardSize) {
                val pos = gridStart + i * gridStep
                // 縦線
                drawLine(
                    color = Color.Black,
                    start = Offset(pos, gridStart),
                    end = Offset(pos, gridEnd),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
                // 横線
                drawLine(
                    color = Color.Black,
                    start = Offset(gridStart, pos),
                    end = Offset(gridEnd, pos),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }

            // 星（黒い丸）を描画
            val starRadius = gridStep * 0.13f // 星点の大きさは盤面サイズに比例
            for ((row, col) in starPoints) {
                val center = Offset(
                    x = gridStart + col * gridStep,
                    y = gridStart + row * gridStep
                )
                drawCircle(
                    color = Color.Black,
                    radius = starRadius,
                    center = center
                )
            }

            // 石を交点に描画
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    val cell = board[row][col]
                    cell.owner?.let { player ->
                        val center = Offset(
                            x = gridStart + col * gridStep,
                            y = gridStart + row * gridStep
                        )
                        val stoneColor = when {
                            !showStoneColors -> Color.White
                            player == Player.PLAYER1 -> Color.Black
                            player == Player.PLAYER2 -> Color.White
                            else -> Color.Transparent
                        }
                        // プレイヤーマークの輪郭
                        drawCircle(
                            color = Color.Black,
                            radius = gridStep * 0.4f,
                            center = center,
                            style = Stroke(width = 4f)
                        )
                        drawCircle(
                            color = stoneColor,
                            radius = gridStep * 0.4f,
                            center = center
                        )
                    }
                }
            }
        }
    }
}
