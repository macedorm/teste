package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaOrigem implements Serializable {
	private double latCheckin;
	private double longCheckin;
	private boolean inRadius;

    public double getLatCheckin() {
        return latCheckin;
    }

    public void setLatCheckin(double latCheckin) {
        this.latCheckin = latCheckin;
    }

    public double getLongCheckin() {
        return longCheckin;
    }

    public void setLongCheckin(double longCheckin) {
        this.longCheckin = longCheckin;
    }

    public boolean isInRadius() {
        return inRadius;
    }

    public void setInRadius(boolean inRadius) {
        this.inRadius = inRadius;
    }
}
