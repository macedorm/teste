package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class JJBarcodeScannerActivity extends Activity implements ZBarScannerView.ResultHandler {

    public static final int BARCODE_REQUEST = 99;
    public static final String DATA_KEY_BARCODE = "barcode";
    public static final String DATA_KEY_FIELD_NAME = "key_param_field_name";

    private ZBarScannerView mScannerView;

    private String fieldName;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        mScannerView.setFlash(true);
        setContentView(mScannerView);

        try{
            fieldName = getIntent().getExtras().getString(DATA_KEY_FIELD_NAME);
        } catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }
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
        mScannerView.resumeCameraPreview(this);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(DATA_KEY_BARCODE, rawResult.getContents());
        returnIntent.putExtra(DATA_KEY_FIELD_NAME, fieldName);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
