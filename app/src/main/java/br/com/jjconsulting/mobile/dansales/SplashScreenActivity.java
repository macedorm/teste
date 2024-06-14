package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import br.com.jjconsulting.mobile.dansales.kotlin.UpdateUser;
import br.com.jjconsulting.mobile.dansales.service.MyFirebaseMessagingService;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JJSDK.initializeAuthConnection(this, BuildConfig.URL_API, BuildConfig.USER, BuildConfig.USERKEY, BuildConfig.DATABASE_NAME, BuildConfig.DATABASE_VERSION);

        FirebaseApp.initializeApp(this);
        FirebaseUtils.init();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().get(key).toString();
                Log.d(Config.TAG, "Key: " + key + " Value: " + value);
            }
        }

        MyFirebaseMessagingService.pushBackground(this, getIntent().getExtras());

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }
}
