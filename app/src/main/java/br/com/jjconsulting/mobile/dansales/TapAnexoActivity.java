package br.com.jjconsulting.mobile.dansales;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class TapAnexoActivity extends BaseActivity {

    private static final String ARG_URL_TAP = "id_tap";

    public WebView webView;

    private Activity context;

    public ValueCallback mUploadMessage;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, TapAnexoActivity.class);
        intent.putExtra(ARG_URL_TAP, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tap_anexo);

        setTitle(getString(R.string.title_tap_anexo));

        context = this;
        String url = getIntent().getStringExtra(ARG_URL_TAP);

//        ChromeView chromeView = (ChromeView)findViewById(R.id.gameUiView);


        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        webView.loadUrl(url);

        webView.setWebChromeClient(new CustomWebChromeClient());

        webView.setWebViewClient(new WebClient(webView  ));
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class WebClient extends WebViewClient {
        private WebView webView;

        public WebClient(WebView webView){
            this.webView = webView;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            final Uri uri = request.getUrl();
            handleError(view, error.getErrorCode(), error.getDescription().toString(), uri);
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            final Uri uri = Uri.parse(failingUrl);
            handleError(view, errorCode, description, uri);
        }

        private void handleError(WebView view, int errorCode, String description, final Uri uri) {
            final String host = uri.getHost();
            final String scheme = uri.getScheme();
            dialogsDefault.showDialogMessage(getString(R.string.etap_error_connection_anexo), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                @Override
                public void onClick() {
                    finish();
                }
            });
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            webview.loadUrl(url);
            return false;
        }
    }

    protected class CustomWebChromeClient extends WebChromeClient
    {
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType )
        {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            context.startActivityForResult( Intent.createChooser( i, "File Chooser" ), 99 );
        }

        // For Android < 3.0
        public void openFileChooser( ValueCallback<Uri> uploadMsg )
        {
            openFileChooser( uploadMsg, "" );
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        // Fo
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            TapAnexoActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"), 99);
        }

        //For Android 4.1+ only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Browser"), 99);
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), 99);
        }

        // For Lollipop 5.0+ Devices
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }

            mUploadMessage = filePathCallback;
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = fileChooserParams.createIntent();
            }
            try {
                startActivityForResult(intent, 100);
            } catch (ActivityNotFoundException e) {
                mUploadMessage = null;
                Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }
}
