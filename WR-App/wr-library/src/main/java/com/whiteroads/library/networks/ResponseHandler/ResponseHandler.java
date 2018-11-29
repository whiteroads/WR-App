package com.whiteroads.library.networks.ResponseHandler;


import retrofit2.Response;

public interface ResponseHandler {

    void onSuccess(Response response);

    void onFailure(Throwable t);
}
