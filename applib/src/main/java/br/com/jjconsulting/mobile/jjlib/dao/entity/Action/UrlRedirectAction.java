package br.com.jjconsulting.mobile.jjlib.dao.entity.Action;

public class UrlRedirectAction extends BasicAction {
    private String urlRedirect;
    private boolean urlAsPopUp;
    private String titlePopUp;

    public UrlRedirectAction(){
        //setIcon(TIcon.EXTERNAL_LINK);
        setTitlePopUp("Título");
        setUrlAsPopUp(false);
    }

    public String getUrlRedirect() {
        return urlRedirect;
    }

    public void setUrlRedirect(String urlRedirect) {
        this.urlRedirect = urlRedirect;
    }

    public boolean isUrlAsPopUp() {
        return urlAsPopUp;
    }

    public void setUrlAsPopUp(boolean urlAsPopUp) {
        this.urlAsPopUp = urlAsPopUp;
    }

    public String getTitlePopUp() {
        return titlePopUp;
    }

    public void setTitlePopUp(String titlePopUp) {
        this.titlePopUp = titlePopUp;
    }
}
