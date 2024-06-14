package br.com.jjconsulting.mobile.jjlib.masterdata;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public abstract class JJBaseFragment extends Fragment {

    public DialogsCustom dialogCustom;
    public Gson gson;

    public JJBaseFragment(){

        gson = new GsonBuilder().
                registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {

                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if(src == src.longValue())
                            return new JsonPrimitive(src.longValue());
                        return new JsonPrimitive(src);
                    }
                }).create();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dialogCustom = new DialogsCustom(getActivity());

    }
}

