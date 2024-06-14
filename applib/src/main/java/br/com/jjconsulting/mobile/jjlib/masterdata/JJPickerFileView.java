package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.BasicAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.CustomAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class JJPickerFileView {

    private OnOpenIntent onOpenIntent;

    private Context mContext;

    private boolean isEnalbe;

    private int width;

    private List<BasicAction> actions;

    private FrameLayout containerTextInputLayout;

    private LinearLayout clickDefaultLinearLayout;

    private TextInputLayout textInputLayout;

    private String fieldName;

    private String fileName;

    private String fileBase64;


    public JJPickerFileView(Context context, OnOpenIntent onOpenIntent) {
        this.mContext = context;
        this.onOpenIntent = onOpenIntent;

        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }


    public View renderView() {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View layout = inflater.inflate(R.layout.jj_item_button_upload_view, null);

        textInputLayout = layout.findViewById(R.id.jj_button_text_input_layout);
        textInputLayout.setEnabled(false);
        containerTextInputLayout = layout.findViewById(R.id.jj_container_input_relative_layout);
        clickDefaultLinearLayout = layout.findViewById(R.id.click_default_linear_layout);

        LinearLayout containerJJIcon = layout.findViewById(R.id.container_jj_icon_view);

        int iconSize = 0;

        actions = new ArrayList<>();

        CustomAction action = new CustomAction();
        action.setDefaultOption(true);
        action.setIcon(TIcon.CLOUD_UPLOAD);
        action.setOrder(1);
        actions.add(action);

        width = ((Activity)mContext).getWindowManager().getDefaultDisplay().getWidth();

        if (actions != null) {

            int index = 0;

            for (BasicAction item : actions) {
                String color = String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff);
                JJIcon jjIcon = new JJIcon(mContext, item.getIcon(), color);
                jjIcon.setId(index);
                jjIcon.setOnClickListener(v-> {
                    ExFilePicker exFilePicker = new ExFilePicker();
                    exFilePicker.setCanChooseOnlyOneItem(true);
                    exFilePicker.setQuitButtonEnabled(true);
                    exFilePicker.start((Activity)mContext, ImageCameraGallery.OPEN_FILE);
                    onOpenIntent.onOpen(fieldName);
                });

                containerJJIcon.addView(jjIcon.renderView());
                iconSize += jjIcon.getWidth();
                index++;
            }
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((width) - iconSize, LinearLayout.LayoutParams.WRAP_CONTENT);
        containerTextInputLayout.setLayoutParams(params);

        containerTextInputLayout.setVisibility(View.VISIBLE);
        clickDefaultLinearLayout.setOnClickListener((v)-> {
            ExFilePicker exFilePicker = new ExFilePicker();
            exFilePicker.setQuitButtonEnabled(true);
            exFilePicker.setCanChooseOnlyOneItem(true);
            exFilePicker.start((Activity)mContext, ImageCameraGallery.OPEN_FILE);
            onOpenIntent.onOpen(fieldName);
        });

        return layout;
    }

    public void onActivityResult(ExFilePickerResult exFilePickerResult){
        String fileString[] = exFilePickerResult.getNames().get(0).split("/");
        fileName = fileString[fileString.length - 1];
        textInputLayout.getEditText().setText(fileName);
        File file = new File(exFilePickerResult.getPath() + "/" + fileName);

        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            fileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (FileNotFoundException e) {
            LogUser.log(Config.TAG, e.getMessage());
        } catch (IOException e) {
            LogUser.log(Config.TAG, e.getMessage());
        }

    }

    public boolean isEnable() {
        return isEnalbe;
    }

    public void setEnable(boolean enable) {
        isEnalbe = enable;
        textInputLayout.getEditText().setEnabled(enable);
        clickDefaultLinearLayout.setVisibility(enable? View.GONE: View.VISIBLE);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setText(String name) {
        textInputLayout.getEditText().setText(name);
    }

    public interface OnOpenIntent{
        void onOpen(String filedName);
    }
}

