package br.com.jjconsulting.mobile.dansales.kotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.base.BaseActivity
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection
import br.com.jjconsulting.mobile.dansales.kotlin.adapter.NFAdapter
import br.com.jjconsulting.mobile.dansales.kotlin.connectionController.NFConnection
import br.com.jjconsulting.mobile.dansales.kotlin.model.NotaFiscal
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.AsyncTaskViewModel
import br.com.jjconsulting.mobile.jjlib.model.RetError
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import br.com.jjconsulting.mobile.jjlib.util.TextUtils
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.lang.Exception
import java.util.ArrayList

class PedidoTrackingFilterActivity : BaseActivity(), BaseConnection.ConnectionListener {

    private val vm: AsyncTaskViewModel by lazy { ViewModelProvider(this)[AsyncTaskViewModel::class.java] }

    private lateinit var dialogCustom: DialogsCustom

    private lateinit var mNotaEditText: EditText
    private lateinit var mSerialNotaEditText: EditText
    private lateinit var mPedidoNotaEditText: EditText
    private lateinit var mOkButton: Button
    private lateinit var nfConnection: NFConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_tracking_filter)

        nfConnection = NFConnection(this, this)


        loadindComponents()
        addListener()
    }

    fun loadindComponents() {
        dialogCustom = DialogsCustom(this)

        mNotaEditText = findViewById(R.id.nota_edit_text)
        mSerialNotaEditText = findViewById(R.id.serial_edit_text)
        mPedidoNotaEditText = findViewById(R.id.pedido_nota_edit_text)
        mOkButton = findViewById(R.id.ok_button)
    }

    fun addListener() {
        mNotaEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                mPedidoNotaEditText.setText("")
            }
        }

        mSerialNotaEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                mPedidoNotaEditText.setText("")
            }
        }

        mPedidoNotaEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                mNotaEditText.setText("")
                mSerialNotaEditText.setText("")
            }
        }

        mOkButton.setOnClickListener {
            if(!mNotaEditText.text.toString().isNullOrEmpty() && !mSerialNotaEditText.text.toString().isNullOrEmpty()){
                startActivity(PedidoTrackingDetailActivity.newIntent(this, mNotaEditText.text.toString(), mSerialNotaEditText.text.toString(), ""))
            } else if (!mPedidoNotaEditText.text.toString().isNullOrEmpty()){
                startActivity(PedidoTrackingListNFActivity.newIntent(this, mPedidoNotaEditText.text.toString()))
            } else {
                showMessageError(getString(R.string.pedido_tracking_title_filter))
            }
        }
    }


    override fun onSucess(
        response: String?,
        typeConnection: Int,
        reader: InputStreamReader?,
        list: ArrayList<Array<String>>?
    ) {
        lifecycleScope.launch {
            var nf: NotaFiscal

            val gson = Gson()
            nf = gson.fromJson(response, NotaFiscal::class.java)

            dialogsDefault.showDialogLoading(false)

            startActivity(PedidoTrackingListNFActivity.newIntent(this@PedidoTrackingFilterActivity,
                mNotaEditText.text.toString(), mSerialNotaEditText.text.toString(), nf.cnpj))
        }
    }

    fun loading(){
        vm.execute(onPreExecute = {
            dialogCustom.showDialogLoading(true)
        }, doInBackground = {
            nfConnection.getNFDetails(mNotaEditText.text.toString(), mSerialNotaEditText.text.toString())
        }, onPostExecute = {
            dialogCustom.showDialogLoading(false)
        })
    }

    override fun onError(
        volleyError: VolleyError?,
        code: Int,
        typeConnection: Int,
        response: String?
    ) {
        lifecycleScope.launch {
            try {
                if (!TextUtils.isNullOrEmpty(response)) {
                    val error = gson.fromJson(response, RetError::class.java)
                    if (error != null) {
                        dialogsDefault.showDialogMessage(
                            error.message,
                            DialogsCustom.DIALOG_TYPE_ERROR,
                            null
                        )
                    }
                } else {
                    dialogsDefault.showDialogMessage(
                        getString(R.string.title_connection_error),
                        DialogsCustom.DIALOG_TYPE_ERROR,
                        null
                    )
                }
            } catch (ex: Exception) {
                LogUser.log(ex.message)
                dialogsDefault.showDialogMessage(
                    getString(R.string.title_connection_error),
                    DialogsCustom.DIALOG_TYPE_ERROR,
                    null
                )
            }
            dialogsDefault.showDialogLoading(false)
        }
    }
}