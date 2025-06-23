package io.github.ryangryota.gomoku.ui.screen.game

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ryangryota.gomoku.domain.model.GameState
import io.github.ryangryota.gomoku.domain.model.Player
import io.github.ryangryota.gomoku.domain.usecase.GomokuGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * GameViewModelは五目並べの対戦画面における状態管理・ロジックを担当します。
 * 盤面の状態、手番、勝敗、アンドゥ履歴、石の表示切替などを保持し、UI層と連携します。
 *
 * @HiltViewModel アノテーションは、Hiltによる依存性注入を有効化します。
 * SavedStateHandleはNavigation経由の引数や画面回転時の状態保持に利用されます。
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ログ出力用のタグ
    private val TAG = "GameViewModel"

    // 盤面サイズ（Navigationから受け取る。デフォルトは13）
    private val boardSize: Int = savedStateHandle.get<Int>("boardSize") ?: 13

    // 「どちらが置いたか表示」初期値（Navigationから受け取る。デフォルトはfalse）
    private val initialShowStoneColors: Boolean = savedStateHandle.get<Boolean>("showStoneColors") ?: false

    // 五目並べロジック本体（盤面サイズを指定して生成）
    private val gomokuGame = GomokuGame(boardSize)

    // ゲーム状態を保持するStateFlow（UIはこれを監視する）
    private val _gameState = MutableStateFlow(
        GameState(
            board = gomokuGame.createInitialBoard(),
            showStoneColors = initialShowStoneColors
        )
    )
    val gameState = _gameState.asStateFlow()

    /**
     * 盤面のセルがタップされたときに呼ばれる関数です。
     * 勝敗決定後や引き分け後は何もしません。
     * 石を置き、勝敗・引き分けを判定し、状態を更新します。
     */
    fun onCellClicked(row: Int, col: Int) {
        val state = _gameState.value
        if (state.winner != null || state.isDraw) {
            Log.d(TAG, "すでに勝敗が決まっているため操作を無視します")
            return
        }

        // 石を置く
        val newBoard = gomokuGame.placeStone(state.board, row, col, state.currentPlayer)

        // 盤面が変わっていない場合（既に石があった）は処理を中断
        if (newBoard == state.board) {
            Log.d(TAG, "石が置かれなかったため操作を中断します")
            return
        }

        // 勝敗判定
        val isWin = gomokuGame.checkWinner(newBoard, row, col, state.currentPlayer)
        // 引き分け判定
        val isDraw = !isWin && gomokuGame.checkDraw(newBoard)

        // 状態を更新
        _gameState.update {
            it.copy(
                board = newBoard,
                currentPlayer = if (!isWin && !isDraw) it.currentPlayer.opposite() else it.currentPlayer,
                winner = if (isWin) it.currentPlayer else null,
                isDraw = isDraw,
                history = it.history + listOf(it.board)
            )
        }

        Log.d(TAG, "セルがクリックされました: row=$row, col=$col, player=${state.currentPlayer.name}")
        if (isWin) {
            Log.d(TAG, "勝者が決まりました: player=${state.currentPlayer.name}")
        } else if (isDraw) {
            Log.d(TAG, "引き分けになりました")
        }
    }

    /**
     * アンドゥ（1手戻す）処理。
     * 履歴があれば1つ前の盤面に戻します。
     */
    fun onUndo() {
        _gameState.update { state ->
            if (state.history.isNotEmpty()) {
                Log.d(TAG, "アンドゥ実行: 1手前に戻します")
                state.copy(
                    board = state.history.last(),
                    currentPlayer = state.currentPlayer.opposite(),
                    winner = null,
                    isDraw = false,
                    history = state.history.dropLast(1)
                )
            } else {
                Log.d(TAG, "アンドゥ不可: 履歴がありません")
                state
            }
        }
    }

    /**
     * ゲームをリセット（初期状態に戻す）処理。
     */
    fun onReset() {
        Log.d(TAG, "ゲームをリセットします")
        _gameState.value = GameState(
            board = gomokuGame.createInitialBoard(),
            showStoneColors = initialShowStoneColors
        )
    }

    /**
     * 石の色分け表示（どちらが置いたか表示）を切り替える処理。
     */
    fun onToggleShowStoneColors() {
        _gameState.update {
            Log.d(TAG, "石の色分け表示を切り替えます: 現在=${it.showStoneColors}")
            it.copy(showStoneColors = !it.showStoneColors)
        }
    }

    /**
     * プレイヤーを交代する拡張関数。
     */
    private fun Player.opposite(): Player =
        if (this == Player.PLAYER1) Player.PLAYER2 else Player.PLAYER1
}
