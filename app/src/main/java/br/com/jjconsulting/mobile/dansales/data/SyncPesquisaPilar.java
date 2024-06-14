package br.com.jjconsulting.mobile.dansales.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import br.com.jjconsulting.mobile.dansales.BuildConfig;


public class SyncPesquisaPilar implements Parcelable{
    private float nota;
    private float peso;
    private String pilar;
    private String urlImg;
    private int status;
    private String informe;

    private boolean isShowHeader;

    private String textHeader;

    public SyncPesquisaPilar() {

    }

    protected SyncPesquisaPilar(Parcel in) {
        nota = in.readFloat();
        peso = in.readFloat();
        pilar = in.readString();
        urlImg = in.readString();
        status = in.readInt();
        informe = in.readString();
        isShowHeader = in.readByte() != 0;
        textHeader = in.readString();
    }

    public static final Creator<SyncPesquisaPilar> CREATOR = new Creator<SyncPesquisaPilar>() {
        @Override
        public SyncPesquisaPilar createFromParcel(Parcel in) {
            return new SyncPesquisaPilar(in);
        }

        @Override
        public SyncPesquisaPilar[] newArray(int size) {
            return new SyncPesquisaPilar[size];
        }
    };

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getPilar() {
        return pilar;
    }

    public void setPilar(String pilar) {
        this.pilar = pilar;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isShowHeader() {
        return isShowHeader;
    }

    public void setShowHeader(boolean showHeader) {
        isShowHeader = showHeader;
    }

    public String getTextHeader() {
        return textHeader;
    }

    public void setTextHeader(String textHeader) {
        this.textHeader = textHeader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.nota);
        parcel.writeFloat(this.peso);
        parcel.writeString(this.pilar);
        parcel.writeString(this.urlImg);
        parcel.writeInt(this.status);
        parcel.writeString(this.informe);

    }
}
