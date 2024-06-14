package br.com.jjconsulting.mobile.dansales.kotlin

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.database.PedidoLogDao
import br.com.jjconsulting.mobile.dansales.kotlin.adapter.PedidoLogAdapter
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import androidx.appcompat.app.AppCompatActivity
import br.com.jjconsulting.mobile.dansales.R


class PedidoLogFragment: Fragment(R.layout.fragment_log_pedido){

    private lateinit var mLogPedidoRecyclerView: RecyclerView

    private lateinit var dialogCustom: DialogsCustom

    private lateinit var mContainerEmpty: LinearLayout

    var codigoPedido: String? = null

    companion object {
        const val ARG_CODIGO = "idPedido"


        fun newInstance(idPedido:String): PedidoLogFragment {
            val fragment = PedidoLogFragment()
            val args = Bundle()
            args.putString(ARG_CODIGO, idPedido)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogCustom = DialogsCustom(context)
        codigoPedido = requireArguments().getString(ARG_CODIGO, "");

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.setTitle(R.string.title_log)
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = codigoPedido

        mContainerEmpty = view.findViewById(R.id.list_empty_text_view)
        mLogPedidoRecyclerView = view.findViewById(R.id.log_pedido_recycler_view)

        mLogPedidoRecyclerView.layoutManager = LinearLayoutManager(activity)
        DividerItemDecoration(mLogPedidoRecyclerView.context, DividerItemDecoration.VERTICAL)

        loading()
    }

    fun loading() {
        val pedidoDao = PedidoLogDao(context)
        val listLogPedido = pedidoDao.getLogPedido(codigoPedido)

        val mLogPedidoAdapter = PedidoLogAdapter(context, listLogPedido)
        mLogPedidoRecyclerView.adapter = mLogPedidoAdapter
        mContainerEmpty.visibility = if (listLogPedido.size > 0) View.GONE else View.VISIBLE
        mLogPedidoRecyclerView.visibility = View.VISIBLE
    }
}

