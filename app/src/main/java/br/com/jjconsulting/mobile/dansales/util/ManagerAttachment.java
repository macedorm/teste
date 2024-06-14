package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.File;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ManagerAttachment {

    private final String FILE_PROVIDER = ".fileprovider";

    private Context mContext;
    private DialogsCustom dialogsDefault;

    private ProgressBar progressBar;
    private Dialog dialogProgressPercent;

    private int downloadID;

    private boolean isOpenInternalImage;

    private String titleDetail;

    /**
     *
     * @param context
     * @param isOpenInternalImage
     */
    public ManagerAttachment(Context context, boolean isOpenInternalImage) {
        this.mContext = context;
        dialogsDefault = new DialogsCustom((Activity) context);
        this.isOpenInternalImage = isOpenInternalImage;
    }

    public ManagerAttachment(Context context) {
        this.mContext = context;
        dialogsDefault = new DialogsCustom((Activity) context);
        isOpenInternalImage = false;
    }


    /**
     * Verifica se arquivo existe, caso não efetua o download
     *
     * @param URL
     * @param filename
     * @param id
     */
    public void checkAndOpenFile(String URL, String filename, String id, Context context) {
        String path = context.getExternalFilesDir(null) + "/" + mContext.getString(R.string.app_name);
        String fileNameFinal = id + "_" + filename;

        File sdcard = new File(path);
        File file = new File(sdcard, fileNameFinal);

        if(isImage(fileNameFinal) &&  isOpenInternalImage){
            mContext.startActivity(DetailImage.newIntent(mContext, URL, titleDetail, "#000000", false));
            return;
        }

        if (file.exists()) {
                Uri uri = Uri.fromFile(file);
            openDownloadedAttachment(mContext, uri, getMimeType(uri.toString()));
        } else {

            progressBar = showDialogProgress(mContext.getString(R.string.message_attachment_title_progress, filename), new DialogsCustom.OnClickDialogMessage() {
                @Override
                public void onClick() {
                    try {
                        PRDownloader.cancel(downloadID);
                    }catch (Exception ex){
                        LogUser.log(Config.TAG, "Nao foi possivel cancelar o download");
                    }
                }
            });

            downloadID = PRDownloader.download(URL, path, fileNameFinal)
                .build()
                .setOnStartOrResumeListener(() -> {
                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {
                    try {
                        if(dialogsDefault != null) {
                            cancelDialogProgress();
                            dialogsDefault.showDialogMessage(mContext.getString(R.string.sync_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }
                    } catch (Exception ex) {
                        LogUser.log(ex.toString());
                    }

                })
                .setOnProgressListener((progress) -> {
                    try {
                        if (progressBar != null) {
                            int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                            progressBar.setProgress(percent);
                        }
                    } catch (Exception ex) {
                        LogUser.log(ex.toString());
                    }

                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        try {
                            if(dialogProgressPercent != null) {
                                cancelDialogProgress();
                            }
                            Uri uri = Uri.fromFile(file);
                            openDownloadedAttachment(mContext, uri, getMimeType(uri.toString()));
                        } catch (Exception ex) {
                            LogUser.log(ex.toString());
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        try {

                            if(dialogProgressPercent != null){
                                cancelDialogProgress();
                                dialogsDefault.showDialogMessage(mContext.getString(R.string.sync_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                            }

                        } catch (Exception ex) {
                            LogUser.log(ex.toString());
                        }
                    }
                });

        }
    }

    /**
     * Abre arquivo por Intent
     *
     * @param context
     * @param attachmentUri
     * @param attachmentMimeType
     */
    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if (attachmentUri != null) {

            Uri uri = null;
            File file = null;
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                file = new File(attachmentUri.getPath());
                uri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + FILE_PROVIDER, file);
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(uri, attachmentMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                dialogsDefault.showDialogMessage(mContext.getString(R.string.message_open_attachment_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }

        }
    }

    /**
     * Retorna extensão do arquivo pela URL
     *
     * @param url
     * @return
     */
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean isImage(String name){
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        } else {
            String extension = name.substring(lastIndexOf);

            switch (extension.toLowerCase()) {
                case ".png":
                case ".jpg":
                case ".jpeg":
                    return true;
                default:
                    return false;
            }
        }
    }


    /**
     * Retorna o background usado na lista de anexos de acordo com a extensao do arquvio
     *
     * @param name
     * @return
     */
    public static Drawable getBackgroundFileExtension(Context context, String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null;
        } else {
            String extension = name.substring(lastIndexOf);

            switch (extension.toLowerCase()){
                case ".mp4":
                case ".3gp":
                    return context.getResources().getDrawable(R.drawable.attachment_background_video);
                case ".pdf":
                    return context.getResources().getDrawable(R.drawable.attachment_background_pdf);
                case ".png":
                case ".jpg":
                case ".jpeg":
                    return context.getResources().getDrawable(R.drawable.attachment_background_image);
                case ".doc":
                case ".docx":
                    return context.getResources().getDrawable(R.drawable.attachment_background_docs);
                case ".xls":
                case ".xlsx":
                    return  context.getResources().getDrawable(R.drawable.attachment_background_sheets);
                 default:
                     return null;
            }
        }
    }

    public String getTitleDetail() {
        return titleDetail;
    }

    public void setTitleDetail(String titleDetail) {
        this.titleDetail = titleDetail;
    }


    // Dialog para mensagens de aviso ao usuário
    public ProgressBar showDialogProgress(String message, DialogsCustom.OnClickDialogMessage onClickDialogMessage) {

        ProgressBar progressBar = null;

        try {

            dialogProgressPercent = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
            dialogProgressPercent.setCancelable(false);
            dialogProgressPercent.setContentView(R.layout.dialog_progress);

            progressBar = dialogProgressPercent.findViewById(R.id.dialog_progress);

            TextView mMessageDialogTextView = dialogProgressPercent.findViewById(br.com.jjconsulting.mobile.jjlib.R.id.tv_dialog_message);
            mMessageDialogTextView.setText(message);

            ImageView icon = dialogProgressPercent.findViewById(br.com.jjconsulting.mobile.jjlib.R.id.icon_image_view);
            icon.setColorFilter(mContext.getResources().getColor(br.com.jjconsulting.mobile.jjlib.R.color.alertCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
            icon.setImageDrawable(mContext.getResources().getDrawable(br.com.jjconsulting.mobile.jjlib.R.drawable.ic_error_black_24dp));

            TextView tvOk = dialogProgressPercent.findViewById(R.id.ok_cancel);
            tvOk.setOnClickListener(view -> {
                dialogProgressPercent.dismiss();
                if (onClickDialogMessage != null) {
                    onClickDialogMessage.onClick();
                }
            });

            dialogProgressPercent.show();

            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.jjlib.util.Config.TAG, ex.toString());
        }



        return progressBar;
    }

    public void cancelDialogProgress(){
        dialogProgressPercent.dismiss();
    }
}
