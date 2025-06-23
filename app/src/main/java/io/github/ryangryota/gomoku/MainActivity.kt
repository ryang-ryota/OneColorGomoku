package io.github.ryangryota.gomoku

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.github.ryangryota.gomoku.ui.screen.game.GameScreen
import io.github.ryangryota.gomoku.ui.screen.title.TitleScreen
import io.github.ryangryota.gomoku.ui.theme.OneColorGomokuTheme

/**
 * MainActivityはアプリのエントリポイント（最初に起動する画面）です。
 * @AndroidEntryPoint アノテーションは、Hilt（依存性注入ライブラリ）を利用するために必要です。
 * Hiltを使うことで、ViewModelやRepositoryなどのインスタンス管理が自動化されます。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ログ出力用のタグ
    private val TAG = "MainActivity"

    /**
     * onCreateはAndroidアプリのActivityが生成されるときに最初に呼ばれるメソッドです。
     * savedInstanceStateは画面回転などでActivityが再生成されたときに前回の状態を保持するためのものです。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: MainActivityが起動しました")

        // Jetpack ComposeのUIをセットする
        setContent {
            // アプリのテーマを適用
            OneColorGomokuTheme {
                // 画面遷移を管理するNavControllerを作成
                val navController = rememberNavController()

                // NavHostで画面遷移のルールを定義
                NavHost(
                    navController = navController,
                    startDestination = "title" // 最初に表示する画面
                ) {
                    // タイトル画面の定義
                    composable("title") {
                        Log.d(TAG, "タイトル画面を表示")
                        TitleScreen(
                            onStartGame = { boardSize, showPlayerMark ->
                                // ゲーム画面へ遷移し、選択した値を引数として渡す
                                Log.d(TAG, "Start Game: boardSize=$boardSize, showPlayerMark=$showPlayerMark")
                                navController.navigate("game/$boardSize/$showPlayerMark")
                            }
                        )
                    }
                    // ゲーム画面の定義。引数として盤面サイズとマーク表示フラグを受け取る
                    composable(
                        route = "game/{boardSize}/{showPlayerMark}",
                        arguments = listOf(
                            navArgument("boardSize") { type = NavType.IntType },
                            navArgument("showPlayerMark") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        // NavBackStackEntryから引数を取得
                        val boardSize = backStackEntry.arguments?.getInt("boardSize") ?: 13
                        val showPlayerMark = backStackEntry.arguments?.getBoolean("showPlayerMark") ?: false
                        Log.d(TAG, "ゲーム画面を表示: boardSize=$boardSize, showPlayerMark=$showPlayerMark")
                        GameScreen {
                            Log.d(TAG, "タイトル画面へ戻る")
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
