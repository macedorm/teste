package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.content.Intent;

import br.com.jjconsulting.mobile.dansales.LoginActivity;
import br.com.jjconsulting.mobile.dansales.SystemUpdateActivity;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ManagerSystemUpdate {

    public static boolean isRequiredUpadate(Context context, String errorMessage){
        if(!TextUtils.isNullOrEmpty(errorMessage) && errorMessage.toLowerCase().contains("dispositivo desatualizado")){
            Intent systemUpdate = new Intent(context,
                    SystemUpdateActivity.class);
            context.startActivity(systemUpdate);
            return true;
        } else {
            return false;
        }
    }
}
