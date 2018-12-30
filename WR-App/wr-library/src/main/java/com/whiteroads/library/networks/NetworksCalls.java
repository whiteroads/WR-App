package com.whiteroads.library.networks;

import android.content.Context;

import com.whiteroads.library.StartService;
import com.whiteroads.library.constants.EventBusContext;
import com.whiteroads.library.constants.NetworkConstants;
import com.whiteroads.library.data.LocationDataWrapper;
import com.whiteroads.library.data.SensorsDataWrapper;
import com.whiteroads.library.data.UserDataWrapper;
import com.whiteroads.library.model.CommonResponse;
import com.whiteroads.library.model.UserLoginResponse;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworksCalls {
    private Context context;

    public NetworksCalls(Context context) {
        this.context = context;
    }

    public void storeCapturedData(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA;
            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Call<CommonResponse> call = service.storeCapturedData(url, capturedData);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        SensorsDataWrapper.getInstance(context).removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeCapturedDataLocations(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA_LOCATIONS;
            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Call<CommonResponse> call = service.storeCapturedData(url, capturedData);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        LocationDataWrapper.getInstance(context).removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeCapturedDataSteps(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA_STEPS;
            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Call<CommonResponse> call = service.storeCapturedData(url, capturedData);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        SensorsDataWrapper.getInstance(context).removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loginUser(String data, final EventBus eventBus) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.LOGIN_USER;
            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Call<UserLoginResponse> call = service.loginUser(url, data);
            call.enqueue(new Callback<UserLoginResponse>() {
                @Override
                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                    UserLoginResponse commonResponse = (UserLoginResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        UserDataWrapper.getInstance(context).saveUserEmail(commonResponse.getUserEmail());
                        UserDataWrapper.getInstance(context).saveUserId(commonResponse.getUserId());
                        UserDataWrapper.getInstance(context).saveVehicleNumber(commonResponse.getVehicleNumber());
                        UserDataWrapper.getInstance(context).saveFrequency(commonResponse.getFrequency());
                        UserDataWrapper.getInstance(context).saveUploadTimeInMins(commonResponse.getUploadTimeLocation());
                    }
                    eventBus.post(new EventBusContext(StartService.UPLOAD_DATA,commonResponse.getResultCode()));
                }

                @Override
                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                    eventBus.post(new EventBusContext(NetworkConstants.ResultCodeFail));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
