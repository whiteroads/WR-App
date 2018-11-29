package com.whiteroads.application;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.whiteroads.application.data.UserDataWrapper;
import com.whiteroads.application.utils.CommonMethods;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {
    private Button fbLogin,googleLogin;
    private CallbackManager callbackManager;
    ProgressDialog progressDialog;
    private TextView privacyPolicy;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            privacyPolicy = (TextView)findViewById(R.id.privacyPolicy);
            if(UserDataWrapper.getInstance().getVehicleNumber()!=null
                    && !UserDataWrapper.getInstance().getVehicleNumber().equalsIgnoreCase("")){
                redirectToHome();
                return;
            }
            try{
                privacyPolicy.setVisibility(View.VISIBLE);
                privacyPolicy.setText(Html.fromHtml("As per our <a href=\"https://drivinganalytics.com/terms.php\"> Privacy Policy</a>, by continuing, you consent to share your Driving Analytics app usage data with DrivingAnalytics to help us with visual and non-visual feedback for improving the user experience in the app."));
                privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
            fbLogin = (Button) findViewById(R.id.fbLogin);
            googleLogin = (Button) findViewById(R.id.googleLogin);
            fbLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                }
            });
            googleLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 1001);
                }
            });
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // Callback registration
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    // App code
                    try {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code
                                        try {
                                            showHideDialog(true);
                                            showVehicleNoDialog(object,null);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showHideDialog(boolean isShow){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait....");
        }
        if(isShow){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }


    public void redirectToHome(){
        try{
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            showVehicleNoDialog(null,account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
        }
    }

    public void showVehicleNoDialog(final JSONObject object, final GoogleSignInAccount account) {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.common_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            final EditText number = (EditText) dialog.findViewById(R.id.vehicleNumberET);
            final EditText mobile = (EditText) dialog.findViewById(R.id.mobileNumberET);
            TextView save = (TextView) dialog.findViewById(R.id.saveBtn);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (number.getText().toString().length() == 0) {
                            CommonMethods.ShowToast(LoginActivity.this, "Enter Valid Vehicle Number");
                        }
                        if (!isValidMobile(mobile.getText().toString())) {
                            CommonMethods.ShowToast(LoginActivity.this, "Enter Valid Mobile Number");
                        } else {
                            String[] data = new String[5];
                            try {
                                if (account == null) {
                                    data[0] = object.getString("email");
                                    data[1] = object.getString("name");
                                    data[2] = object.getString("gender");
                                } else {
                                    data[0] = account.getEmail();
                                    data[1] = account.getDisplayName();
                                    data[2] = "NA Google doesn't provide";
                                    signOut();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            data[3] = number.getText().toString();
                            data[4] = mobile.getText().toString();
                            showHideDialog(true);
                            UserDataWrapper.getInstance().saveUserEmail(data[0]);
                            UserDataWrapper.getInstance().saveUserName(data[1]);
                            UserDataWrapper.getInstance().saveVehicleNumber(data[3]);
                            UserDataWrapper.getInstance().saveUserMobile(data[4]);
                            redirectToHome();
                            dialog.dismiss();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 10) {
                // if(phone.length() != 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}
