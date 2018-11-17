package com.analytics.library.networks;

import android.content.Context;

import com.analytics.library.StartService;
import com.analytics.library.constants.EventBusContext;
import com.analytics.library.constants.NetworkConstants;
import com.analytics.library.data.LocationDataWrapper;
import com.analytics.library.data.SensorsDataWrapper;
import com.analytics.library.data.UserDataWrapper;
import com.analytics.library.model.CommonResponse;
import com.analytics.library.model.UserLoginResponse;
import com.analytics.library.networks.ResponseHandler.ResponseCallback;
import com.analytics.library.networks.ResponseHandler.ResponseHandler;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Response;

public class NetworksCalls {
    private Context context;

    public NetworksCalls(Context context) {
        this.context = context;
    }

    public void storeCapturedData(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA;
            Call<CommonResponse> call = null;
            call = APISetup.apiInterface.storeCapturedData(url, capturedData);
            call.enqueue(new ResponseCallback(new ResponseHandler() {
                @Override
                public void onSuccess(Response response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        SensorsDataWrapper.getInstance().removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeCapturedDataLocations(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA_LOCATIONS;
            Call<CommonResponse> call = null;
            call = APISetup.apiInterface.storeCapturedData(url, capturedData);
            call.enqueue(new ResponseCallback(new ResponseHandler() {
                @Override
                public void onSuccess(Response response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        LocationDataWrapper.getInstance().removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeCapturedDataSteps(final String capturedData) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.STORE_CAPTURE_DATA_STEPS;
            Call<CommonResponse> call = null;
            call = APISetup.apiInterface.storeCapturedData(url, capturedData);
            call.enqueue(new ResponseCallback(new ResponseHandler() {
                @Override
                public void onSuccess(Response response) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        //removing cache
                        SensorsDataWrapper.getInstance().removeCacheData(capturedData);
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loginUser(String data, final EventBus eventBus) {
        try {
            String url = NetworkConstants.BASE_URL + NetworkConstants.LOGIN_USER;
            Call<UserLoginResponse> call = null;
            call = APISetup.apiInterface.loginUser(url, data);
            call.enqueue(new ResponseCallback(new ResponseHandler() {
                @Override
                public void onSuccess(Response response) {

                    UserLoginResponse commonResponse = (UserLoginResponse) response.body();
                    if(commonResponse!=null && commonResponse.getResultCode()==NetworkConstants.ResultCodeSuccess){
                        UserDataWrapper.getInstance().saveUserEmail(commonResponse.getUserEmail());
                        UserDataWrapper.getInstance().saveUserId(commonResponse.getUserId());
                        UserDataWrapper.getInstance().saveVehicleNumber(commonResponse.getVehicleNumber());
                    }
                    eventBus.post(new EventBusContext(StartService.UPLOAD_DATA,commonResponse.getResultCode()));
                }

                @Override
                public void onFailure(Throwable t) {
                    eventBus.post(new EventBusContext(NetworkConstants.ResultCodeFail));
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
