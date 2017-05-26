package th.co.gosoft.sbp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.model.UserModel;
import th.co.gosoft.sbp.util.PropertyUtility;

public class LoadingActivity extends Activity {

    private final String LOG_TAG = "LoadingActivityTag";

    private final long SPLASH_TIME_OUT = 1000L;
    private boolean IS_LOGIN_FACEBOOK = false;
    private boolean IS_SIGNIN_GOOGLE = false;
    private GoogleApiClient mGoogleApiClient;

    private String URL;
    private String URL_CHECK_USER_ACTIVATION;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String ACCESS_URL;
    private boolean isActivate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_loading);
//        notification
//        BMSClient.getInstance().initialize(this, BMSClient.REGION_SYDNEY);
//        push = MFPPush.getInstance();
//        push.initialize(getApplicationContext(), "3c5e9860-be2b-4276-a53b-b12f0d3db6bb", "f1b7da23-fe5e-40d4-99b5-ca39eaae8b35");
//        push.registerDevice(new MFPPushResponseListener<String>() {
//            @Override
//            public void onSuccess(String deviceId) {
//                Log.d(LOG_TAG,"REGISTER SUCCESS : "+deviceId);    // 180d96c9-8b0f-3f3d-9247-260d4aa635cb
//            }
//            @Override
//            public void onFailure(MFPPushException ex) {
//                Log.e(LOG_TAG,"REGISTER FALSE : "+ex.getMessage(), ex);
//            }
//        });
//        push.setNotificationStatusListener(new MFPPushNotificationStatusListener() {
//            @Override
//            public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
//                Log.d(LOG_TAG, "Status Change : "+status);
//            }
//        });
//        notificationListener = new MFPPushNotificationListener() {
//            @Override
//            public void onReceive (final MFPSimplePushNotification message){
//                Log.d(LOG_TAG, "Messsage : "+message);
//            }
//        };

        URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/getUserByAccountId";
        URL_CHECK_USER_ACTIVATION = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/checkUserActivation";
        ACCESS_URL = PropertyUtility.getProperty("httpsUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                +"topic/accessapp";
        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        try{
            if(hasUserLoggedIn()){
                Log.i(LOG_TAG, "User Logged In");
                activateUserAccount();
            } else {
                Log.i(LOG_TAG, "User Not Logged In");
                gotoLoginActivity();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void gotoHomeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
                startActivity(i);
                finish();

            }
        }, SPLASH_TIME_OUT);

    }
