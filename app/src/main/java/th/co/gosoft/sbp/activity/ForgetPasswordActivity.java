package th.co.gosoft.sbp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.util.PropertyUtility;

public class ForgetPasswordActivity extends AppCompatActivity {

    private final String LOG_TAG = "ForgetPasswordActivity";

    private String URL;
    private ProgressDialog progress;
    private EditText txtForgotEmail;
    private TextView txtOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)+"user/resetPasswordByEmail";
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.forget_pass);

        txtForgotEmail = (EditText) findViewById(R.id.txtForgotEmail);
        txtOutput = (TextView) findViewById(R.id.txtOutput);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    public void submit(View view){
        hideKeyboard();
        if(isInputsEmpty()){
            Toast.makeText(this, "Please enter your E-mail.", Toast.LENGTH_SHORT).show();
        } else {
            callWebService(txtForgotEmail.getText().toString());
        }
    }

    private void callWebService(String email){
        String concatString = URL+"?email="+email;

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(concatString, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        closeLoadingDialog();
                        String responseString = new String(response);
                        Log.i(LOG_TAG, "Reset password result string : "+responseString);

                        if("User does not exist on the system.".equals(responseString)){
                            txtOutput.setTextColor(Color.RED);
                        } else {
                            txtOutput.setTextColor(getResources().getColor(R.color.colorDarkGreen));
                        }
                        txtOutput.setText(responseString);
//                        if(userModelList.isEmpty()){
//                            Log.i(LOG_TAG, "Not have user model");
//                            Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            if(isActivate(userModelList)){
//                                Toast.makeText(getApplication(), "The e-mail or password is incorrect.\nPlease try again.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                insertUserModelToSharedPreferences(userModelList.get(0));
//                                if(hasNotSettingAvatar(userModelList)){
//                                    gotoSettingAvatarActivity();
//                                } else {
//                                    gotoHomeActivity();
//                                }
//                            }
//                            Log.i(LOG_TAG, "have user model");
//                        }

                    } catch (Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(LOG_TAG, "Error code : " + statusCode + ", " + error.getMessage());
                    closeLoadingDialog();
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
        }
    }

    private boolean isInputsEmpty() {
        return isEmpty(txtForgotEmail);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null, "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            Log.i(LOG_TAG, "view null hide keyboard");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
