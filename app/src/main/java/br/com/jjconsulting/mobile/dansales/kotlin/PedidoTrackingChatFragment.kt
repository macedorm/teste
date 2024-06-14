package br.com.jjconsulting.mobile.dansales.kotlin

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.BuildConfig
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.database.ChatDao
import br.com.jjconsulting.mobile.dansales.database.MessageDao
import br.com.jjconsulting.mobile.dansales.kotlin.adapter.ChatAdapter
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessageResult
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.AsyncTaskViewModel
import br.com.jjconsulting.mobile.dansales.kotlin.viewModel.PedidoTrackingViewModel
import br.com.jjconsulting.mobile.dansales.model.Message
import br.com.jjconsulting.mobile.dansales.service.Current
import br.com.jjconsulting.mobile.dansales.service.MyFirebaseMessagingService
import br.com.jjconsulting.mobile.dansales.util.CustomAPI
import br.com.jjconsulting.mobile.jjlib.OnPageSelected
import br.com.jjconsulting.mobile.jjlib.connection.soap.kotlin.SoapService
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils
import br.com.jjconsulting.mobile.jjlib.util.LogUser
import kotlinx.coroutines.launch
import java.util.*


class PedidoTrackingChatFragment : Fragment(R.layout.activity_chat), View.OnClickListener,
    SoapService.OnConnection, OnPageSelected {

    private val vm: AsyncTaskViewModel by lazy { ViewModelProvider(this)[AsyncTaskViewModel::class.java] }
    private val viewModel: PedidoTrackingViewModel by activityViewModels()

    private lateinit var dialogCustom: DialogsCustom
    private lateinit var mSendImageView: ImageView
    private lateinit var mMessageEditText: EditText
    private lateinit var mChatRecyclerView: RecyclerView
    private lateinit var mChatAdpater: ChatAdapter
    private lateinit var mManager: LinearLayoutManager
    private lateinit var mChatDao: ChatDao

    private var mMessageChat: MutableList<PedidoTrackingMessage>? = null
    private lateinit var messageChatLastSend: PedidoTrackingMessage

    var isLoading = false


    companion object {
        fun newInstance(): PedidoTrackingChatFragment {
            return PedidoTrackingChatFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogCustom = DialogsCustom(context)
        mChatDao = ChatDao(context)
        mMessageChat = mutableListOf()
        mChatAdpater = ChatAdapter(requireContext(), mMessageChat!!)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.setTitle(R.string.title_rastreio)

        mSendImageView = view.findViewById(R.id.fab_img)
        mMessageEditText = view.findViewById(R.id.message_edit_text)
        mChatRecyclerView = view.findViewById(R.id.list_message_recycler_view)

        mManager = LinearLayoutManager(context)
        mChatRecyclerView.setHasFixedSize(true)
        mChatRecyclerView.layoutManager = mManager

        mSendImageView.setOnClickListener(this)
        mChatRecyclerView.adapter = mChatAdpater

        loading()
    }

    fun loading() {
        vm.execute(onPreExecute = {
            dialogCustom = DialogsCustom(context)
        }, doInBackground = {
            lifecycleScope.launch {

                if(mMessageChat!!.size > 0 && mMessageChat!![mMessageChat!!.size - 1].id!! < 0){
                    var messageDao = MessageDao(context);

                    var message = Message()
                    message.idMessage = mMessageChat!![mMessageChat!!.size - 1].id!!
                    messageDao.setMessagemLida(message);
                }

                mMessageChat = mChatDao.getAll(
                    viewModel.nota.value,
                    viewModel.serieNota.value,
                    viewModel.cnpjEmitente.value
                )

                mChatAdpater = ChatAdapter(requireContext(), mMessageChat!!)
                mChatRecyclerView.adapter = mChatAdpater
                mChatRecyclerView.scrollToPosition(mMessageChat!!.size - 1)
                context?.let { NotificationManagerCompat.from(it).cancelAll() }
                MyFirebaseMessagingService.pushChat = null;
                isLoading = true;
            }
        }, onPostExecute = {
        })
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fab_img -> sendMessage()
            }
        }
    }

    private fun createNewMessageUser(message: String) {
        if (mMessageChat == null) {
            mMessageChat = mutableListOf()
        }

        var messageDao = MessageDao(context)

        messageChatLastSend = PedidoTrackingMessage()
        messageChatLastSend.message = message
        messageChatLastSend.id = messageDao.idNewMessage
        messageChatLastSend.type = 4
        messageChatLastSend.nf = viewModel.nota.value
        messageChatLastSend.serial = viewModel.serieNota.value
        messageChatLastSend.cnpj = viewModel.cnpjEmitente.value
        messageChatLastSend.date = FormatUtils.toTextToCompareDateInSQlite(Date())
        messageChatLastSend.typeUser = false
        mChatDao.insert(messageChatLastSend);

        mMessageChat!!.add(messageChatLastSend)

        activity?.runOnUiThread(Runnable {
            mChatAdpater.notifyItemInserted(mMessageChat!!.size - 1)
            mChatRecyclerView.scrollToPosition(mMessageChat!!.size - 1)
        })
    }

    private fun sendMessage() {
        var soapService = SoapService()

        val xml: String = CustomAPI.getBodySoapChat(
            BuildConfig.TOKEN_MULTI,
            viewModel.nota.value.toString(),
            viewModel.serieNota.value.toString(),
            viewModel.chave.value.toString(),
            viewModel.cnpjDestinatario.value.toString(),
            viewModel.cnpjEmitente.value.toString(),
            Current.getInstance(activity).usuario.codigo,
            mMessageEditText.text.toString()
        )
        LogUser.log("XML: $xml")


        vm.execute(onPreExecute = {
            dialogCustom.showDialogSending(true)
        }, doInBackground = {
            soapService.callService(
                xml,
                BuildConfig.URL_MULTI,
                CustomAPI.SOAPACTIONCHAT,
                this@PedidoTrackingChatFragment
            )
        }, onPostExecute = {
            dialogCustom.showDialogSending(false)
        })
    }

    override fun onSuccess(status: Int, response: String) {
        LogUser.log(response)

        lifecycleScope.launch {
            try {
                var messsageChat = PedidoTrackingMessageResult.fromJson(response)

                var result =
                    messsageChat?.messageEnvelopeResult?.messageBodyResult?.response?.chatResult

                if (result != null) {
                    if (result.aCodigoMensagem == "200") {
                        createNewMessageUser(mMessageEditText.text.toString())
                        mMessageEditText.setText("")
                        mMessageEditText.text.clear()


                    } else {
                        LogUser.log(result.aMensagem);

                        dialogCustom.showDialogMessage(
                            result.aMensagem,
                            DialogsCustom.DIALOG_TYPE_ERROR,
                            null
                        );
                    }
                }
            } catch (ex: Exception) {
                dialogCustom.showDialogMessage(
                    getString(R.string.error_connection_multi),
                    DialogsCustom.DIALOG_TYPE_ERROR,
                    null
                );
            }

        }
    }

    override fun onError(status: Int, response: String) {
        lifecycleScope.launch {
            try {
                if (response.isNotEmpty()) {
                    dialogCustom.showDialogMessage(response, DialogsCustom.DIALOG_TYPE_ERROR, null);
                } else {
                    dialogCustom.showDialogMessage(
                        getString(R.string.etap_error_connection_anexo),
                        DialogsCustom.DIALOG_TYPE_ERROR,
                        null
                    );
                }
            } catch (ex: Exception) {
                dialogCustom.showDialogMessage(
                    getString(R.string.error_connection_multi),
                    DialogsCustom.DIALOG_TYPE_ERROR,
                    null
                )
            }
        }
    }

    override fun onPageSelected(position: Int) {
    }
}

