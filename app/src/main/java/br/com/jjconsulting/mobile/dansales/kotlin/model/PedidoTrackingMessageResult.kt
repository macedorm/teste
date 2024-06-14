package br.com.jjconsulting.mobile.dansales.kotlin.model

import com.beust.klaxon.*

private fun <T> Klaxon.convert(
    k: kotlin.reflect.KClass<*>,
    fromJson: (JsonValue) -> T,
    toJson: (T) -> String,
    isUnion: Boolean = false
) =
    this.converter(object : Converter {
        @Suppress("UNCHECKED_CAST")
        override fun toJson(value: Any) = toJson(value as T)
        override fun fromJson(jv: JsonValue) = fromJson(jv) as Any
        override fun canConvert(cls: Class<*>) =
            cls == k.java || (isUnion && cls.superclass == k.java)
    })

private val klaxon = Klaxon()

data class PedidoTrackingMessageResult(
    @Json(name = "s:Envelope")
    val messageEnvelopeResult: MessageEnvelopeResult? = null
) {
    fun toJson() = klaxon.toJsonString(this)

    companion object {

        fun fromJson(json: String) = klaxon.parse<PedidoTrackingMessageResult>(json);
    }

    data class MessageEnvelopeResult(
        @Json(name = "xmlns:s")
        val xmlnsS: String? = null,

        @Json(name = "s:Body")
        val messageBodyResult: MessageBodyResult? = null
    )

    data class MessageBodyResult(
        @Json(name = "EnviarMensagemChatEntregaResponse")
        val response: MessageResponse? = null
    )

    data class MessageResponse(
        @Json(name = "EnviarMensagemChatEntregaResult")
        val chatResult: MessageResult? = null
    )

    data class MessageResult(

        @Json(name = "a:CodigoMensagem")
        val aCodigoMensagem: String? = null,


        @Json(name = "a:Mensagem")
        val aMensagem: String? = null,
    )
}

