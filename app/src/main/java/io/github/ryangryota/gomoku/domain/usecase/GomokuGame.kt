package io.github.ryangryota.gomoku.domain.usecase

import android.util.Log
import io.github.ryangryota.gomoku.domain.model.Board
import io.github.ryangryota.gomoku.domain.model.BoardCell
import io.github.ryangryota.gomoku.domain.model.Player

/**
 * 五目並べのコアロジックを提供するクラスです。
 * 盤面の初期化、石を置く処理、勝敗判定、引き分け判定などの機能をまとめています。
 * ViewModelやUI層から本クラスを利用することで、ゲーム進行に必要なビジネスロジックを一元管理できます。
 *
 * @property boardSize 盤面のサイズ（例: 9, 13, 19）
 */
class GomokuGame(
    private val boardSize: Int // 盤面のサイズ（例: 9, 13, 19）
) {
    // ログ出力用のタグ
    private val TAG = "GomokuGame"

    /**
     * 空の盤面（全て未配置）を生成します。
     *
     * @return 盤面（2次元リスト）。各セルはownerがnullで初期化されます。
     */
    fun createInitialBoard(): Board {
        Log.d(TAG, "空の盤面を生成します（サイズ: $boardSize）")
        return List(boardSize) { row ->
            List(boardSize) { col -> BoardCell(row, col) }
        }
    }

    /**
     * 指定したセルにプレイヤーの石を置きます。
     * 既に石が置かれているセルには上書きしません。
     *
     * @param board 現在の盤面
     * @param row 行番号（0始まり）
     * @param col 列番号（0始まり）
     * @param player 石を置くプレイヤー
     * @return 石を置いた後の新しい盤面（イミュータブルな2次元リスト）
     */
    fun placeStone(
        board: Board,
        row: Int,
        col: Int,
        player: Player
    ): Board {
        // 既に石がある場合は操作を無視
        if (board[row][col].owner != null) {
            Log.d(TAG, "すでに石が置かれています。操作を無視します。")
            return board // 変更前の盤面を返す
        }

        // 石を置く
        Log.d(TAG, "石を置く: row=$row, col=$col, player=${player.name}")
        return board.map { it.toMutableList() }.also { newBoard ->
            newBoard[row][col] = newBoard[row][col].copy(owner = player)
        }
    }

    /**
     * 勝敗判定を行います。
     * 指定したセルを最後に置いたプレイヤーが、縦・横・斜めいずれかの方向で5つ連続して石を並べているかを判定します。
     *
     * @param board 現在の盤面
     * @param lastRow 最後に石を置いた行番号
     * @param lastCol 最後に石を置いた列番号
     * @param player 判定対象のプレイヤー
     * @return 5つ並んでいればtrue（勝利）、それ以外はfalse
     */
    fun checkWinner(
        board: Board,
        lastRow: Int,
        lastCol: Int,
        player: Player
    ): Boolean {
        Log.d(TAG, "勝敗判定: row=$lastRow, col=$lastCol, player=${player.name}")
        // 縦・横・斜めの4方向を定義
        val directions = listOf(
            Pair(1, 0),  // 縦
            Pair(0, 1),  // 横
            Pair(1, 1),  // 斜め（右下）
            Pair(1, -1)  // 斜め（左下）
        )
        for ((dx, dy) in directions) {
            var count = 1 // 連続している石の数
            // 前方向にカウント
            var x = lastRow + dx
            var y = lastCol + dy
            while (x in board.indices && y in board.indices && board[x][y].owner == player) {
                count++
                x += dx
                y += dy
            }
            // 後方向にカウント
            x = lastRow - dx
            y = lastCol - dy
            while (x in board.indices && y in board.indices && board[x][y].owner == player) {
                count++
                x -= dx
                y -= dy
            }
            if (count >= 5) {
                Log.d(TAG, "勝利条件達成: $count 連続")
                return true
            }
        }
        return false
    }

    /**
     * 引き分け判定を行います。
     * 盤面がすべて埋まっていて勝者がいない場合にtrueを返します。
     *
     * @param board 現在の盤面
     * @return 引き分けならtrue、そうでなければfalse
     */
    fun checkDraw(board: Board): Boolean {
        val isDraw = board.all { row -> row.all { it.owner != null } }
        if (isDraw) {
            Log.d(TAG, "引き分け判定: 盤面がすべて埋まりました")
        }
        return isDraw
    }
}
