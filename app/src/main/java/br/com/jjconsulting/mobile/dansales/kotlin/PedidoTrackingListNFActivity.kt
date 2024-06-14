package br.com.jjconsulting.mobile.dansales.kotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.base.BaseActivity
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection
import br.com.jjconsulting.mobile.dansales.kotlin.adapter.NFAdapter
import br.com.jjconsulting.mobile.dansales.kotlin.connectionController.NFConnection
import br.com.jjconsulting.mobile.dansales.kotlin.model.NotaFiscal
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.AsyncTaskViewModel
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.PedidoTrackingListNFViewModel
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport
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

class PedidoTrackingListNFActivity : BaseActivity(), BaseConnection.ConnectionListener {

    private val vm: AsyncTaskViewModel by lazy { ViewModelProvider(this)[AsyncTaskViewModel::class.java] }
    private val viewModel: PedidoTrackingListNFViewModel by lazy { ViewModelProvider(this)[PedidoTrackingListNFViewModel::class.java] }

    private lateinit var mNFRecyclerView: RecyclerView
    private lateinit var mNFAdapter: NFAdapter
    private lateinit var mEmpty: LinearLayout
    private lateinit var nfConnection: NFConnection

    companion object {
        const val KEY_CODIGO = "key_codigo"
        const val KEY_NF = "key_NF"
        const val KEY_SERIAL = "key_serial"
        const val KEY_IS_NF = "key_is_nf"

        /**
         * Consulta por nota fiscal
         */
        fun newIntent(packageContext: Context, codigo: String): Intent {
            val intent = Intent(packageContext, PedidoTrackingListNFActivity::class.java)
            intent.putExtra(KEY_CODIGO, codigo)
            intent.putExtra(KEY_IS_NF, false)
            return intent
        }

        /**
         * Consulta por nota fiscal
         */
        fun newIntent(packageContext: Context, nf: String, serial:String, cnpj:String): Intent {
            val intent = Intent(packageContext, PedidoTrackingListNFActivity::class.java)
            //Consulta por nota fiscal
            intent.putExtra(KEY_NF, nf)
            intent.putExtra(KEY_SERIAL, serial)
            intent.putExtra(KEY_CODIGO, cnpj)
            intent.putExtra(KEY_IS_NF, true)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_nf)

        viewModel.isNF.value = intent.getBooleanExtra(KEY_IS_NF, false)
        viewModel.code.value = intent.getStringExtra(KEY_CODIGO)

        if(viewModel.isNF.value!!){
            viewModel.nf.value = intent.getStringExtra(KEY_NF)
            viewModel.serial.value = intent.getStringExtra(KEY_SERIAL)
        }

        viewModel.isNF.value = intent.getBooleanExtra(KEY_IS_NF, false)
        nfConnection = NFConnection(this, this)

        loadindComponents()
        loading()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    fun loadindComponents() {
        mEmpty = findViewById(R.id.list_empty_text_view)
        mNFRecyclerView = findViewById(R.id.nf_recycler_view)

        mNFRecyclerView.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(
            mNFRecyclerView.context, DividerItemDecoration.VERTICAL
        )
        divider.setDrawable(this.resources.getDrawable(R.drawable.custom_divider))
        mNFRecyclerView.addItemDecoration(divider)

        ItemClickSupport.addTo(mNFRecyclerView)
            .setOnItemClickListener { _: RecyclerView?, position: Int, _: View? ->
                var notaFiscal: NotaFiscal = mNFAdapter.getItem(position)
                startActivity(
                    PedidoTrackingDetailActivity.newIntent(
                        this,
                        notaFiscal.notaFiscal,
                        notaFiscal.serieNotaFiscal,
                        notaFiscal.cnpj
                    )
                )
            }

        mNFRecyclerView.visibility = View.GONE
        mEmpty.visibility = View.GONE
    }

    fun loading() {
        vm.execute(onPreExecute = {
        }, doInBackground = {
            lifecycleScope.launch {
                if(viewModel.isNF.value == true){
                    startActivity(
                        PedidoTrackingDetailActivity.newIntent(
                            this@PedidoTrackingListNFActivity,
                            viewModel.nf.value!!,
                            viewModel.serial.value!!,
                            viewModel.code.value!!
                        )
                    )
                    this@PedidoTrackingListNFActivity.finish()
                } else {
                    if (!isFinishing) {
                        dialogsDefault.showDialogLoading(true)
                        nfConnection.getNF(viewModel.code.value!!)
                    }
                }
            }
        }, onPostExecute = {
        })
    }

    override fun onSucess(
        response: String?,
        typeConnection: Int,
        reader: InputStreamReader?,
        list: ArrayList<Array<String>>?
    ) {
        lifecycleScope.launch {
            var nf: List<NotaFiscal>

            val gson = Gson()
            val itemType = object : TypeToken<List<NotaFiscal>>() {}.type
            nf = gson.fromJson(response, itemType)

            mNFAdapter = NFAdapter(this@PedidoTrackingListNFActivity, nf)
            mNFRecyclerView.adapter = mNFAdapter
            dialogsDefault.showDialogLoading(false)


            if(nf.isNotEmpty()){
                if(nf.size == 1){
                    var notaFiscal: NotaFiscal = mNFAdapter.getItem(0)
                    startActivity(
                        PedidoTrackingDetailActivity.newIntent(
                            this@PedidoTrackingListNFActivity,
                            notaFiscal.notaFiscal,
                            notaFiscal.serieNotaFiscal,
                            notaFiscal.cnpj
                        )
                    )
                    this@PedidoTrackingListNFActivity.overridePendingTransition(0,0)
                    this@PedidoTrackingListNFActivity.finish()
                } else {
                    mNFRecyclerView.visibility = View.VISIBLE
                    mEmpty.visibility = View.GONE
                }
            } else {
                mNFRecyclerView.visibility = View.GONE
                mEmpty.visibility = View.VISIBLE
            }

        }
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

            mNFRecyclerView.visibility = View.GONE
            mEmpty.visibility = View.VISIBLE

            dialogsDefault.showDialogLoading(false)
        }
    }
}