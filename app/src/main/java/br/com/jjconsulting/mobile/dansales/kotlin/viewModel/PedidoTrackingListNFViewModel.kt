package br.com.jjconsulting.mobile.dansales.kotlin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.jjconsulting.mobile.dansales.util.CustomAPI
import br.com.jjconsulting.mobile.jjlib.connection.soap.kotlin.SoapService
import br.com.jjconsulting.mobile.jjlib.util.LogUser

class PedidoTrackingListNFViewModel: ViewModel() {


    val code: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    fun getCode(): LiveData<String> {
        return code
    }

    val nf: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    fun getNF(): LiveData<String> {
        return code
    }


    val serial: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    fun getSerial(): LiveData<String> {
        return serial
    }

    val cnpj: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    fun getCnpj(): LiveData<String> {
        return serial
    }

    val isNF: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
        }
    }

    fun isNF(): LiveData<Boolean> {
        return isNF
    }


}