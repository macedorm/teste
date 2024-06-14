package br.com.jjconsulting.mobile.dansales.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class DetailImage extends BaseActivity {

    private final static String KEY_URL_IMAGE = "key_url_image";
    private final static String KEY_TITLE = "key_title";
    private final static String KEY_BACKGROUND = "key_background";
    private final static String KEY_ROTATION = "key_rotation";


    public static Intent newIntent(Context context, String urlImage) {
        Intent intent = new Intent(context, DetailImage.class);
        intent.putExtra(KEY_URL_IMAGE, urlImage);
        intent.putExtra(KEY_ROTATION, false);

        return intent;
    }

    public static Intent newIntent(Context context, String urlImage, String title, String background, boolean isLockRotation) {
        Intent intent = new Intent(context, DetailImage.class);
        intent.putExtra(KEY_URL_IMAGE, urlImage);

        if(!TextUtils.isNullOrEmpty(title)){
            intent.putExtra(KEY_TITLE, title);
        }

        if(!TextUtils.isNullOrEmpty(background)){
            intent.putExtra(KEY_BACKGROUND, background);
        }

        intent.putExtra(KEY_ROTATION, isLockRotation);

        return intent;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr_detail_image);

        getSupportActionBar().hide();
        String url = getIntent().getStringExtra(KEY_URL_IMAGE);

        ImageView imageView = findViewById(R.id.layout_photo_view);
        RelativeLayout backgroundRelativeLayout = findViewById(R.id.background_image_relative_layout);

        PicassoCustom.setImage(this, url, imageView);

        try{
            if(getIntent().getExtras() != null) {

                if(getIntent().getExtras().getBoolean(KEY_ROTATION))
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

                if(getIntent().getExtras().containsKey(KEY_TITLE)) {
                    String title = getIntent().getStringExtra(KEY_TITLE);

                    if (!TextUtils.isNullOrEmpty(title)) {
                        getSupportActionBar().setTitle(title);
                        getSupportActionBar().show();
                    }
                }

                if(getIntent().getExtras().containsKey(KEY_BACKGROUND)) {
                    String background = getIntent().getStringExtra(KEY_BACKGROUND);
                    if (!TextUtils.isNullOrEmpty(background)) {
                        backgroundRelativeLayout.setBackgroundColor(Color.parseColor(background));
                    }
                }

            }
        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }

    }

}
