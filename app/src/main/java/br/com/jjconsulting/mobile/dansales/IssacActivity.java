package br.com.jjconsulting.mobile.dansales;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import java.util.Map;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class IssacActivity extends BaseActivity {

    private Map<String, String> headers = new HashMap<>();

    private boolean error;

    private String URL;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, IssacActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issac);

        setTitle(getString(R.string.title_chat));

        URL = BuildConfig.URL_SITE + CustomAPI.API_ISSAC;

        LogUser.log(URL);

        WebView issacWebView = findViewById(R.id.issacWebView);

        WebSettings webSettings = issacWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");


        issacWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(URL.equals(url)){
                    view.loadUrl(url, headers);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                issacWebView.setVisibility(error ? View.GONE:View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCodet, String description, String url){
                //Your code to do
                error = true;
                dialogsDefault.showDialogQuestion(getString(R.string.issac_error_connection), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        error = false;
                        issacWebView.loadUrl(url, headers );

                    }

                    @Override
                    public void onClickNegative() {
                        finish();
                    }
                });
            }
        });

        userInfo.getUserInfo(this);
        headers.put("Token",  userInfo.getUserInfo(this).getToken());

        issacWebView.loadUrl(URL, headers);
    }

    @Override
    public void onBackPressed() {

        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

        int sizeStack =  am.getRunningTasks(2).size();
        boolean isContain = false;

        for(int i = 0;i < sizeStack;i++){
            ComponentName cn = am.getRunningTasks(2).get(i).topActivity;

            if(cn.getClassName().contains("MasterActivity")){
                isContain = true;
            }
        }

        if(!isContain){
            startActivity(new Intent(this, MasterActivity.class));
        } else {
            moveTaskToBack(true);
        }
    }
}
