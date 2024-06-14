package br.com.jjconsulting.mobile.jjlib.connection.soap.kotlin


import br.com.jjconsulting.mobile.jjlib.util.LogUser
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject

class SoapService {

    lateinit var onConnection:OnConnection

    fun callService(bodyXML:String, soapURL: String,
                    soapAction: String, currentOnConnection:OnConnection) {

        onConnection = currentOnConnection

        var status = 0

        var connection: HttpURLConnection? = null
        try {
            val url = URL(soapURL)
            connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty(
                "Content-Length",
                bodyXML.toByteArray().size.toString() + ""
            )

            connection.setRequestProperty(
                "SOAPAction",
                soapAction
            )

            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8")

            connection.requestMethod = "POST"
            connection.doInput = true
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(bodyXML.toByteArray(charset("UTF-8")))
            outputStream.close()
            connection.connect()
            status = connection.responseCode

        } catch (e: ProtocolException) {
            LogUser.log("HTTP status code : $e.printStackTrace()")
        } catch (e: MalformedURLException) {
            LogUser.log("HTTP status code : $e.printStackTrace()")
        } catch (e: IOException) {
            LogUser.log("HTTP status code : $e.printStackTrace()")
        } finally {
            LogUser.log("HTTP status code : $status")
        }

        var inputStream: InputStream?

        val content = StringBuilder()
        lateinit var xmlToJson:String;

        try {
            inputStream = connection?.inputStream

            val reader = BufferedReader(inputStream?.reader())
            reader.use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    content.append(line)
                    line = reader.readLine()
                }
            }


            xmlToJson = XmlToJson.Builder(content.toString()).build().toString()

            if(status == 200){
                onConnection.onSuccess(status, xmlToJson)
            } else {
                onConnection.onError(status, xmlToJson)
            }

        } catch (e: IOException) {
            e.printStackTrace()
            onConnection.onError(status, "")
        }
    }

    interface OnConnection {
        fun onSuccess(status:Int,response:String){}
        fun onError(status:Int,response:String)
    }
}