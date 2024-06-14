package br.com.jjconsulting.mobile.jjlib;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SuperActivity extends AppCompatActivity {

    @Override
    public void startActivity(Intent intent) {
        if (BuildConfig.DEBUG) {
            super.startActivity(intent);
            return;
        }

        if (intent == null) {
            super.startActivity(intent);
            return;
        }

        if (intent.getComponent() == null) {
            super.startActivity(intent);
            return;
        }

        try {
            Class target = Class.forName(intent.getComponent().getClassName());
            for (Class c : target.getInterfaces()) {
                if (c.equals(UnderDevelopment.class)) {
                    super.startActivity(UnderDevelopmentActivity.newIntent(this));
                    return;
                }
            }
        } catch (ClassNotFoundException classNotFoundEx) {
            super.startActivity(intent);
        }

        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (BuildConfig.DEBUG) {
            super.startActivity(intent, options);
            return;
        }

        if (intent == null) {
            super.startActivity(intent, options);
            return;
        }

        if (intent.getComponent() == null) {
            super.startActivity(intent, options);
            return;
        }

        try {
            Class target = Class.forName(intent.getComponent().getClassName());
            for (Class c : target.getInterfaces()) {
                if (c.equals(UnderDevelopment.class)) {
                    super.startActivity(
                            UnderDevelopmentActivity.newIntent(this), options);
                    return;
                }
            }
        } catch (ClassNotFoundException classNotFoundEx) {
            super.startActivity(intent);
        }

        super.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (BuildConfig.DEBUG) {
            super.startActivityForResult(intent, requestCode);
            return;
        }

        if (intent == null) {
            super.startActivityForResult(intent, requestCode);
            return;
        }

        if (intent.getComponent() == null) {
            super.startActivityForResult(intent, requestCode);
            return;
        }

        try {
            Class target = Class.forName(intent.getComponent().getClassName());
            for (Class c : target.getInterfaces()) {
                if (c.equals(UnderDevelopment.class)) {
                    super.startActivityForResult(
                            UnderDevelopmentActivity.newIntent(this), requestCode);
                    return;
                }
            }
        } catch (ClassNotFoundException classNotFoundEx) {
            super.startActivity(intent);
        }

        super.startActivityForResult(intent, requestCode);
    }
}
