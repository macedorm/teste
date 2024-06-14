package br.com.jjconsulting.mobile.dansales;

import androidx.core.content.FileProvider;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SystemUpdateActivity extends BaseActivity {

    private ProgressDialog bar;

    private boolean storeVersion;
    private String nameFile = "dansales.apk";
    private String path;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_update);

        TextView versionTextView = findViewById(R.id.update_version_text_view);
        TextView descriptionVersionTextView = findViewById(R.id.description_update_text_version);

        Button buttonUpdate = findViewById(R.id.update_button);

        userInfo.getUserInfo(this);

        versionTextView.setText(
                getString(R.string.new_app_version, userInfo.getUserInfo(this).getVersion()));

        try {
            storeVersion = HardwareUtil.isStoreVersion(this);
        } catch (Exception ex) {
            storeVersion = false;
        }

        if (storeVersion) {
            descriptionVersionTextView.setText(getString(R.string.update_play_store_title));
        } else {
            descriptionVersionTextView.setText(getString(R.string.update_apk_title));
        }

        buttonUpdate.setOnClickListener(view -> {
            if (storeVersion) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                finish();
            } else {
                new DownloadNewVersion().execute();
            }
        });
    }

    private void openNewVersion(String location) {
        try {
            installApp1(location);
        } catch (Exception ex1) {
            try {
                installApp2(); // Tentativa p/ algumas versões específicas do Android
            } catch (Exception ex2) {
                String error = ex1.toString() + " / " + ex2.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogsDefault.showDialogMessage(error, dialogsDefault.DIALOG_TYPE_ERROR,
                                null);
                    }
                });


            }
        }

        finish();
    }

    private void installApp1(String location) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        File file = new File(location, nameFile);
        Uri uri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".fileprovider", file);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void installApp2() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(outputFile),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar = new ProgressDialog(SystemUpdateActivity.this);
            bar.setCancelable(false);
            bar.setMessage(getString(R.string.update_apk_progress, "0%"));
            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);

            runOnUiThread(() -> {
                if (getWindow().getDecorView().isShown()) {
                    bar.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag;

            try {
                URL url = new URL(JJSDK.getHost(SystemUpdateActivity.this)
                        + CustomAPI.API_DOWNLOAD_APK);


                LogUser.log(Config.TAG, "URL APK: " + url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);
                conn.connect();

                path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS) + "/Dansales/";
                File file = new File(path);
                file.mkdirs();

                outputFile = new File(file, nameFile);

                if (outputFile.exists()) {
                    outputFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = conn.getInputStream();

                byte[] buffer = new byte[1024];
                int downloaded = 0;
                int len1, per;

                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = (downloaded * 100 / BuildConfig.SIZEAPK);
                    publishProgress(per);
                }

                fos.close();
                is.close();

                flag = true;
            } catch (Exception e) {
                flag = false;
                LogUser.log(Config.TAG, "Update Error: " + e.getMessage());

                try {
                    runOnUiThread(() -> {
                        dialogsDefault.showDialogMessage(getString(R.string.update_apk_error),
                                dialogsDefault.DIALOG_TYPE_ERROR, null);
                    });
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }

            }

            return flag;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            String msg = progress[0] > 99 ? getString(R.string.update_apk_finishing)
                    : getString(R.string.update_apk_progress, progress[0] + "%");

            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            bar.setMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            bar.dismiss();

            if (result) {
                openNewVersion(path);
            } else {
                dialogsDefault.showDialogMessage(getString(R.string.update_apk_error),
                        dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        }
    }
}
