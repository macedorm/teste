package br.com.jjconsulting.mobile.dansales.kotlin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingFragment.Companion.newInstance
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity
import java.lang.RuntimeException

class PedidoLogActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return PedidoLogFragment.newInstance(data)

    }

    override fun useOnBackPressedInUpNavigation(): Boolean {
        return true
    }

    companion object {
        @JvmStatic
        fun newIntent(packageContext: Context, idPedido: String):Intent {
            val intent = Intent(packageContext, PedidoLogActivity::class.java)
            intent.putExtra(KEY_DATA_PAR, idPedido)
            return intent
        }

    }
}