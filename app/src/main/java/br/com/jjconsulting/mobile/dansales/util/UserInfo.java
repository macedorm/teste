package br.com.jjconsulting.mobile.dansales.util;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;

public class UserInfo {

    private static final String SHARED_PREFERENCE_FILE = "user_info";


    private SavePref savePref;
    private Gson gson;
    private Login login;

    public UserInfo() {
        savePref = new SavePref();
        gson = new Gson();
    }


    public boolean isFirstLogin(Context context, String codigoUsuario) {
        boolean isFirstLogin = true;

        String userInfo = savePref.getPref(SHARED_PREFERENCE_FILE, Config.TAG, context);

        if (userInfo != null && userInfo.length() > 0) {
            try {
                if (userInfo.equals(codigoUsuario)) {
                    isFirstLogin = false;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return isFirstLogin;

    }

    public void setFirstLogin(Context context, String codigoUsuario) {
        if(codigoUsuario != null){
            savePref.saveSharedPreferences(SHARED_PREFERENCE_FILE, Config.TAG, codigoUsuario, context);
        }

    }

    /**
     * Delete Dictionary
     *
     * @param context
     */
    public void deleteFirstLogin(Context context) {
        savePref.deleteSharedPreferences(SHARED_PREFERENCE_FILE, Config.TAG, context);
    }

    /**
     * Save loading user ???
     */
    public Login getUserInfo(Context context) {
        if (login != null)
            return login;

        String userInfo = savePref.getPref(Config.TAG_USER_INFO, Config.TAG, context);
        if (userInfo != null && userInfo.length() > 0) {
            try {
                login = gson.fromJson(userInfo, Login.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return login;
    }


    public String getUserId(Context context) {
        return savePref.getPref(SHARED_PREFERENCE_FILE, Config.TAG, context);
    }

    /**
     * Save user Info
     *
     * @param login,
     * @param context
     */
    public void saveUserInfo(Login login, Context context) {
        //deleteUserInfo(context);

        this.login = login;

        if (login.getToken() == null) {
            login.setToken("");
        }

        String json = gson.toJson(login);

        savePref.saveSharedPreferences(Config.TAG_USER_INFO, Config.TAG, json, context);
    }

    /**
     * get user Info Unidade Negocio
     *
     * @param unidadeNegocio,
     * @param context
     */
    public void saveUserUnidadeNegocioSelected(String unidadeNegocio, Context context) {
        savePref.saveSharedPreferences(Config.TAG_USER_INFO_UN, Config.TAG, unidadeNegocio, context);
    }


    /**
     * get user Info Unidade Negocio
     *
     * @param context
     */
    public String getUserUnidadeNegocioSelected(Context context) {
        String userInfoUnidadeNegocio = savePref.getPref(Config.TAG_USER_INFO_UN, Config.TAG, context);
        return userInfoUnidadeNegocio;
    }

    /**
     * Delete User Info
     *
     * @param context
     */
    public void deleteUserInfo(Context context) {
        savePref.deleteSharedPreferences(Config.TAG_USER_INFO, Config.TAG, context);
        savePref.deleteSharedPreferences(Config.TAG_USER_INFO_UN, Config.TAG, context);

    }

}
