package br.com.jjconsulting.mobile.dansales.kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.jjconsulting.mobile.dansales.R
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage
import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessageResult
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils
import br.com.jjconsulting.mobile.jjlib.util.LogUser


class ChatAdapter(val mContext:Context, private val mMessageChat: List<PedidoTrackingMessage>) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mUserleftTextView: TextView = itemView.findViewById(R.id.user_left_text_view)
        val mBotRightTextView: TextView = itemView.findViewById(R.id.bot_right_text_view)
        val mNameDataleftTextView: TextView = itemView.findViewById(R.id.name_user_left_text_view)
        val mUserDataleftTextView: TextView = itemView.findViewById(R.id.data_user_left_text_view)
        val mBotDataRightTextView: TextView = itemView.findViewById(R.id.data_bot_right_text_view)
        val mBotRightViewGroup: ViewGroup = itemView.findViewById(R.id.bot_right_linear_layout)
        val mUserLeftViewGroup: ViewGroup = itemView.findViewById(R.id.user_left_linear_layout)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_chat, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageChat = mMessageChat[position]

        var ptDate: String? = ""

        val isTypeUser = messageChat.typeUser

        try {
            val date = FormatUtils.toDate(messageChat.date)
            ptDate = FormatUtils.toDefaultDateHoursBrazilianFormat(date)
        } catch (ex: Exception) {
            LogUser.log(ex.toString())
        }

        if (isTypeUser) {
            holder.mUserleftTextView.text = messageChat.message
            var name: String? = messageChat.name
            when(messageChat.type){
                1 -> name += " (Motorista)"
                2 -> name += " (Analista)"
                3 -> name += " (Vendedor)"
                4 -> name += " (Supervisor)"
                5 -> name += " (Gerente)"
            }

            holder.mNameDataleftTextView.text = name
            holder.mUserDataleftTextView.text = ptDate
        } else {
            holder.mBotRightTextView.text = messageChat.message
            holder.mBotDataRightTextView.text = ptDate
        }

        holder.mUserLeftViewGroup.visibility = if (isTypeUser) View.VISIBLE else View.GONE
        holder.mBotRightViewGroup.visibility = if (isTypeUser) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int {
        return  mMessageChat.size;
    }

    fun getItem(position:Int):PedidoTrackingMessage{
        return mMessageChat[position]
    }

}

