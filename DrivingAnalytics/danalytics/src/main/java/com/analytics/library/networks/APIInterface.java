package com.analytics.library.networks;

import com.analytics.library.constants.NetworkConstants;
import com.analytics.library.model.CommonResponse;
import com.analytics.library.model.UserLoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIInterface {
    @FormUrlEncoded
    @POST
    Call<UserLoginResponse> loginUser(
            @Url String url,
            @Field(NetworkConstants.loginData) String data);

    @FormUrlEncoded
    @POST
    Call<CommonResponse> storeCapturedData(
            @Url String url,
            @Field(NetworkConstants.captureData) String data);


}
