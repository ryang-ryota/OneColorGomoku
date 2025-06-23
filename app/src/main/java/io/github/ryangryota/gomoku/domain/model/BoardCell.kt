package io.github.ryangryota.gomoku.domain.model

/**
 * 五目並べの盤面上の各マス（セル）
 *
 * @property row 行番号（0始まり）
 * @property col 列番号（0始まり）
 * @property owner このセルに石を置いたプレイヤー。nullなら未配置、PLAYER1またはPLAYER2ならそのプレイヤーが配置済み。
 */
data class BoardCell
    (
    val row: Int,
    val col: Int,
    val owner: Player? = null // null=未配置、PLAYER1/PLAYER2=配置済み
)
