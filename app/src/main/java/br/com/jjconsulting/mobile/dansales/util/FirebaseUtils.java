package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import br.com.jjconsulting.mobile.dansales.BuildConfig;

public class FirebaseUtils {

    /**
     * Inicializa Firebase utils
     */
    public static void init(){
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(BuildConfig.DEBUG ? false:true);
    }

    /**
     * Defini o usuário para sessão do crashlictis
     * @param codigo
     * @param email
     * @param name
     */
    public static void setUser(String codigo, String email, String name){
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setUserId(codigo);
        firebaseCrashlytics.setCustomKey("email", email);
        firebaseCrashlytics.setCustomKey("name", name);
    }

    /**
     * Gera eventos para o analitycs
     * @param context
     * @param event
     * @param bundle
     */
    public static void sendEvent(Context context, String event, Bundle bundle){
        if (!BuildConfig.DEBUG) {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            mFirebaseAnalytics.logEvent(event, bundle);
        }
    }
}
