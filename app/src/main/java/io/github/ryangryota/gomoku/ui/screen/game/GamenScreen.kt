package io.github.ryangryota.gomoku.ui.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.ryangryota.gomoku.domain.model.GameState

// 定数定義
private const val BOARD_PADDING = 16
private const val VERTICAL_SPACER_HEIGHT = 24
private const val BUTTON_SPACING = 16
private const val UNDO_BUTTON_DESCRIPTION = "Undo"
private const val TOGGLE_COLOR_DESCRIPTION = "Toggle Stone Color"
private const val BACK_BUTTON_DESCRIPTION = "Back"
private const val RESET_BUTTON_DESCRIPTION = "Reset"

/**
 * GameScreenのエントリポイントComposable
 *
 * Navigationから渡されたパラメータを受け取り、ViewModelを初期化します。
 *
 * @param onBack タイトル画面に戻るコールバック
 */
@Composable
fun GameScreen(
    onBack: () -> Unit
) {
    val viewModel: GameViewModel = hiltViewModel()
    val gameState by viewModel.gameState.collectAsState()

    GameScreenContent(
        gameState = gameState,
        onCellClick = viewModel::onCellClicked,
        onUndo = viewModel::onUndo,
        onReset = viewModel::onReset,
        onBack = onBack,
        onToggleShowStoneColors = viewModel::onToggleShowStoneColors
    )
}

/**
 * ゲーム画面のメインコンテンツ
 *
 * 盤面表示、操作ボタン、状態表示を管理します。
 *
 * @param gameState 現在のゲーム状態
 * @param onCellClick 盤面セルクリック時のコールバック
 * @param onUndo アンドゥ操作時のコールバック
 * @param onReset リセット操作時のコールバック
 * @param onBack 戻る操作時のコールバック
 * @param onToggleShowStoneColors プレイヤーマーク表示切替コールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenContent(
    gameState: GameState,
    onCellClick: (row: Int, col: Int) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onToggleShowStoneColors: () -> Unit
) {
    Scaffold(
        topBar = { GameTopAppBar(onBack, onReset) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(BOARD_PADDING.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 盤面表示
            GomokuBoard(
                board = gameState.board,
                showStoneColors = gameState.showStoneColors,
                onCellClick = onCellClick,
                enabled = gameState.winner == null && !gameState.isDraw
            )

            Spacer(Modifier.height(VERTICAL_SPACER_HEIGHT.dp))

            // ゲーム状態表示
            GameStatusText(gameState)

            Spacer(Modifier.height(BOARD_PADDING.dp))

            // 操作ボタン群
            GameActionButtons(
                gameState = gameState,
                onUndo = onUndo,
                onToggleShowStoneColors = onToggleShowStoneColors
            )
        }
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    // 勝敗がついたら自動的にダイアログを表示
    LaunchedEffect(gameState.winner, gameState.isDraw) {
        if (gameState.winner != null || gameState.isDraw) {
            showDialog = true
        }
    }

    // 勝敗ダイアログ表示
    if (showDialog) {
        WinnerDialog(
            winner = gameState.winner,
            isDraw = gameState.isDraw,
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * ゲーム画面のトップアプリケーションバー
 *
 * @param onBack 戻るボタンクリックコールバック
 * @param onReset リセットボタンクリックコールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopAppBar(
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = BACK_BUTTON_DESCRIPTION
                )
            }
        },
        title = { Text("OneColorGomoku") },
        actions = {
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = RESET_BUTTON_DESCRIPTION
                )
            }
        }
    )
}

/**
 * ゲーム状態を表示するテキスト
 *
 * @param gameState 現在のゲーム状態
 */
@Composable
private fun GameStatusText(gameState: GameState) {
    val statusText = when {
        gameState.winner != null -> "Winner: ${gameState.winner.displayName}"
        gameState.isDraw -> "Draw"
        else -> "Turn: ${gameState.currentPlayer.displayName}"
    }

    Text(
        text = statusText,
        style = MaterialTheme.typography.titleMedium
    )
}

/**
 * ゲーム操作ボタングループ
 *
 * @param gameState 現在のゲーム状態
 * @param onUndo アンドゥ操作コールバック
 * @param onToggleShowStoneColors プレイヤーマーク表示切替コールバック
 */
@Composable
private fun GameActionButtons(
    gameState: GameState,
    onUndo: () -> Unit,
    onToggleShowStoneColors: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BUTTON_SPACING.dp)) {
        // アンドゥボタン
        Button(
            onClick = onUndo,
            enabled = gameState.history.isNotEmpty()
        ) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = UNDO_BUTTON_DESCRIPTION)
            Spacer(Modifier.width(8.dp))
            Text("Undo")
        }

        // プレイヤーマーク表示切替ボタン
        OutlinedButton(onClick = onToggleShowStoneColors) {
            Icon(
                imageVector = if (gameState.showStoneColors) Icons.Default.Visibility
                else Icons.Default.VisibilityOff,
                contentDescription = TOGGLE_COLOR_DESCRIPTION
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (gameState.showStoneColors) "Show Colors: ON"
                else "Show Colors: OFF"
            )
        }
    }
}
