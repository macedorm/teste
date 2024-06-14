package br.com.jjconsulting.mobile.dansales.kotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

import com.google.android.material.tabs.TabLayoutMediator

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.PedidoTrackingViewModel
import br.com.jjconsulting.mobile.dansales.service.MyFirebaseMessagingService
import br.com.jjconsulting.mobile.jjlib.OnPageSelected
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import br.com.jjconsulting.mobile.jjlib.util.TextUtils
import java.lang.Exception

class PedidoTrackingDetailActivity : AppCompatActivity() {

    private val pedidoTrackingViewModel: PedidoTrackingViewModel by lazy { ViewModelProvider(this)[PedidoTrackingViewModel::class.java] }

    lateinit var broadcastReceiver: BroadcastReceiver

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2
    private val viewModel: PedidoTrackingViewModel by viewModels()

    lateinit var viewPagerAdapter:ViewPagerAdapter

    var loadingChat = false;

    companion object {
        val KEY_NOTE: String = "key_note"
        val KEY_SERIAL: String = "key_serial"
        val KEY_CNPJ: String = "key_cnpj"
        val KEY_PUSH: String = "key_push"

        fun newIntent(packageContext: Context, nota: String, serieNota: String, cnpj:String): Intent {
            val intent = Intent(packageContext, PedidoTrackingDetailActivity::class.java)
            intent.putExtra(KEY_NOTE, nota.replace("^0*", ""))
            intent.putExtra(KEY_SERIAL, serieNota.replace("^0*", ""))
            intent.putExtra(KEY_CNPJ, cnpj.replace("^0*", ""))
            return intent
        }

        fun newIntentPush(packageContext: Context, nota: String, serieNota: String, cnpj:String): Intent {
            val intent = Intent(packageContext, PedidoTrackingDetailActivity::class.java)
            intent.putExtra(KEY_NOTE, nota.replace("^0*", ""))
            intent.putExtra(KEY_SERIAL, serieNota.replace("^0*", ""))
            intent.putExtra(KEY_CNPJ, cnpj.replace("^0*", ""))
            intent.putExtra(KEY_PUSH, true)
            return intent
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_tracking_detail)

         var selectedPage = 0

        registerReceiver();

        pedidoTrackingViewModel.nota.value = Integer.parseInt(intent.getStringExtra(KEY_NOTE)).toString()
        pedidoTrackingViewModel.serieNota.value = Integer.parseInt(intent.getStringExtra(KEY_SERIAL)).toString()
        pedidoTrackingViewModel.cnpj.value = intent.getStringExtra(KEY_CNPJ)

        if(intent.getSerializableExtra(KEY_PUSH) != null) {
            loadingChat = true;
        }

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tabs)
        viewPager.adapter = createCardAdapter(loadingChat)

        viewPager.currentItem = selectedPage

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if(viewPagerAdapter != null){
                    val listener = viewPagerAdapter.getItem(position) as OnPageSelected
                    listener.onPageSelected(position)
                }
            }
        })

        TabLayoutMediator(tabLayout, viewPager
        ) { tab, position -> tab.text = getNameTab(position) }.attach()
    }

    fun getNameTab(position: Int):String{
        return when (position) {
            0 -> "Status"
            else -> "Chat"
        }
    }

    private fun createCardAdapter(loadingChat:Boolean): ViewPagerAdapter? {
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.loadingChat = loadingChat
        return viewPagerAdapter
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        private val mapFragments: HashMap<Int, Fragment> = HashMap()
        var loadingChat = false;

        override fun createFragment(position: Int): Fragment {
            if(!mapFragments.containsKey(position)){
                mapFragments[0] = PedidoTrackingFragment.newInstance(loadingChat)
                mapFragments[1] = PedidoTrackingChatFragment.newInstance()
            }

            return mapFragments[position]!!
        }

        override fun getItemCount(): Int {
            return CARD_ITEM_SIZE
        }

         fun getItem(position: Int): Fragment {



            return mapFragments[position]!!
        }

        companion object {
            lateinit var note:String
            lateinit var serial:String
            private const val CARD_ITEM_SIZE = 2
        }
    }

    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if(MyFirebaseMessagingService.pushChat.get("NumNF").equals(pedidoTrackingViewModel.nota.value)){

                    if(viewPagerAdapter == null){
                        return
                    }


                    val pedidoTrackingChatFragment = viewPagerAdapter.getItem(1) as PedidoTrackingChatFragment

                    if(pedidoTrackingChatFragment.isLoading){
                        pedidoTrackingChatFragment.loading()
                    }

                    viewPager.currentItem = 1

                    val nMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    nMgr.cancelAll()
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("push"))
    }

     override fun onDestroy() {
        super.onDestroy()

         try {
             if (broadcastReceiver != null) {
                 LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
             }
         }catch(ex:Exception){
             LogUser.log(ex.message);
         }
    }
}