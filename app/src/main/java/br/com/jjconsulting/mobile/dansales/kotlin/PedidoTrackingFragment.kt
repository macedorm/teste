package br.com.jjconsulting.mobile.dansales.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.BuildConfig
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.kotlin.adapter.PedidoTrackingAdapter
import br.com.jjconsulting.mobile.dansales.kotlin.model.BSituacaoNotaFiscalOcorrencia
import br.com.jjconsulting.mobile.dansales.kotlin.model.ConsultaSituacaoNotaFiscalResult
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.AsyncTaskViewModel
import br.com.jjconsulting.mobile.dansales.util.CustomAPI
import br.com.jjconsulting.mobile.jjlib.connection.soap.kotlin.SoapService
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import kotlinx.coroutines.launch
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTracking
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.PedidoTrackingViewModel
import br.com.jjconsulting.mobile.jjlib.OnPageSelected
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

 class PedidoTrackingFragment: Fragment(R.layout.fragment_pedido_tracking), View.OnClickListener, SoapService.OnConnection, OnMapReadyCallback, OnPageSelected {

    private val vm: AsyncTaskViewModel by lazy { ViewModelProvider(this)[AsyncTaskViewModel::class.java] }

    private val pedidoTrackingViewModel: PedidoTrackingViewModel by activityViewModels()

    private lateinit var mLogPedidoRecyclerView: RecyclerView

    private lateinit var dialogCustom: DialogsCustom

    private lateinit var mInfoTextView: TextView

    private lateinit var mContainerRelativeLayout: RelativeLayout

    private lateinit var mContainerEmpty: LinearLayout

    private lateinit var mContainerList:CardView

    private lateinit var mLinkButton:Button

    private lateinit var mGoogleMaps:GoogleMap

    private lateinit var mMapsCardView:CardView

    private lateinit var mStatusTextView:TextView;

    private lateinit var mStatusImageView:ImageView

    var loadingChat = false;

    companion object {

        fun newInstance(): PedidoTrackingFragment {
            return PedidoTrackingFragment()
        }

        fun newInstance(loadingChat:Boolean): PedidoTrackingFragment {
            var pedidoTrackingFragment = PedidoTrackingFragment()
            pedidoTrackingFragment.loadingChat = loadingChat
            return pedidoTrackingFragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                false
            }
            R.id.action_save -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogCustom = DialogsCustom(context)

        LogUser.log(pedidoTrackingViewModel.nota.value)
        LogUser.log(pedidoTrackingViewModel.serieNota.value)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mMapsCardView = view.findViewById(R.id.map_log_pedido_card_view)
        mContainerList = view.findViewById(R.id.log_pedido_card_view);

        mStatusTextView = view.findViewById(R.id.status_pedido_text_view);
        mStatusImageView = view.findViewById(R.id.ivIconStatus)

        mContainerEmpty = view.findViewById(R.id.list_empty_text_view)
        mContainerRelativeLayout = view.findViewById(R.id.container_relative_layout)

        mLogPedidoRecyclerView = view.findViewById(R.id.log_pedido_recycler_view)
        mLogPedidoRecyclerView.setHasFixedSize(true)

        mLinkButton =  view.findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(this)

        mLogPedidoRecyclerView.layoutManager = LinearLayoutManager(activity)
        DividerItemDecoration(mLogPedidoRecyclerView.context, DividerItemDecoration.VERTICAL)

        mInfoTextView = view.findViewById(R.id.info_text_view)

        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun loading(){
        vm.execute(onPreExecute = {
            dialogCustom.showDialogLoading(true)
        }, doInBackground = {
            getStatusNF(pedidoTrackingViewModel.nota.value.toString(),  pedidoTrackingViewModel.serieNota.value.toString(), pedidoTrackingViewModel.cnpj.value.toString())
        }, onPostExecute = {
            dialogCustom.showDialogLoading(false)
        })
    }

    private fun getStatusNF(nota:String, serie:String, cnpj:String){
        var soapService = SoapService()
        val xml:String = CustomAPI.getBodySoapNF(BuildConfig.TOKEN_MULTI, Integer.parseInt(nota.trim()).toString(), Integer.parseInt(serie.trim()).toString(), cnpj)

        soapService.callService(xml, BuildConfig.URL_MULTI, CustomAPI.SOAPACTION, this@PedidoTrackingFragment)
    }

    override fun onSuccess(status: Int, response: String) {
        lifecycleScope.launch {
            try {
                var pedidoTracking = PedidoTracking.fromJson(response)

                var consultaSituacaoNotaFiscalResult =


                    pedidoTracking?.sEnvelope?.sBody?.consultaSituacaoNotaFiscalResponse?.consultaSituacaoNotaFiscalResult

                if (consultaSituacaoNotaFiscalResult != null) {

                    if (consultaSituacaoNotaFiscalResult.aCodigoMensagem == "200") {

                        pedidoTrackingViewModel.codigoIntegracao.value = consultaSituacaoNotaFiscalResult.aObjeto?.bDestinatario?.cCodigoIntegracao.toString()

                        if(!consultaSituacaoNotaFiscalResult.aObjeto?.bNota?.bSituacaoNotaFiscalNotasCargaList.isNullOrEmpty()){

                            for(item in consultaSituacaoNotaFiscalResult.aObjeto?.bNota?.bSituacaoNotaFiscalNotasCargaList!!){
                                if(pedidoTrackingViewModel.nota.value.equals(item.numeroNota)){
                                    pedidoTrackingViewModel.cnpjEmitente.value = item.bCNPJEmitente
                                    pedidoTrackingViewModel.cnpjDestinatario.value =
                                        consultaSituacaoNotaFiscalResult.aObjeto?.bDestinatario?.cCPFCNPJ?.replace(".","")
                                            ?.replace("/", "")?.replace("-","");
                                    pedidoTrackingViewModel.chave.value = item.chave
                                }
                            }
                            LogUser.log("CNPJ: " + pedidoTrackingViewModel.cnpjEmitente.value)

                        } else {
                            pedidoTrackingViewModel.cnpjEmitente.value =
                                consultaSituacaoNotaFiscalResult.aObjeto?.bNota?.bSituacaoNotaFiscalNotasCarga?.bCNPJEmitente
                            pedidoTrackingViewModel.chave.value = consultaSituacaoNotaFiscalResult.aObjeto?.bNota?.bSituacaoNotaFiscalNotasCarga?.chave
                        }

                        mContainerRelativeLayout.visibility = View.VISIBLE
                        mContainerEmpty.visibility = View.GONE

                        val mLogPedidoAdapter = PedidoTrackingAdapter(
                            activity as AppCompatActivity,
                            pedidoTracking?.getOcorrencias() as List<BSituacaoNotaFiscalOcorrencia>
                        )

                        if ((pedidoTracking?.getOcorrencias() as List<BSituacaoNotaFiscalOcorrencia>).isEmpty()) {
                            mContainerList.visibility = View.GONE
                            mStatusTextView.text = "Status não informado"
                            mStatusTextView.setTextColor(resources.getColor(R.color.colorSpinnerBorder))
                            mStatusImageView.setColorFilter(
                                resources.getColor(R.color.colorSpinnerBorder),
                                android.graphics.PorterDuff.Mode.MULTIPLY
                            );
                        } else {
                            mStatusTextView.text =
                                "Status: " + (pedidoTracking?.getOcorrencias() as List<BSituacaoNotaFiscalOcorrencia>)[0]?.bSituacao

                            var status: String? =
                                (pedidoTracking?.getOcorrencias() as List<BSituacaoNotaFiscalOcorrencia>)[0]?.bSituacao.toString()
                                    .lowercase()

                            if (status.equals("entregue") || status.equals("reentrega")) {
                                mStatusTextView.setTextColor(resources.getColor(R.color.sucessCollor))
                                mStatusImageView.setColorFilter(
                                    resources.getColor(R.color.sucessCollor),
                                    android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            } else if (status.equals("nenhum") || status.equals("status não informado")) {
                                mStatusTextView.setTextColor(resources.getColor(R.color.colorSpinnerBorder))
                                mStatusImageView.setColorFilter(
                                    resources.getColor(R.color.colorSpinnerBorder),
                                    android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            } else if (status.equals("devolvida")) {
                                mStatusTextView.setTextColor(resources.getColor(R.color.errorCollor))
                                mStatusImageView.setColorFilter(
                                    resources.getColor(R.color.errorCollor),
                                    android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            } else {
                                mStatusTextView.setTextColor(resources.getColor(R.color.alertCollor))
                                mStatusImageView.setColorFilter(
                                    resources.getColor(R.color.alertCollor),
                                    android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                        }

                        mLogPedidoRecyclerView.adapter = mLogPedidoAdapter

                        var info: String =
                            "<b>" + getString(R.string.pedido_tracking_driver) + "</b> " + consultaSituacaoNotaFiscalResult.aObjeto?.bMotoristaNome.toString() + "<br><br>"
                        info += "<b>" + getString(R.string.pedido_tracking_shipping_company) + "</b> " + consultaSituacaoNotaFiscalResult.aObjeto?.bVeiculoTransportadora.toString() + "<br><br>"
                        info += "<b>" + getString(R.string.pedido_tracking_vehicle_plate) + "</b> " + consultaSituacaoNotaFiscalResult.aObjeto?.bVeiculoPlaca.toString()

                        mInfoTextView.text = Html.fromHtml(info)

                        visibleFields(consultaSituacaoNotaFiscalResult)

                        if(loadingChat){
                            loadingChat = false;

                            var detail =  activity as PedidoTrackingDetailActivity
                            detail.viewPager.currentItem = 1
                        }

                    } else {

                        LogUser.log(consultaSituacaoNotaFiscalResult.aMensagem);

                        dialogCustom.showDialogMessage(
                            consultaSituacaoNotaFiscalResult.aMensagem,
                            DialogsCustom.DIALOG_TYPE_ERROR,
                            null
                        );
                    }
                }
            }catch (ex:Exception ){
                LogUser.log(ex.toString())

                dialogCustom.showDialogMessage(getString(R.string.error_connection_multi), DialogsCustom.DIALOG_TYPE_ERROR, null);
            }

        }
    }

    override fun onError(status: Int, response: String) {

        LogUser.log(response);

        lifecycleScope.launch {
            try{
                if(response.isNotEmpty()){
                    dialogCustom.showDialogMessage(response, DialogsCustom.DIALOG_TYPE_ERROR, null);
                } else {
                    dialogCustom.showDialogMessage(getString(R.string.etap_error_connection_anexo), DialogsCustom.DIALOG_TYPE_ERROR, null);
                } }catch (ex:Exception ){
                    dialogCustom.showDialogMessage(getString(R.string.error_connection_multi), DialogsCustom.DIALOG_TYPE_ERROR, null);
            }
        }
    }

    fun visibleFields(consultaSituacaoNotaFiscalResult:ConsultaSituacaoNotaFiscalResult){
        if(consultaSituacaoNotaFiscalResult.aObjeto?.bLinkRastreio.isNullOrEmpty()){
            mLinkButton.visibility = View.GONE
        } else {
            mLinkButton.tag = consultaSituacaoNotaFiscalResult.aObjeto?.bLinkRastreio
        }

        var lat = consultaSituacaoNotaFiscalResult.aObjeto?.bPosicaoLatitude
        var lng = consultaSituacaoNotaFiscalResult.aObjeto?.bPosicaoLongitude

        if(lat?.toDouble() != 0.0 && lng?.toDouble() != 0.0){
            if (lat != null && lng != null) {
                addMaps(lat, lng)
            }
        } else {
            mMapsCardView.visibility = View.GONE
        }
    }

    private fun addMaps(latitude:String, longitude: String){
        val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
        mGoogleMaps.addMarker(
            MarkerOptions()
                .position(latLng)
        )
        mGoogleMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMaps.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ));

    }

    private fun openLink(url:String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.link_button -> openLink(v.tag.toString())
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        if (p0 != null) {
            mGoogleMaps = p0
        }
    }
    override fun onPageSelected(position: Int) {
        loading()
    }

}

