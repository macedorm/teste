package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarcodeScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    public static final String KEY_RESULT_BARCODE = "barcode";

    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        LogUser.log(Config.TAG, rawResult.getContents());
        LogUser.log(Config.TAG, rawResult.getBarcodeFormat().getName());
        mScannerView.resumeCameraPreview(this);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_RESULT_BARCODE, rawResult.getContents());
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
