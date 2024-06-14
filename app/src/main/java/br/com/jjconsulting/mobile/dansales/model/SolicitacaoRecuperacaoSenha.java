package br.com.jjconsulting.mobile.dansales.model;

public class SolicitacaoRecuperacaoSenha {

    private String user;
    private String dispositivoIMEI;
    private String appId;

    public SolicitacaoRecuperacaoSenha() {

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDispositivoIMEI() {
        return dispositivoIMEI;
    }

    public void setDispositivoIMEI(String dispositivoIMEI) {
        this.dispositivoIMEI = dispositivoIMEI;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
