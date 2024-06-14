package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;

import br.com.jjconsulting.mobile.jjlib.util.SavePref;

public class JJURL {

    public static final String INDEX_URL_KEY = "index_url";


    public String[] getNameURLArray(Context context, String urlArray[]){
        String nameUrl[] = new String[urlArray.length];

        for(int ind = 0; ind < nameUrl.length; ind++){
            if(urlArray[ind].contains("|")){
                nameUrl[ind] = urlArray[ind].split("\\|")[0];
            }
        }

        return nameUrl;
    }

    public String getURL(Context context, String urlArray[]){
        String url = "";

        if(urlArray == null) {
            return url;
        }

        int index = getIndex(context);

        if(index >= urlArray.length) {
            return url;
        }

        url = urlArray[index];

        if(url.contains("|")){
            return url.split("\\|")[1];
        } else {
            return url;
        }
    }

    public int getIndex(Context context){
        int index = 0;

        SavePref savePref = new SavePref();
        String indexString = savePref.getPref(INDEX_URL_KEY, context.getPackageName(), context);

        if(indexString != null){
            index = Integer.parseInt(indexString);
        }

        return index;
    }

    public void setIndex(Context context, int index){
        SavePref savePref = new SavePref();
        savePref.saveSharedPreferences(INDEX_URL_KEY, context.getPackageName(), String.valueOf(index), context);
    }
}
