package io.github.ryangryota.gomoku.domain.model

/**
 * 五目並べの対戦画面の状態を管理するデータクラス。
 * ゲーム進行に必要な情報をまとめて保持します。
 *
 * @property board 現在の盤面を表す2次元リスト。各セルには石の所有者情報が格納されます。
 * @property currentPlayer 現在の手番のプレイヤー。初期値はPLAYER1（先手）。
 * @property winner 勝者を表すプレイヤー。まだ決まっていない場合はnull。
 * @property isDraw 引き分けの場合はtrue、それ以外はfalse。
 * @property history 盤面の履歴（アンドゥ機能用）。過去の盤面状態をリストで保持します。
 * @property showStoneColors 石の色分け表示フラグ。trueの場合はプレイヤーごとに石の色を分けて表示します。
 * @property foulAlert 禁じ手（例：三々、四々、長連など）が発生した際のアラートメッセージ。通常はnull。
 */
data class GameState(
    val board: Board,
    val currentPlayer: Player = Player.PLAYER1,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val history: List<Board> = emptyList(),
    val showStoneColors: Boolean = false,
    val foulAlert: String? = null
)
