package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class CustomTextInputLayout extends TextInputLayout {

    private Object collapsingTextHelper;
    private Method setCollapsedTypefaceMethod;
    private Method setExpandedTypefaceMethod;

    public CustomTextInputLayout(Context context) {
        this(context, null);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            Field cthField = TextInputLayout.class
                    .getDeclaredField("mCollapsingTextHelper");
            cthField.setAccessible(true);
            collapsingTextHelper = cthField.get(this);

            setCollapsedTypefaceMethod = collapsingTextHelper
                    .getClass().getDeclaredMethod("setCollapsedTypeface", Typeface.class);
            setCollapsedTypefaceMethod.setAccessible(true);

            setExpandedTypefaceMethod = collapsingTextHelper
                    .getClass().getDeclaredMethod("setExpandedTypeface", Typeface.class);
            setExpandedTypefaceMethod.setAccessible(true);

            this.post(() -> {
                loadStyle();
            });


        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            collapsingTextHelper = null;
            setCollapsedTypefaceMethod = null;
            setExpandedTypefaceMethod = null;
            LogUser.log(Config.TAG, e.toString());
        }
    }

    public void setCollapsedTypeface(Typeface typeface) {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            setCollapsedTypefaceMethod.invoke(collapsingTextHelper, typeface);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LogUser.log(Config.TAG, e.toString());
        }
    }

    public void setExpandedTypeface(Typeface typeface) {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            setExpandedTypefaceMethod.invoke(collapsingTextHelper, typeface);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LogUser.log(Config.TAG, e.toString());
        }
    }

    public void setHintBold() {
        setCollapsedTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

    }

    public void setTypeColorHint() {
        getEditText().getBackground().clearColorFilter();
    }


    @Override
    public void onWindowFocusChanged(boolean value) {
        super.onWindowFocusChanged(value);
        loadStyle();
    }

    public void loadStyle() {
        setTypeColorHint();
        setHintBold();
    }


}