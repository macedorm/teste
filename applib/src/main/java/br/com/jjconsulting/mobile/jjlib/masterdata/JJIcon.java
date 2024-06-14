package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.IconHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJIcon {

    private Context mContext;
    private TIcon mIcon;
    private String mColor;
    private View.OnClickListener onClickListener;

    private int fontSizeDefault = 18;

    private boolean isLargeMargin;

    public JJIcon(Context context, TIcon icon, String color) {
        this.mContext = context;
        this.mIcon = icon;
        this.mColor = color;
        size = (int)mContext.getResources().getDimension(R.dimen.size_icon);

        TypefaceProvider.registerDefaultIconSets();
    }

    private int size;
    private int id = -99;

    public View renderView(int fontSize) {
        return  createJJIcon(fontSize);
    }

    public View renderView() {
        return createJJIcon(fontSizeDefault);
    }

    public View createJJIcon(int fontSize){
        AwesomeTextView view = null;

        try {
            view = new AwesomeTextView(mContext);

            //Cria icone legenda
            LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsImage.setMargins((int)mContext.getResources().getDimension(R.dimen.margin_icon), 0, (int)mContext.getResources().getDimension(R.dimen.margin_icon), 0);
            view.setLayoutParams(paramsImage);
            view.setVisibility(View.GONE);
            view.setFontAwesomeIcon(IconHelper.getName(mIcon));
            view.setTextColor(Color.parseColor(mColor));
            view.setTextSize(fontSize);


            view.setClickable(true);

            if(isLargeMargin){
                view.setPadding(30,30,30,30);
            }

            if(onClickListener != null) {
                view.setOnClickListener((v) -> {
                    onClickListener.onClick(v);
                });
            } else {
                view.setClickable(false);
            }

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        if(id != - 99){
            view.setId(id);
        }

        return view;

    }

    public int getWidth(){
        return (int)(size * 1.5);
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLargeMargin() {
        return isLargeMargin;
    }

    public void setLargeMargin(boolean largeMargin) {
        isLargeMargin = largeMargin;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}

