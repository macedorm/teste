package br.com.jjconsulting.mobile.dansales.kotlin.model

enum class TStatusRD (val valor: Int) {
    ALTERAR_DADOS(160),
    ALTERAR_NOMES(161),
    ALERTAR_EMAIL(162),
    ALTERAR_NOMES_ALERT_EMAIL(163),
    BLOQUEIO_EMAIL(164),
    BLOQUEIO_NOME( 165),
    ALTERAR_NOMES_EMAIL_EXPIRADO(166),
    AUTENTICAR_EMAIL(167),
    BLOQUEIO_AUT_EMAIL( 168),
    ALTERAR_EMAIL_EXPIRADO(169),
    AGUARDE_CONFIRMACAO( 170);


    companion object {
        fun getStatusName(value: Int): TStatusRD {
            return when (value) {
                160 -> ALTERAR_DADOS
                161 -> ALTERAR_NOMES
                162 -> ALERTAR_EMAIL
                163 -> ALTERAR_NOMES_ALERT_EMAIL
                164 -> BLOQUEIO_EMAIL
                165 -> BLOQUEIO_NOME
                166 -> ALTERAR_NOMES_EMAIL_EXPIRADO
                167 -> AUTENTICAR_EMAIL
                168 -> BLOQUEIO_AUT_EMAIL
                169 -> ALTERAR_EMAIL_EXPIRADO
                170 -> AGUARDE_CONFIRMACAO
                else -> ALTERAR_DADOS
            }
        }

        fun getStatusID(value: TStatusRD): Int {
            return when (value) {
                ALTERAR_DADOS ->  160
                ALTERAR_NOMES -> 161
                ALERTAR_EMAIL -> 162
                ALTERAR_NOMES_ALERT_EMAIL -> 163
                BLOQUEIO_EMAIL -> 164
                BLOQUEIO_NOME -> 165
                ALTERAR_NOMES_EMAIL_EXPIRADO -> 166
                AUTENTICAR_EMAIL -> 167
                BLOQUEIO_AUT_EMAIL -> 168
                ALTERAR_EMAIL_EXPIRADO -> 169
                AGUARDE_CONFIRMACAO -> 170
            }
        }

        fun isCheckExists(value: Int): Boolean {
            return when (value) {
                160,161,162,163,164,165,166,167,168,169, 170 ->  true
                else -> false
            }
        }
    }
}