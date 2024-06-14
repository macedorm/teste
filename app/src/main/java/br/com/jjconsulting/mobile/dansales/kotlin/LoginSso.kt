package br.com.jjconsulting.mobile.dansales.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import br.com.jjconsulting.mobile.dansales.BuildConfig
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.SyncDataActivity
import br.com.jjconsulting.mobile.dansales.SystemUpdateActivity
import br.com.jjconsulting.mobile.dansales.base.BaseActivity
import br.com.jjconsulting.mobile.dansales.model.Login
import br.com.jjconsulting.mobile.dansales.util.Config
import br.com.jjconsulting.mobile.dansales.util.UserInfo
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import br.com.jjconsulting.mobile.jjlib.util.JJSDK
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import br.com.jjconsulting.mobile.jjlib.util.SavePref
import br.com.jjconsulting.mobile.jjlib.util.TextUtils
import com.google.gson.Gson


class   LoginSso : BaseActivity() {

    private lateinit var webView: WebView
    private lateinit var pb: RelativeLayout

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Config.REQUEST_SYNC_DATABASE && resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        } else if (requestCode == Config.REQUEST_SYNC_DATABASE) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sso)

        webView = findViewById(R.id.webView)
        pb = findViewById(R.id.rl_progress_bar)

        val savePref = SavePref()


        if( savePref.getBoolPref(Config.TAG_CACHE, Config.TAG, this)){
            clearWebViewCache(this@LoginSso, webView)
            savePref.saveBoolSharedPreferences(Config.TAG_CACHE, Config.TAG, false, this)
        }

        webView.webViewClient = MyWebViewClient()
        webView.reload();
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.javaScriptEnabled = true

        var ssoUrl = BuildConfig.URL_SITE +  "/LoginSSOApp"
        pb.visibility = View.GONE
        webView.visibility = View.VISIBLE
        //https://dansaleswebdev.danone.com.br/dev/LoginSSOApp - Oficial
        //https://dansaleswebdev.danone.com.br/dev/LoginSSOAppTest - OK
        //https://dansaleswebdev.danone.com.br/dev/LoginSSOAppTestErr - Erro
        //https://dansaleswebdev.danone.com.br/dev/LoginWebSSO?email=welton.almeida@danone.com - dados usuarios

                                   webView.loadUrl(ssoUrl);
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            pb.visibility = View.VISIBLE
            webView.visibility = View.GONE
            dialogsDefault.showDialogMessage(getString(R.string.title_connection_error),
                DialogsCustom.DIALOG_TYPE_ERROR
            ) { finish() }

        }


        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if(!url.toString().contains("LoginWebSSO")){
                return
            }

            webView.evaluateJavascript(
                "(function() { return document.documentElement.innerHTML; })();"
            ) { htmlContent ->
                LogUser.log(htmlContent)
                var jsonString = htmlContent
                    .replaceFirst("u003Chead>".toRegex(), "")
                    .replace("</body>".toRegex(), "")
                jsonString = jsonString
                    .replaceFirst("u003Chead>".toRegex(), "")
                    .replaceFirst("u003Cbody>".toRegex(), "")
                    .replace("u003C/body>".toRegex(), "")
                    .replace("u003C/head>".toRegex(), "")

                jsonString = jsonString.replace("\\", "");

                val indexKey: Int = jsonString.indexOf('{')

                if (indexKey != -1) {
                    jsonString = jsonString.substring(indexKey)
                }

                jsonString = jsonString.substring(0, jsonString.length - 1)

                LogUser.log(jsonString)

                val gson = Gson()
                val userJson = gson.fromJson(jsonString, objectUser::class.java)

                openMain(userJson);

            }
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url.toString()

            if (url.contains("danone")) {
                pb.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            return false
        }
    }

    private fun openMain(userJson: objectUser) {

        var userInfo = UserInfo();

        if (userJson.validacao) {
            var login = Login()
            login.token = userJson.token
            login.userId = userJson.userid
            login.version = userJson.version
            login.isEnableMobile = userJson.EnableMobile

            if(!login.isEnableMobile) {
                dialogsDefault.showDialogMessage(getString(R.string.permission_mobile_error),
                    DialogsCustom.DIALOG_TYPE_ERROR
                ) { finish() }
                clearWebViewCache(this@LoginSso, webView)
                return
            }

            userInfo.saveUserInfo(login, this)
            initializeJJSDK()

            var isVersionOld = false
            if (login != null) {
                if (BuildConfig.VERSION_NAME != login.version) {
                    isVersionOld = true
                }
                if (!isVersionOld) {
                    if (userInfo.isFirstLogin(this, login.userId)) {
                        val syncDataIntent = Intent(
                            this,
                            SyncDataActivity::class.java
                        )
                        startActivityForResult(syncDataIntent, Config.REQUEST_SYNC_DATABASE)
                    } else {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                } else {
                    LogUser.log(Config.TAG, "Aplicativo nao esta atualizado")
                    val systemUpdate = Intent(
                        this,
                        SystemUpdateActivity::class.java
                    )
                    finish()
                    startActivityForResult(systemUpdate, Config.REQUEST_UPDATE_APK)
                }
            }
        } else {
            clearWebViewCache(this@LoginSso, webView)
            dialogsDefault.showDialogMessage(TextUtils.firstLetterUpperCase(userJson.msg),
                DialogsCustom.DIALOG_TYPE_ERROR
            ) { finish() }
        }
    }

    private fun initializeJJSDK() {
        val userInfo = UserInfo()
        JJSDK.setToken(this, userInfo.getUserInfo(this).token)
        JJSDK.setCodUser(this, userInfo.getUserInfo(this).userId)
    }

    data class objectUser(
        val validacao: Boolean,
        val EnableMobile: Boolean,
        val version: String,
        val token: String,
        val userid: String,
        val msg: String

    )


    fun clearWebViewCache(context: Context, webView: WebView) {
        // Limpar o cache do WebView
        webView.clearCache(true)

        // Limpar o histórico de navegação
        webView.clearHistory()

        // Limpar todos os dados de cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)

        // Limpar o Armazenamento Web (Local Storage, Session Storage)
        val webStorage = WebStorage.getInstance()
        webStorage.deleteAllData()

        // Limpar o cache da AppCache
        val settings = webView.settings
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

}
