package io.github.ryangryota.gomoku.domain.model

/**
 * 五目並べのプレイヤーを表す列挙型。
 *
 * - PLAYER1: 先手（黒石担当）
 * - PLAYER2: 後手（白石担当）
 *
 * @property displayName プレイヤーをUI上で表示するためのラベル（例: "Player 1 (Black)"）
 */
enum class Player {
    /** 先手（黒石担当） */
    PLAYER1,

    /** 後手（白石担当） */
    PLAYER2;

    /**
     * プレイヤーの表示名を返します。
     * 例: "Player 1 (Black)" または "Player 2 (White)"
     */
    val displayName: String
        get() = when (this) {
            PLAYER1 -> "Player 1 (Black)"
            PLAYER2 -> "Player 2 (White)"
        }
}
