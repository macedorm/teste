package br.com.jjconsulting.mobile.dansales.kotlin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingFragment.Companion.newInstance
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity

class PedidoTrackingActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return newInstance()
    }

    override fun useOnBackPressedInUpNavigation(): Boolean {
        return false
    }

    companion object {
        fun newIntent(packageContext: Context):Intent {
            return Intent(packageContext, PedidoTrackingActivity::class.java)
        }
    }
}