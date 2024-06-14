package br.com.jjconsulting.mobile.dansales.kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.kotlin.model.NotaFiscal
import br.com.jjconsulting.mobile.dansales.util.PedidoUtils
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils


class NFAdapter(val mContext:Context, private val mNF: List<NotaFiscal>) :
        RecyclerView.Adapter<NFAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mStatus: ImageView = itemView.findViewById(R.id.nf_status_image_view)
        val mNome: TextView = itemView.findViewById(R.id.nf_cliente_nome_text_view)
        val mCodigoSapTextView: TextView = itemView.findViewById(R.id.nf_pedido_sap_text_view)
        val mDataCadastroTextView: TextView = itemView.findViewById(R.id.nf_emissao_sap_text_view)
        val mNFTextView: TextView = itemView.findViewById(R.id.nf_num_sap_text_view)
        val mSerieNFTextView: TextView = itemView.findViewById(R.id.nf_serie_sap_text_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_nf, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nf: NotaFiscal = mNF[position]
        PedidoUtils.getPedidoIconImageResourceId(mContext, holder.mStatus, nf.status, nf.origem);

        holder.mNome.text = nf.codCli + " " + nf.nomeCli
        holder.mCodigoSapTextView.text = mContext.getString(
                R.string.pedido_codigo_sap_label, if (nf.sap == null)
                    mContext.getString(R.string.null_field) else nf.sap);
        holder.mDataCadastroTextView.text = mContext.getString(R.string.pedido_emissao_label, nf.data)

        holder.mNFTextView.text = mContext.getString(R.string.pedido_nf_label, nf.notaFiscal)

        holder.mSerieNFTextView.text = mContext.getString(R.string.pedido_serie_nf_label, nf.serieNotaFiscal)
    }

    override fun getItemCount(): Int {
        return  mNF.size;
    }

    fun getItem(position:Int):NotaFiscal{
        return mNF[position]
    }

}

