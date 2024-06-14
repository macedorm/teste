package br.com.jjconsulting.mobile.jjlib.dao;


import br.com.jjconsulting.mobile.jjlib.OnInsertProgress;

public class CurrentProcess {

    private int sizeUpdateScreen;

    public CurrentProcess(){
        this.sizeUpdateScreen = 499;
    }

    private OnInsertProgress onInsertProgress;

    public int getSizeUpdateScreen() {
        return sizeUpdateScreen;
    }

    public void setSizeUpdateScreen(int sizeUpdateScreen) {
        this.sizeUpdateScreen = sizeUpdateScreen;
    }

    public OnInsertProgress getOnInsertProgress() {
        return onInsertProgress;
    }

    public void setOnInsertProgress(OnInsertProgress onInsertProgress) {
        this.onInsertProgress = onInsertProgress;
    }

    public void setProgress(int itens) {
        onInsertProgress.onProgress(itens);
    }
}
