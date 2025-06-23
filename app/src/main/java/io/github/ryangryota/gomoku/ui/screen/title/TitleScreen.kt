package io.github.ryangryota.gomoku.ui.screen.title

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * タイトル画面（TitleScreen）は、ゲーム開始前の設定やアプリの第一印象を担う画面です。
 * より魅力的に見せるため、タイポグラフィ・配色・カードUI・余白を工夫しています。
 * topBar（タイトルバー）は非表示にしています。
 *
 * @param onStartGame ゲーム開始時に呼ばれるコールバック。盤面サイズとマーク表示フラグを渡します。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleScreen(
    onStartGame: (boardSize: Int, showStoneColors: Boolean) -> Unit
) {
    // ログ出力用のタグ
    val TAG = "TitleScreen"

    // 盤面サイズ選択の状態（デフォルトは13路）
    var boardSize by remember { mutableStateOf(13) }
    // 「どちらが置いたか表示」オプションの状態（デフォルトはOFF）
    var showStoneColors by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OneColorGomoku") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background // ここで背景色指定
    ) { padding ->
        // Columnで縦並びレイアウト。中央寄せ＆余白をしっかり確保
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // アプリ名を大きく表示（タイポグラフィで印象アップ）
            Text(
                "OneColorGomoku",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            // サブタイトルまたはキャッチコピー
            Text(
                "Minimalist Gomoku for Everyone",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            // 設定項目カード
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 盤面サイズ選択ラベル
                    Text(
                        "Board Size",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    // 盤面サイズ選択ボタン（9, 13, 19）
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        listOf(
                            9,
                            13/*, 19(TODO: 19路盤はズーム機能に工数がかかるので保留)*/
                        ).forEach { size ->
                            val isSelected = boardSize == size
                            val targetColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                            val animatedColor by animateColorAsState(targetValue = targetColor)
                            OutlinedButton(
                                onClick = {
                                    boardSize = size
                                    Log.d(TAG, "盤面サイズを選択: $size")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = animatedColor
                                )
                            ) {
                                Text("$size")
                            }
                        }
                    }
                    // 「どちらが置いたか表示」チェックボックス
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clickable {
                                showStoneColors = !showStoneColors
                                Log.d(TAG, "Show Stone Colors: $showStoneColors")
                            }
                    ) {
                        Checkbox(
                            checked = showStoneColors,
                            onCheckedChange = null
                        )
                        Text(
                            text = "Show Stone Colors",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }

                }
            }
            Spacer(Modifier.height(32.dp))
            // ゲーム開始ボタン。大きめ＆角丸でタッチしやすく
            Button(
                onClick = {
                    Log.d(
                        TAG,
                        "Start Gameボタンが押されました: boardSize=$boardSize, showStoneColors=$showStoneColors"
                    )
                    onStartGame(boardSize, showStoneColors)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Start Game", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
