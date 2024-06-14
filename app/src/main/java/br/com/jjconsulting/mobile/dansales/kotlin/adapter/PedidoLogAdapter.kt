package br.com.jjconsulting.mobile.dansales.kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.kotlin.model.BSituacaoNotaFiscalOcorrencia
import br.com.jjconsulting.mobile.dansales.model.LogPedido
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils


class PedidoLogAdapter(val context:Context?, private val mLog: ArrayList<LogPedido>?) :
        RecyclerView.Adapter<PedidoLogAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDesc: TextView = itemView.findViewById(R.id.log_descricao)
        val mNome: TextView = itemView.findViewById(R.id.log_nome)
        val mData: TextView = itemView.findViewById(R.id.log_date)
        val mLineTop: LinearLayout = itemView.findViewById(R.id.line_top_linear_layout)
        val mLineBottom: LinearLayout = itemView.findViewById(R.id.line_bottom_linear_layout)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_log_pedido, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val log = mLog?.get(position)
        if (log != null) {
            holder.mDesc.text = log.descricao
            holder.mData.text = context?.getString(R.string.log_item_date, FormatUtils.toDefaultDateAndHourFormat(context, log.data))
            holder.mNome.text = log.nome
        }

        holder.mLineTop.visibility = View.VISIBLE
        holder.mLineBottom.visibility = View.VISIBLE

        if (position == 0) {
            holder.mLineTop.visibility = View.INVISIBLE
        }

        if (mLog != null) {
            if (position + 1 == mLog.size) {
                holder.mLineBottom.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return if(mLog == null){
            0
        } else {
            mLog.size
        }
    }

}

