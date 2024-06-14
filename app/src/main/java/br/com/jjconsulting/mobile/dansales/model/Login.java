package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;


public class    Login {

    @SerializedName("errorId")
    private int idRetorno;
    @SerializedName("isValid")
    private boolean isValid;
    @SerializedName("message")
    private String message;
    @SerializedName("link")
    private String link;
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private String userId;
    @SerializedName("version")
    private String version;
    @SerializedName("enableMobile")
    private boolean enableMobile;

    private String user;
    private String password;

    public int getIdRetorno() {
        return idRetorno;
    }

    public void setIdRetorno(int idRetorno) {
        this.idRetorno = idRetorno;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isEnableMobile() {
        return enableMobile;
    }

    public void setEnableMobile(boolean enableMobile) {
        this.enableMobile = enableMobile;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
