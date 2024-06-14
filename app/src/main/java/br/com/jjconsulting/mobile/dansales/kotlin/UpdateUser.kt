package br.com.jjconsulting.mobile.dansales.kotlin


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.base.BaseActivity
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection
import br.com.jjconsulting.mobile.dansales.kotlin.connectionController.ChangePersonalDataConnection
import br.com.jjconsulting.mobile.dansales.kotlin.model.TStatusRD
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import com.android.volley.VolleyError
import org.json.JSONObject
import java.io.InputStreamReader


class UpdateUser : BaseActivity() {

    private lateinit var connection: ChangePersonalDataConnection
    private lateinit var mButtonSend: Button
    private lateinit var mEditTextUser: EditText
    private lateinit var mEditTextName: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mLinearLayout: LinearLayout
    private lateinit var mRelativeLayout: RelativeLayout
    private var value: Int = 0
    private var token: String? = null
    private var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        value = intent.getIntExtra("key", 160)
        token = intent.getStringExtra("token")
        user = intent.getStringExtra("user")

        mButtonSend = findViewById(R.id.login_button)
        mEditTextUser = findViewById(R.id.user_name_edit_text)
        mEditTextName = findViewById(R.id.name_edit_text)
        mEditTextEmail = findViewById(R.id.email_edit_text)
        mLinearLayout = findViewById(R.id.loading_linear_layout)
        mRelativeLayout = findViewById(R.id.base_relative_layout)

        when (TStatusRD.getStatusName(value)) {
            TStatusRD.ALTERAR_DADOS, TStatusRD.ALTERAR_NOMES_EMAIL_EXPIRADO -> fullFields()
            TStatusRD.ALTERAR_NOMES, TStatusRD.ALTERAR_NOMES_ALERT_EMAIL, TStatusRD.BLOQUEIO_NOME -> blockedMail()
            TStatusRD.BLOQUEIO_EMAIL, TStatusRD.AUTENTICAR_EMAIL,
            TStatusRD.BLOQUEIO_AUT_EMAIL, TStatusRD.ALTERAR_EMAIL_EXPIRADO -> onlyMail()
            TStatusRD.AGUARDE_CONFIRMACAO, TStatusRD.ALERTAR_EMAIL -> TODO()
        }

        mButtonSend.setOnClickListener {
            sendInfo()
        }
    }

    private fun showProgress(value: Boolean) {
        if (value) {
            mLinearLayout.visibility = View.VISIBLE
            mRelativeLayout.visibility = View.GONE
        } else {
            mLinearLayout.visibility = View.GONE
            mRelativeLayout.visibility = View.VISIBLE
        }
    }

    private fun fullFields() {
        mEditTextUser.visibility = View.VISIBLE
        mEditTextName.visibility = View.VISIBLE
        mEditTextEmail.visibility = View.VISIBLE
    }

    private fun blockedMail() {
        mEditTextUser.visibility = View.VISIBLE
        mEditTextName.visibility = View.VISIBLE
        mEditTextEmail.visibility = View.GONE
    }

    private fun onlyMail() {
        mEditTextUser.visibility = View.GONE
        mEditTextName.visibility = View.GONE
        mEditTextEmail.visibility = View.VISIBLE
    }

    private fun validationEmail(email: String): Boolean {
        val regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
        return regex.matches(email)
    }

    private fun validationFields(): String {

        var message = ""

        var isValidNameEmail = false
        var isValidName = false
        var isValidEmail = false

        when (TStatusRD.getStatusName(value)) {
            TStatusRD.ALTERAR_DADOS, TStatusRD.ALTERAR_NOMES_EMAIL_EXPIRADO -> isValidNameEmail = true
            TStatusRD.ALTERAR_NOMES, TStatusRD.ALTERAR_NOMES_ALERT_EMAIL, TStatusRD.BLOQUEIO_NOME -> isValidName = true
            TStatusRD.BLOQUEIO_EMAIL, TStatusRD.AUTENTICAR_EMAIL,
            TStatusRD.BLOQUEIO_AUT_EMAIL, TStatusRD.ALTERAR_EMAIL_EXPIRADO -> isValidEmail = true
            TStatusRD.AGUARDE_CONFIRMACAO, TStatusRD.ALERTAR_EMAIL -> TODO()
        }

        if (isValidEmail || isValidNameEmail) {
            message =
                if (validationEmail(mEditTextEmail.text.toString())) "" else "E-mail inválido \n"
        }

        if (isValidNameEmail || isValidName) {
            message += if (mEditTextName.text.toString()
                    .isNotEmpty()
            ) "" else "Nome do usuário inválido \n"
            message += if (mEditTextUser.text.toString()
                    .isNotEmpty()
            ) "" else "Nome abreviado inválido\n"

        }

        return message
    }

    fun cleanFields() {
        mEditTextName.setText("")
        mEditTextUser.setText("")
        mEditTextEmail.setText("")

    }

    private fun sendInfo() {
        val message = validationFields()

        if (message.isNotEmpty()) {
            dialogsDefault.showDialogMessage(
                message, DialogsCustom.DIALOG_TYPE_ERROR
            ) {

            }
        } else {
            val connectionListener = object : BaseConnection.ConnectionListener {
                override fun onSucess(
                    response: String?,
                    typeConnection: Int,
                    reader: InputStreamReader?,
                    list: ArrayList<Array<String>>?
                ) {
                    showProgress(false)

                    val jsonObject = JSONObject(response!!)

                    val hasMessage = jsonObject.has("message")

                    if (hasMessage && jsonObject.getString("message").isNotEmpty()) {
                        dialogsDefault.showDialogMessage(
                            jsonObject.getString("message").replace("<br />", "\n"),
                            DialogsCustom.DIALOG_TYPE_WARNING
                        ) {
                            cleanFields()
                        }
                    } else {
                        dialogsDefault.showDialogMessage(
                            "Dados Enviados com sucesso", DialogsCustom.DIALOG_TYPE_SUCESS
                        ) {
                            finish()
                        }
                    }


                }

                override fun onError(
                    volleyError: VolleyError?,
                    code: Int,
                    typeConnection: Int,
                    response: String?
                ) {
                    showProgress(false)
                    dialogsDefault.showDialogMessage(
                        response, DialogsCustom.DIALOG_TYPE_ERROR
                    ) {
                        finish()
                    }
                }
            }
            connection = ChangePersonalDataConnection(this, connectionListener, token!!, user!!)
            showProgress(true)
             connection.setChangeDataUser(
                mEditTextUser.text.toString(),
                mEditTextName.text.toString(),
                mEditTextEmail.text.toString(),
                value != 160
            )

        }
    }

}
