package br.com.jjconsulting.mobile.dansales.kotlin.model

import com.beust.klaxon.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

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

private var list = "0"
private var single = "0"

data class PedidoTracking(
    @Json(name = "s:Envelope")
    val sEnvelope: SEnvelope? = null
) {
    fun toJson() = klaxon.toJsonString(this)

    companion object {

        fun fromJson(json: String) = klaxon.parse<PedidoTracking>(replaced(json))

        private fun replaced(json:String):String{
            var replaced = json.replace("\"b:Ocorrencias\":\"\"", "")

            if(replaced.contains("OcorrenciaDetalhes\":{")){
                replaced = replaced.replace("\"b:OcorrenciaDetalhes", "\"b:OcorrenciaDetalhesSimple")
            }


            if(replaced.contains("SituacaoNotaFiscalNotasCarga\":{")){
                replaced = replaced.replace("\"b:SituacaoNotaFiscalNotasCarga", "\"b:SituacaoNotaFiscalNotasCargaSimple")
            }

            return replaced
        }
    }

    fun getOcorrencias():List<BSituacaoNotaFiscalOcorrencia?>{
        var ocorrencias = sEnvelope?.sBody?.consultaSituacaoNotaFiscalResponse?.
        consultaSituacaoNotaFiscalResult?.aObjeto?.bOcorrencias?.bSituacaoNotaFiscalOcorrencia

        var simpleOcorrencia = sEnvelope?.sBody?.consultaSituacaoNotaFiscalResponse?.
        consultaSituacaoNotaFiscalResult?.aObjeto?.bOcorrencias?.bSituacaoNotaFiscalOcorrenciaSimple

        if(ocorrencias != null){
            return ocorrencias
        } else {
            var listOcorrencias:MutableList<BSituacaoNotaFiscalOcorrencia?> = ArrayList()

            if(simpleOcorrencia != null){
                listOcorrencias.add(simpleOcorrencia)
            }

            return listOcorrencias.toList()
        }
    }
}

data class SEnvelope(
    @Json(name = "xmlns:s")
    val xmlnsS: String? = null,

    @Json(name = "s:Body")
    val sBody: SBody? = null
)

data class SBody(
    @Json(name = "ConsultaSituacaoNotaFiscalResponse")
    val consultaSituacaoNotaFiscalResponse: ConsultaSituacaoNotaFiscalResponse? = null
)

data class ConsultaSituacaoNotaFiscalResponse(
    val xmlns: String? = null,

    @Json(name = "ConsultaSituacaoNotaFiscalResult")
    val consultaSituacaoNotaFiscalResult: ConsultaSituacaoNotaFiscalResult? = null
)

data class ConsultaSituacaoNotaFiscalResult(
    @Json(name = "xmlns:a")
    val xmlnsA: String? = null,

    @Json(name = "a:Objeto")
    val aObjeto: AObjeto? = null,

    @Json(name = "a:CodigoMensagem")
    val aCodigoMensagem: String? = null,

    @Json(name = "a:Status")
    val aStatus: String? = null,

    @Json(name = "a:Mensagem")
    val aMensagem: String? = null,

    @Json(name = "a:DataRetorno")
    val aDataRetorno: String? = null,

    @Json(name = "xmlns:i")
    val xmlnsI: String? = null
)

