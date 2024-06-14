package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskLayout;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncCRReportConnection;
import br.com.jjconsulting.mobile.dansales.model.CRReport;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.RetProcess;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class CRReportFragment extends BaseFragment implements View.OnClickListener, AsyncTaskLayout.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskLayout mAsyncTaskLayout;

    private ImageCameraGallery imageCameraGallery;
    private List<Layout> mLayoutList;
    private Layout mCurrentLayout;

    private TextView currentLayoutTextView;
    private ImageView mFotoImageView;
    private SpinnerArrayAdapter<Layout> mLayoutSpinnerAdapter;
    private Spinner mLayoutSpinner;

    private Button cancelButton;
    private Button reportButton;

    private ViewGroup mLoadingLinearLayout;
    private ViewGroup mCRReportLinearLayout;

    private Bitmap bitmap;

    private String extension;
    private String nameFile;

    public CRReportFragment() {
        imageCameraGallery = new ImageCameraGallery();
    }

    public static CRReportFragment newInstance(Layout layout) {
        CRReportFragment crDetailFragment = new CRReportFragment();
        crDetailFragment.setLayout(layout);
        return crDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_layout_report));

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_cr_report, container, false);

        currentLayoutTextView = view.findViewById(R.id.current_layout_text_view);
        mLayoutSpinner = view.findViewById(R.id.new_layout_spinner);
        mLoadingLinearLayout = view.findViewById(R.id.loading_linear_layout);
        mCRReportLinearLayout = view.findViewById(R.id.cr_report_linear_layout);
        mFotoImageView = view.findViewById(R.id.report_image_view);

        cancelButton = view.findViewById(R.id.cancel_button);
        reportButton = view.findViewById(R.id.report_button);

        mFotoImageView.setOnClickListener(this);
        reportButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


        if(mCurrentLayout != null){
            currentLayoutTextView.setText(mCurrentLayout.getNome());
        }

        showLoading(true);
        loadLayout();

        Layout layout = getLayout();

        if(layout == null || layout.getCodigo() == null){
            getActivity().finish();
            return view;
        }

        nameFile = "report_" + layout.getCodigo() + "_" + layout.getCodCliente() + ".jpg";

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void processFinish(Object[] objects) {
        showLoading(false);
        mLayoutList = (List<Layout>) objects[0];
        setupLayoutSpinner(mLayoutList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_button:

                String messageError = null;

                if(bitmap == null){
                    messageError = getString(R.string.message_report_error_bitmap);
                }

                if(mLayoutSpinner.getSelectedItemPosition() == 0){
                    messageError = getString(R.string.message_report_error_layout);
                }

                if(TextUtils.isNullOrEmpty(messageError)){
                    Date date = new Date();
                    CRReport crReport = new CRReport();
                    crReport.setCod_cliente(getLayout().getCodCliente());
                    crReport.setSales_organization(Current.getInstance(getContext()).getUnidadeNegocio().getCodigo());
                    crReport.setBase64_arquivo(ImageCameraGallery.convertBase64(bitmap));
                    crReport.setNome_arquivo(nameFile);
                    crReport.setLayout_old(getLayout().getCodigo());
                    crReport.setLayout_new(((Layout)mLayoutSpinner.getSelectedItem()).getCodigo());
                    crReport.setCod_reg_func(Current.getInstance(getContext()).getUsuario().getCodigo());
                    crReport.setDt_ult_alt(FormatUtils.toTextToCompareDateInSQlite(date));
                    crReport.setDt_inc(FormatUtils.toTextToCompareDateInSQlite(date));

                    SyncCRReportConnection syncCRReportConnection = new SyncCRReportConnection(getContext(), new BaseConnection.ConnectionListener() {
                        @Override
                        public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> list) {
                            if(!getActivity().isFinishing() && getContext() != null) {

                                showLoading(false);
                                try {
                                    ArrayList<ValidationLetter> validationLetters = gson.fromJson(response, new TypeToken<List<ValidationLetter>>() {
                                    }.getType());

                                    if (validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS) {
                                        dialogsDefault.showDialogMessage(
                                                getString(R.string.message_report_success),
                                                dialogsDefault.DIALOG_TYPE_SUCESS,
                                                new DialogsCustom.OnClickDialogMessage() {
                                                    @Override
                                                    public void onClick() {
                                                        getActivity().finish();
                                                    }
                                                });
                                    } else {
                                        dialogsDefault.showDialogMessage(
                                                validationLetters.get(0).getMessage(),
                                                dialogsDefault.DIALOG_TYPE_ERROR,
                                                null);
                                    }
                                } catch (Exception ex) {
                                    dialogsDefault.showDialogMessage(
                                            getString(R.string.report_sync_error_connection),
                                            dialogsDefault.DIALOG_TYPE_ERROR,
                                            null);
                                }

                            }
                        }

                        @Override
                        public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                            if (!getActivity().isFinishing() && getContext() != null) {

                                showLoading(false);
                                ValidationLetter errorConnection;

                                if (code == Connection.AUTH_FAILURE) {
                                    errorConnection = gson.fromJson(response, ValidationLetter.class);
                                    if (ManagerSystemUpdate.isRequiredUpadate(getContext(), errorConnection.getMessage())) {
                                        return;
                                    }

                                    dialogsDefault.showDialogMessage(errorConnection.getMessage(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                                    return;
                                }

                                if (code == Connection.SERVER_ERROR) {
                                    try {
                                        errorConnection = gson.fromJson(response, ValidationLetter.class);
                                        if (errorConnection != null) {
                                            showMessageError(errorConnection.getMessage());
                                            return;
                                        }
                                    } catch (Exception ex) {
                                        LogUser.log(ex.toString());
                                    }
                                }

                                dialogsDefault.showDialogMessage(
                                        getString(R.string.pesquisa_sync_error_connection),
                                        dialogsDefault.DIALOG_TYPE_ERROR,
                                        null);
                            }
                        }
                    });

                    showLoading(true);
                    syncCRReportConnection.syncReport(crReport);


                } else {
                    dialogsDefault.showDialogMessage(messageError, dialogsDefault.DIALOG_TYPE_ERROR, null);
                }

                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.report_image_view:
                openCamera();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.startActivityForResult(data, requestCode);
        } catch (Exception ex){}

        if(ImageCameraGallery.OPEN_CAMERA == requestCode) {
            String path = null;

            if (data != null) {
                Uri uriImage = data.getData();
                if (uriImage == null) {
                    try {
                        uriImage = imageCameraGallery.getOutPutfileUri();
                        path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());

                        bitmap = BitmapFactory.decodeFile(path);
                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }

                } else {
                    try {
                        path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());
                        bitmap = BitmapFactory.decodeFile(path);

                    } catch (Exception e) {
                        LogUser.log(Config.TAG, e.toString());
                    }
                }



            } else {
                Uri uriImage = imageCameraGallery.getOutPutfileUri();
                try {
                    path = imageCameraGallery.getRealPathFromURI(uriImage, getActivity());
                    bitmap = BitmapFactory.decodeFile(path);
                } catch (Exception e) {
                    LogUser.log(Config.TAG, e.toString());
                }

            }

            if (bitmap != null) {
                bitmap = imageCameraGallery.imageOrientationValidator(bitmap, path);
                mFotoImageView.setImageBitmap(bitmap);
            }

            extension = path.substring(path.lastIndexOf(".") + 1);

        }

    }


    private void loadLayout() {
        if (mAsyncTaskLayout != null) {
            mAsyncTaskLayout.cancel(true);
        }

        mAsyncTaskLayout = new AsyncTaskLayout(getActivity(), this);
        mAsyncTaskLayout.execute();
    }

    private void showLoading(boolean value){
        mLoadingLinearLayout.setVisibility(value ? View.VISIBLE:View.GONE);
        mCRReportLinearLayout.setVisibility(value ? View.GONE:View.VISIBLE);
    }


    private void setupLayoutSpinner(List<Layout> list) {

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(list.toArray(),
                getString(R.string.select_layout));

        mLayoutSpinnerAdapter = new SpinnerArrayAdapter<Layout>(
                getContext(), objects, true) {
            @Override
            public String getItemDescription(Layout item) {
                return item.getNome();
            }
        };

        mLayoutSpinner.setAdapter(mLayoutSpinnerAdapter);
        mLayoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void openCamera() {
        startActivityForResult(imageCameraGallery.getPhotoIntent(getString(R.string.title_intent_photo), nameFile,  getActivity()), ImageCameraGallery.OPEN_CAMERA);
    }

    public Layout getLayout() {
        return mCurrentLayout;
    }

    public void setLayout(Layout layout) {
        this.mCurrentLayout = layout;
    }

}
