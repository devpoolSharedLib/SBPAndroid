package th.co.gosoft.sbp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.model.UserModel;
import th.co.gosoft.sbp.util.PropertyUtility;

public class FacebookGoogleLoginActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private final String LOG_TAG = "LoginActivityTag";

    private String URL;
    private static final int RC_SIGN_IN = 9001;
    private boolean IS_REGISTER_ACCOUNT = false;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_google_login);

        URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/user/getUserByAccountId";
        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        try{
            FacebookSdk.sdkInitialize(getApplicationContext());
            prepareFacebookLoginSession();
            prepareGmailLoginSession();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void prepareFacebookLoginSession(){
        try{
            callbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            List < String > permissionNeeds = Arrays.asList("public_profile");
            loginButton.setReadPermissions(permissionNeeds);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.i(LOG_TAG, "success : "+loginResult);

                    GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i(LOG_TAG, response.toString());
                                try {
                                    getUserDataFromServer(object.getString("id"));
                                } catch (JSONException e) {
                                    Log.e(LOG_TAG, e.getMessage(), e);
                                }
                            }
                        });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields","id, name");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                    Log.i(LOG_TAG, "cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void prepareGmailLoginSession() {
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Log.i(LOG_TAG, "prepareGmailLoginSession()");
    }

    public void signIn(){
        Log.i(LOG_TAG, "callGoogleSignInActivity()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, data);
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
                            gotoRegisterActivity();
                        } else {
                            Log.i(LOG_TAG, "have user model");
                            insertUserModelToSharedPreferences(userModelList.get(0));
                            gotoHomeActivity();
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

    private void gotoHomeActivity() {
        Intent i = new Intent(FacebookGoogleLoginActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void gotoRegisterActivity() {
        Intent i = new Intent(FacebookGoogleLoginActivity.this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    private void insertUserModelToSharedPreferences(String accountId){

        Log.i(LOG_TAG, "account id : "+accountId);

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}