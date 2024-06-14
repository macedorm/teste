package br.com.jjconsulting.mobile.dansales.kotlin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.jjconsulting.mobile.dansales.kotlin.model.PushNotaFiscal
import br.com.jjconsulting.mobile.dansales.model.Pedido
import br.com.jjconsulting.mobile.dansales.util.CustomAPI
import br.com.jjconsulting.mobile.jjlib.connection.soap.kotlin.SoapService
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import java.io.Serializable

class PedidoTrackingViewModel: ViewModel() {

    val chave: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    val codigoIntegracao: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }


    val cnpjDestinatario: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }


    val cnpjEmitente: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }


    val serieNota: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    val nota: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

    val pushNotaFiscal: MutableLiveData<PushNotaFiscal> by lazy {
        MutableLiveData<PushNotaFiscal>().also {
        }
    }

    val cnpj: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
        }
    }

}