data class AObjeto(
    @Json(name = "b:MotoristaTelefone")
    val bMotoristaTelefone: String? = null,

    @Json(name = "b:OperadorNome")
    val bOperadorNome: String? = null,

    @Json(name = "b:MotoristaCPF")
    val bMotoristaCPF: String? = null,

    @Json(name = "b:VeiculoTaraKg")
    val bVeiculoTaraKg: String? = null,

    @Json(name = "b:LinkRastreio")
    val bLinkRastreio: String? = null,

    @Json(name = "b:OperadorLogin")
    val bOperadorLogin: String? = null,

    @Json(name = "b:PosicaoLatitude")
    val bPosicaoLatitude: String? = null,

    @Json(name = "b:VeiculoPlaca")
    val bVeiculoPlaca: String? = null,

    @Json(name = "b:CargaProtocoloCarga")
    val bCargaProtocoloCarga: String? = null,

    @Json(name = "b:Ocorrencias")
    val bOcorrencias: BOcorrencias? = null,

    @Json(name = "xmlns:b")
    val xmlnsB: String? = null,

    @Json(name = "b:CargaPeso")
    val bCargaPeso: String? = null,

    @Json(name = "b:MotoristaNome")
    val bMotoristaNome: String? = null,

    @Json(name = "b:CargaDataCriacao")
    val bCargaDataCriacao: String? = null,

    @Json(name = "b:OperadorEmail")
    val bOperadorEmail: String? = null,

    @Json(name = "b:VeiculoCapacidadeKG")
    val bVeiculoCapacidadeKG: String? = null,

    @Json(name = "b:CargaDataInicioViagem")
    val bCargaDataInicioViagem: String? = null,

    @Json(name = "b:PosicaoLongitude")
    val bPosicaoLongitude: String? = null,

    @Json(name = "b:VeiculoTransportadora")
    val bVeiculoTransportadora: String? = null,

    @Json(name = "b:CargaDataCarregamento")
    val bCargaDataCarregamento: String? = null,

    @Json(name = "b:CargaDescricaoOperacao")
    val bCargaDescricaoOperacao: String? = null,

    @Json(name = "b:CargaDescricaoRota")
    val bCargaDescricaoRota: String? = null,


    @Json(name = "b:CargaCodigoCargaEmbarcador")
    val bCargaCodigoCargaEmbarcador: String? = null,

    @Json(name = "b:Destinatario")
    val bDestinatario: BDestinatario? = null,

    @Json(name = "b:Remetente")
    val bRemetente: BRemetente? = null,

    @Json(name = "b:Notas")
    val bNota: BNotas? = null




)

data class BRemetente(
    @Json(name = "c:CodigoIntegracao")
    val cCodigoIntegracao: String? = null
)

data class BDestinatario(
    @Json(name = "c:CPFCNPJ")
    val cCPFCNPJ: String? = null,
    @Json(name = "c:CodigoIntegracao")
    val cCodigoIntegracao: String? = null
)

data class BNotas(
    @Json(name = "b:SituacaoNotaFiscalNotasCarga")
    val bSituacaoNotaFiscalNotasCargaList: List<bSituacaoNotaFiscalNotasCarga>? = null,
    @Json(name = "b:SituacaoNotaFiscalNotasCargaSimple")
    val bSituacaoNotaFiscalNotasCarga: bSituacaoNotaFiscalNotasCarga = bSituacaoNotaFiscalNotasCarga(),
)


data class bSituacaoNotaFiscalNotasCarga(
    @Json(name = "b:CNPJEmitente")
    val bCNPJEmitente: String? = null,
    @Json(name = "b:Numero")
    val numeroNota: String? = null,
    @Json(name = "b:Chave")
    val chave: String? = null
    )

data class BOcorrencias(
    @Json(name = "b:OcorrenciaDetalhes")
    val bSituacaoNotaFiscalOcorrencia: List<BSituacaoNotaFiscalOcorrencia>? = null,

    @Json(name = "b:OcorrenciaDetalhesSimple")
    val bSituacaoNotaFiscalOcorrenciaSimple: BSituacaoNotaFiscalOcorrencia? = null
)


data class BSituacaoNotaFiscalOcorrencia(
    @Json(name = "b:Situacao")
    val bSituacao: String? = null,

    @Json(name = "b:Tipo")
    val bTipo: String? = null,

    @Json(name = "b:Data")
    val bData: String? = null,

    @Json(name = "b:Observacao")
    val bObservacao: String? = null
)
