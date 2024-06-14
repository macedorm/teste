package br.com.jjconsulting.mobile.dansales.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import br.com.jjconsulting.mobile.dansales.LoginActivity;
import br.com.jjconsulting.mobile.dansales.database.UnidadeNegocioDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

/**
 * Holds and manages the references of the logged user (usuário) and unidade de negócio.
 */
public class Current  {

    public static String REQUEST_SESSION_EXPIRED = "request_session_expired";
    private static Current current;
    private Usuario usuario;
    private UnidadeNegocio unidadeNegocio;

    public static Current getInstance(Context context) {
        if(current == null || current.usuario == null) {
            UserInfo userInfo = new UserInfo();
            String codUser = userInfo.getUserId(context);
            String codUn = userInfo.getUserUnidadeNegocioSelected(context);
            if (!TextUtils.isNullOrEmpty(codUser) && !TextUtils.isNullOrEmpty(codUn)) {
                Usuario usuario = new UsuarioDao(context).get(codUser);
                UnidadeNegocio unNeg = new UnidadeNegocioDao(context).get(codUser, codUn);
                if (usuario != null && unNeg != null){
                    current = new Current();
                    current.usuario =  usuario;
                    current.unidadeNegocio = unNeg;
                }
            }

            if (current == null || current.usuario == null || current.unidadeNegocio == null) {
                Intent loginIntent = new Intent(context, LoginActivity.class);
                loginIntent.putExtra(REQUEST_SESSION_EXPIRED, true);
                Activity a = (Activity)context;
                a.startActivity(loginIntent);
                a.finish();
            }

        }

        return current;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public UnidadeNegocio getUnidadeNegocio() {
        return unidadeNegocio;
    }

    public static void setValues(Usuario usuario, UnidadeNegocio unidadeNegocio) {
        current = new Current();
        current.usuario = usuario;
        current.unidadeNegocio = unidadeNegocio;
    }

    public static void clear(Context context) {
        if(current != null) {
            current.usuario = null;
            current. unidadeNegocio = null;
        }
        current = null;
        UserInfo userInfo = new UserInfo();
        userInfo.deleteUserInfo(context);
    }

}
