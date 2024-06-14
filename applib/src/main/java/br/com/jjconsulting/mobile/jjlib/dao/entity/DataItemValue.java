package br.com.jjconsulting.mobile.jjlib.dao.entity;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataItemValue {
    @SerializedName("id")
    public String id;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public int icon;
    @SerializedName("imagecolor")
    public String imageColor;

    public DataItemValue() { }

    public DataItemValue(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public DataItemValue(String id, String description, int icon, String imageColor) {
        this.id = id;
        this.description = description;
        this.icon = icon;
        this.imageColor = imageColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getImageColor() {
        return imageColor;
    }

    public void setImageColor(String imageColor) {
        this.imageColor = imageColor;
    }

    public static DataItemValue getContainsDataItemValue(ArrayList<DataItemValue> dataItem, String value){

       DataItemValue currentDataItemValue = null;

        for (DataItemValue item: dataItem) {
            if(item.id.equals(value)){
                currentDataItemValue =  item;
                break;
            }
        }

        return  currentDataItemValue;
    }



}