package br.com.jjconsulting.mobile.jjlib;

public interface OnUpdateChangeScreen {

    void onPreparation();
    void onStart();
    void onUpdateNotAvailabe();
    void onFinish(int totalRow, long timeTotalUpdate);
    void onError(String message);
    void onErrorConnection();
    void onCancel();
    void onProgress(int totalRow, int currentRow, int currentProgress);
    void onProgressStatus(String message);

}
