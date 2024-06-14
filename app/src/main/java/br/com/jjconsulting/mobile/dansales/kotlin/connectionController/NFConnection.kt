package br.com.jjconsulting.mobile.dansales.kotlin.connectionController

import android.content.Context
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection
import br.com.jjconsulting.mobile.dansales.model.Login
import br.com.jjconsulting.mobile.jjlib.Connection
import br.com.jjconsulting.mobile.jjlib.util.JJSDK
import java.util.*

class NFConnection(val mContext: Context, mListener: ConnectionListener) : BaseConnection() {
    private var endpoint: String = ""

    init {
        listener = mListener
    }

    fun getNF(code:String) {
        createConnection(mContext)
        val headerKeys = ArrayList<String>()
        val headerValues = ArrayList<String>()
        val bodyKeys = ArrayList<String>()
        val bodyValues = ArrayList<String>()

        val login = userInfo.getUserInfo(mContext)
        if (login != null) {
            headerKeys.add("token")
            headerValues.add(login.token)
        }

        endpoint = JJSDK.getHost(mContext) + Connection.API_ORCAMENTO
        endpoint += "/$code/nf"

        connection.GET(
            endpoint,
            bodyValues,
            bodyKeys,
            headerValues,
            headerKeys,
            Connection.INITIALTIMEOUTMED
        )
    }

    fun getNFDetails(nf:String, serial:String) {
        createConnection(mContext)
        val headerKeys = ArrayList<String>()
        val headerValues = ArrayList<String>()
        val bodyKeys = ArrayList<String>()
        val bodyValues = ArrayList<String>()

        val login = userInfo.getUserInfo(mContext)
        if (login != null) {
            headerKeys.add("token")
            headerValues.add(login.token)
        }

        bodyKeys.add("num_nf")
        bodyValues.add(nf)

        bodyKeys.add("num_serie_nf")
        bodyValues.add(serial)

        endpoint = JJSDK.getHost(mContext) + Connection.API_MASTER
        endpoint += "NotaFiscal/"

        connection.GET(
            endpoint,
            bodyValues,
            bodyKeys,
            headerValues,
            headerKeys,
            Connection.INITIALTIMEOUTMED
        )
    }
}