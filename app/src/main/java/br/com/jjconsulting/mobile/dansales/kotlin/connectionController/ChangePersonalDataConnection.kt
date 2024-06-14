package br.com.jjconsulting.mobile.dansales.kotlin.connectionController

import android.content.Context
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection
import br.com.jjconsulting.mobile.jjlib.Connection
import br.com.jjconsulting.mobile.jjlib.util.JJSDK
import java.util.*

class ChangePersonalDataConnection(val mContext: Context, mListener: ConnectionListener, tokenCurrrent:String, userCurrent:String) : BaseConnection() {
    private var endpoint: String = ""
    private var token: String = ""
    private var user: String = ""
    init {
        listener = mListener
        token = tokenCurrrent
        user = userCurrent

    }

    fun setChangeDataUser(name:String, shortName:String, email:String, isAlready:Boolean) {
        createConnection(mContext)
        val headerKeys = ArrayList<String>()
        val headerValues = ArrayList<String>()
        val bodyKeys = ArrayList<String>()
        val bodyValues = ArrayList<String>()


        bodyKeys.add("user")
        bodyValues.add(user)

        headerKeys.add("token")
        headerValues.add(token)


        if(shortName.isNotEmpty()){
            bodyKeys.add( "name");
            bodyValues.add(name)
        }

        if(shortName.isNotEmpty()){
            bodyKeys.add("shortName");
            bodyValues.add(shortName)
        }

        if(email.isNotEmpty()){
            bodyKeys.add("email")
            bodyValues.add(email)
        }

        bodyKeys.add("isUpdate");
        bodyValues.add(isAlready.toString())

         endpoint = JJSDK.getHost(mContext) + Connection.API_CHANGE_DATA_USER

        connection.POST(
            endpoint,
            bodyValues,
            bodyKeys,
            headerValues,
            headerKeys,
            Connection.INITIALTIMEOUTMED
        )
    }

}