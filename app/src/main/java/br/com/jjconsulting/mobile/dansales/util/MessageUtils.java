package br.com.jjconsulting.mobile.dansales.util;


import br.com.jjconsulting.mobile.dansales.R;

public class MessageUtils {

    public static int getBackgroundIcon(boolean isRead) {
        if(isRead){
            return R.drawable.background_icon_message_oval;
        } else {
            return R.drawable.background_icon_message_unread_oval;
        }
    }

    public static int getString(TMessageType tMessageType) {
        switch (tMessageType){
            case MESSAGEM:
                return R.string.message_type_mes;
            case PUSH:
                return R.string.message_type_push;
            case ROTA_GUIADA:
                return R.string.message_type_rg;
             default:
                 return R.string.message_type_mes;

        }

    }

    public static int getIcon(TMessageType tMessageType, boolean isRead) {
        switch (tMessageType){
            case MESSAGEM:
                if(isRead){
                    return R.drawable.ic_message_type_1_read_40dp;
                } else {
                    return R.drawable.ic_message_type_1_unread_40dp;
                }
            case PUSH:
                return R.drawable.ic_message_type_3_40dp;
            case ROTA_GUIADA:
                return R.drawable.ic_message_type_2_40dp;
            default:
                return R.drawable.ic_message_type_1_read_40dp;

        }
    }

}
