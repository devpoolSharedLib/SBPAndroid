package th.co.gosoft.sbp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.model.UserModel;
import th.co.gosoft.sbp.util.PropertyUtility;

public class SettingAvatarName extends AppCompatActivity {

    private final String LOG_TAG = "SettingAvatarName";
//    private final String URL = "http://go10webservice.au-syd.mybluemix.net/GO10WebService/api/user/updateUser";
    private static final int MAX_LENGTH = 20;

    private String URL;
    private String CHECK_AVATAR_NAME_URL;
    private EditText edtAvatarName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;
    private boolean isSeparateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_avatar_name);

        URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/updateUser";
        CHECK_AVATAR_NAME_URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                +"user/checkAvatarName";
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.change_avatar_name);

        sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();
        isSeparateUpdate = extras.getBoolean("isSeparateUpdate");

        edtAvatarName = (EditText) findViewById(R.id.edtAvatarName);
        edtAvatarName.setFilters( new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH)});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_avatar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSaveSetting:
                if(isEmpty(edtAvatarName.getText().toString())){
                    Toast.makeText(getApplication(), "Please insert your avatar name.", Toast.LENGTH_SHORT).show();
                } else {
                    hideKeyboard();
                    checkAvatarNameHasBeenUse();
                }
//                saveAvatarToSharedPreferences();
//                if(isSeparateUpdate){
//                    saveSetting();
//                } else {
//                    backPressed();
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSetting() {
        UserModel userModel = getUserModelFromSharedPreferences();
        callWebService(userModel);
    }

    private void saveAvatarToSharedPreferences(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("avatarName",  edtAvatarName.getText().toString());
        editor.commit();
    }

    private UserModel getUserModelFromSharedPreferences() {
        UserModel userModel = new UserModel();
        userModel.set_id(sharedPref.getString("_id", null));
        userModel.set_rev(sharedPref.getString("_rev", null));
        userModel.setAccountId(sharedPref.getString("accountId", null));
        userModel.setEmpName(sharedPref.getString("empName", null));
        userModel.setEmpEmail(sharedPref.getString("empEmail", null));
        userModel.setToken(sharedPref.getString("token", null));
        userModel.setActivate(sharedPref.getBoolean("activate", false));
        userModel.setType(sharedPref.getString("type", null));
        userModel.setAvatarPic(sharedPref.getString("avatarPic", null));
        userModel.setAvatarName(sharedPref.getString("avatarName", null));
        userModel.setBirthday(sharedPref.getString("birthday", null));
        return userModel;
    }

    private void checkAvatarNameHasBeenUse(){
        try{
            String concatString = CHECK_AVATAR_NAME_URL+"?avatarName="+edtAvatarName.getText().toString().trim();
            Log.i(LOG_TAG, concatString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(this, concatString, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    if(statusCode == 201){
                        saveAvatarToSharedPreferences();
                        if(isSeparateUpdate){
                            saveSetting();
                        } else {
                            backPressed();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                    if(statusCode == 404) {
                        closeLoadingDialog();
                        Toast.makeText(getApplication(), new String(errorResponse), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void callWebService(UserModel userModel){
        try {
            String jsonString = new ObjectMapper().writeValueAsString(userModel);
            Log.i(LOG_TAG, URL);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.put(this, URL, new StringEntity(jsonString,"utf-8"),
                    RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.i(LOG_TAG, "New rev : "+new String(response));
                            editor.putString("_rev",  new String(response));
                            editor.commit();
                            closeLoadingDialog();
                            backPressed();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void backPressed(){
        super.onBackPressed();
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(SettingAvatarName.this, null, "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            Log.i(LOG_TAG, "view null hide keyboard");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isEmpty(String string) {
        return string.trim().length() == 0;
    }
}
