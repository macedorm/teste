package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public abstract class JJBaseView {

    public Gson gson;

    private boolean visisble;
    private String name;
    abstract protected View renderView();

    public JJBaseView() {
        gson = new GsonBuilder().
            registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    LogUser.log(Config.TAG, "ten");

                    if(src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }
            }).create();

    }

    public View getView(){
        return renderView();
    }

    public boolean isVisisble() {
        return visisble;
    }

    public void setVisisble(boolean visisble) {
        this.visisble = visisble;
    }

    public JJBaseView(String name) {
        this.name = name;
    }
}
