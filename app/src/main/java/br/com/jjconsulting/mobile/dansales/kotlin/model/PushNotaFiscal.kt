package br.com.jjconsulting.mobile.dansales.kotlin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class PushNotaFiscal: Serializable {
    @Transient
    private var map: Map<String, String>? = null

    fun setPush(dataMap: Map<String, String>?) {
        map = dataMap
    }

    fun getPush(): Map<String, String>? {
        return map
    }

    /*

    @SerializedName("NumNF")
    var notaFiscal:String = ""
    @SerializedName("Serie")
    var serieNotaFiscal:String = ""
    @SerializedName("CnpjEmitente")
    var cnpj:String = ""
    @SerializedName("NomeUsuario")
    var name:String = ""
    @SerializedName("Mensagem")
    var message:String = ""
    @SerializedName("DataMensagem")
    var date:Date = Date()
    @SerializedName("TipoUsuario")
    var type:Int = 0
*/
}