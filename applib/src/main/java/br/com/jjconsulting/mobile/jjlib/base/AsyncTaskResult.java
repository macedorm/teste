package br.com.jjconsulting.mobile.jjlib.base;

public class AsyncTaskResult<T> {

    private T mResult;
    private Exception mError;

    public AsyncTaskResult(T result) {
        mResult = result;
    }

    public AsyncTaskResult(Exception error) {
        mError = error;
    }

    public T getResult() {
        return mResult;
    }

    public Exception getError() {
        return mError;
    }
}
