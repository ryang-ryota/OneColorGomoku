package io.github.ryangryota.gomoku.ui.screen.game

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.ryangryota.gomoku.domain.model.Player

/**
 * 勝敗ダイアログを表示するComposable関数です。
 * 勝者が決まった場合や引き分けの場合に呼び出してください。
 *
 * @param winner 勝者（Player型、nullの場合は引き分け）
 * @param isDraw 引き分けかどうか
 * @param onDismiss ダイアログを閉じる処理（OKボタン押下時など）
 */
@Composable
fun WinnerDialog(
    winner: Player?,
    isDraw: Boolean,
    onDismiss: () -> Unit
) {
    // 勝者がいる場合または引き分けの場合のみダイアログを表示
    if (winner != null || isDraw) {
        // ログ出力（AndroidのLogクラスを利用）
        if (winner != null) {
            Log.d("WinnerDialog", "勝者: ${winner.displayName}")
        } else if (isDraw) {
            Log.d("WinnerDialog", "引き分け")
        }

        // ComposeのAlertDialogを利用してダイアログを表示
        AlertDialog(
            onDismissRequest = onDismiss, // ダイアログ外をタップしたときの動作
            title = {
                Text(
                    when {
                        winner != null -> "Winner: ${winner.displayName}" // 勝者名を表示
                        isDraw -> "Draw" // 引き分け表示
                        else -> ""
                    }
                )
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
