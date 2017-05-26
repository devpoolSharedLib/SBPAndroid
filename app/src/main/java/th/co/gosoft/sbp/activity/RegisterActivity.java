package th.co.gosoft.sbp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.model.UserModel;
import th.co.gosoft.sbp.util.PropertyUtility;

public class RegisterActivity extends AppCompatActivity {

    private final String LOG_TAG = "RegisterActivity";

    private String URL;
    private EditText edtToken;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        URL = PropertyUtility.getProperty("httpUrlSite", this)+"SBPWebService/api/user/getUserByToken";
        edtToken = (EditText) findViewById(R.id.edtToken);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void validateToken(View view){
        if(isEmpty(edtToken.getText().toString())){
            Log.i(LOG_TAG, "empty message");
            Toast.makeText(this, "The token is empty. Please enter the invitation code.", Toast.LENGTH_SHORT).show();
        } else {
            callWebService(edtToken.getText().toString());
        }
    }

    private void callWebService(String token){
        String concatString = URL+"?token="+token;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new BaseJsonHttpResponseHandler<List<UserModel>>() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<UserModel> response) {
                    try {
                        closeLoadingDialog();
                        List<UserModel> userModelList = response;
                        Log.i(LOG_TAG, "user modelList size : "+userModelList.size());
                        if(userModelList.isEmpty()){
                            Log.i(LOG_TAG, "Not have user model");
                            Toast.makeText(getApplication(), "The invitation code is invalid.", Toast.LENGTH_SHORT).show();
                        } else {
                            insertUserModelToSharedPreferences(userModelList.get(0));
                            gotoSettingAvatar();
                            Log.i(LOG_TAG, "have user model");
                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<UserModel> errorResponse) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage());
                    closeLoadingDialog();
                }

                @Override
                protected List<UserModel> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<UserModel>>() {});
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
        }
    }

    private void gotoSettingAvatar(){
        Intent intent = new Intent(RegisterActivity.this, SettingAvatar.class);
        intent.putExtra("state", "register");
        startActivity(intent);
        finish();
    }

    private void insertUserModelToSharedPreferences(UserModel userModel){
        editor.putString("_id",  userModel.get_id());
        editor.putString("_rev",  userModel.get_rev());
        editor.putString("empName",  userModel.getEmpName());
        editor.putString("empEmail",  userModel.getEmpEmail());
        editor.putString("token",  userModel.getToken());
        editor.putBoolean("activate",  userModel.isActivate());
        editor.putString("type", userModel.getType());
        editor.commit();
    }

    private boolean isEmpty(String string) {
        return string.trim().length() == 0;
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null, "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }
}