// notification IMPush
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(push != null) {
//            push.listen(notificationListener);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (push != null) {
//            push.hold();
//        }
//    }

    private void gotoLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private boolean hasUserLoggedIn() {
         return sharedPref.getBoolean("hasLoggedIn", false);
    }

    private void callWebAccess(){
        final String empEmail = sharedPref.getString("empEmail", null);
        final String concatAccess = ACCESS_URL+"?empEmail="+empEmail;
        try {
            final AsyncHttpClient clientAccess = new AsyncHttpClient();
            clientAccess.get(this, concatAccess, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(LOG_TAG, "WEBSERVICE : " + concatAccess);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });
        } catch (Exception e) {
                Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            }
    }

    private void activateUserAccount() {
        final String empEmail = sharedPref.getString("empEmail", null);
        String concatString = URL_CHECK_USER_ACTIVATION+"?empEmail="+empEmail;
        try {
            final AsyncHttpClient client = new AsyncHttpClient();
            client.get(this, concatString, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                    Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    if(statusCode == 201){

                        if(hasNotSettingAvatar()){
                            gotoSettingAvatarActivity();
                        } else {
                            callWebAccess();
                            gotoHomeActivity();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                    if(statusCode == 404) {
                        Toast.makeText(getApplication(), new String(errorResponse), Toast.LENGTH_LONG).show();
                        editor.putBoolean("hasLoggedIn", false);
                        editor.commit();
                        gotoLoginActivity();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
        }

    }

    private boolean hasNotSettingAvatar() {
        return "Avatar Name".equals(sharedPref.getString("avatarName","Avatar Name")) || "default_avatar".equals(sharedPref.getString("avatarPic","default_avatar"));
    }

    private void gotoSettingAvatarActivity(){
        Intent intent = new Intent(this, SettingAvatar.class);
        intent.putExtra("state", "register");
        startActivity(intent);
        finish();
    }

    private void checkCurrentTokenFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.i(LOG_TAG, "Facebook logged in");
            initialNewFacebookBundle();
            IS_LOGIN_FACEBOOK = true;
        } else {
            Log.i(LOG_TAG, "Facebook not logged in");
            IS_LOGIN_FACEBOOK = false;
        }
    }

    private void initialNewFacebookBundle() {
        Bundle params = new Bundle();
        params.putString("fields","id, name");

        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
            new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response != null) {
                        try {
                            JSONObject data = response.getJSONObject();
                            getUserDataFromServer(data.getString("id"));
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }
            }).executeAsync();
    }

    private void checkCurrentTokenGmail() {

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.i(LOG_TAG, "Google logged in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
            IS_SIGNIN_GOOGLE = true;
        } else {
            Log.i(LOG_TAG, "Google not logged in");
            IS_SIGNIN_GOOGLE = false;

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            getUserDataFromServer(acct.getId());
        } else {
            Log.i(LOG_TAG, "Cannot Login GMAIL Accout !!!");
        }
    }

    private void prepareGmailLoginSession() {
        try{
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void getUserDataFromServer(final String accountId){
        String concatString = URL+"?accountId="+accountId;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Cache-Control", "no-cache");
            client.get(concatString, new BaseJsonHttpResponseHandler<List<UserModel>>() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<UserModel> response) {
                    try {
                        List<UserModel> userModelList = response;
                        Log.i(LOG_TAG, "user modelList size : "+userModelList.size());
                        if(userModelList.isEmpty()){
                            Log.i(LOG_TAG, "Not have user model");
                            insertUserModelToSharedPreferences(accountId);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(LoadingActivity.this, RegisterActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        } else {
                            Log.i(LOG_TAG, "have user model");
                            Log.i(LOG_TAG,"Acess WebService :"+userModelList);
                            insertUserModelToSharedPreferences(userModelList.get(0));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(LoadingActivity.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<UserModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                }

                @Override
                protected List<UserModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<UserModel>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
        }
    }

    private void insertUserModelToSharedPreferences(String accountId){

        Log.i(LOG_TAG, "account id : "+accountId);
        clearSharedPreference();
        editor.putString("accountId",  accountId);
        if(!sharedPref.contains("empName")){
            editor.putString("empName",  "Employee Name");
        }
        if(!sharedPref.contains("empEmail")){
            editor.putString("empEmail",  "email@gosoft.com");
        }
        if(!sharedPref.contains("avatarName")){
            editor.putString("avatarName",  "Avatar Name");
        }
        if(!sharedPref.contains("avatarPic")) {
            editor.putString("avatarPic", "default_avatar");
        }
        editor.commit();
    }

    private void insertUserModelToSharedPreferences(UserModel userModel){
        Log.i(LOG_TAG, "AVATAR DATA : "+userModel.getAvatarPic()+" : "+userModel.getAvatarName());
        clearSharedPreference();
        editor.putString("_id",  userModel.get_id());
        editor.putString("_rev",  userModel.get_rev());
        editor.putString("accountId",  userModel.getAccountId());
        editor.putString("empName",  userModel.getEmpName());
        editor.putString("empEmail",  userModel.getEmpEmail());
        editor.putString("avatarName",  userModel.getAvatarName());
        editor.putString("avatarPic", userModel.getAvatarPic());
        editor.putString("token",  userModel.getToken());
        editor.putBoolean("activate",  userModel.isActivate());
        editor.putString("type", userModel.getType());
        editor.commit();
    }

    private void clearSharedPreference() {
        editor.clear();
        editor.commit();
    }

}