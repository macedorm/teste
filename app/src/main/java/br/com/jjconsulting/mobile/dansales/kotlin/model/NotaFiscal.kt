package br.com.jjconsulting.mobile.dansales.kotlin.model

import br.com.jjconsulting.mobile.dansales.model.Cliente
import com.google.gson.annotations.SerializedName

class NotaFiscal {

    @SerializedName("NumNf")
    var notaFiscal:String = ""
    @SerializedName("NumSerieNf")
    var serieNotaFiscal:String = ""
    @SerializedName("CnpjDanone")
    var cnpj:String = ""
    @SerializedName("CodPed")
    var pedido:String = ""
    @SerializedName("CodSiga")
    var sap:String = ""
    @SerializedName("DtEmissao")
    var data:String = ""
    @SerializedName("CodCliente")
    var codCli:String = ""
    @SerializedName("NomeCliente")
    var nomeCli:String = ""
    @SerializedName("Status")
    var status:Int = 0
    @SerializedName("Origem")
    var origem:Int = 0

}