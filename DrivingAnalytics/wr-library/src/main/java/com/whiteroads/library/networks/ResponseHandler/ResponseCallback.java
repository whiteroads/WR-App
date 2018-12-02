package com.whiteroads.library.networks.ResponseHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponseCallback implements Callback {

    private ResponseHandler responseHandler;

    public ResponseCallback(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void onResponse(Call call, Response response) {
        responseHandler.onSuccess(response);
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        responseHandler.onFailure(t);
    }
}
