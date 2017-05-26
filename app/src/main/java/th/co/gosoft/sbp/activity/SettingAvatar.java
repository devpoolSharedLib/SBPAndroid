package th.co.gosoft.sbp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.adapter.SettingAvatarAdapter;
import th.co.gosoft.sbp.model.UserModel;
import th.co.gosoft.sbp.util.BitmapUtil;
import th.co.gosoft.sbp.util.DownloadImageUtils;
import th.co.gosoft.sbp.util.PropertyUtility;

public class SettingAvatar extends AppCompatActivity {

    private final String LOG_TAG = "SettingAvatar";

    private String URL;
    private ImageView avatarPic;
    private ListView settingListView;
    private Button btnStart;
    private String avatarPicName;
    private String avatarName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;
    private boolean isSeparateUpdate;
    private String URL_POST_SERVLET;
    private String imgURL;
    private com.squareup.picasso.Target target ;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 89;
    public static final int RESULT_LOAD_IMAGE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_avatar);

        try{

        URL = PropertyUtility.getProperty("httpUrlSite", this)+PropertyUtility.getProperty("contextRoot", this)+"api/"+PropertyUtility.getProperty("versionServer", this)
                    +"user/updateUser";
        URL_POST_SERVLET = PropertyUtility.getProperty("httpUrlSite", this) + PropertyUtility.getProperty("contextRoot", this) + "UploadServlet";
        sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            isSeparateUpdate = true;
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.setting_avatar);
        } else if(isComeFromRegisterActivity(extras)){
            isSeparateUpdate = false;
            btnStart = (Button) findViewById(R.id.btnStart);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(avatarPicName.equals("default_avatar") || avatarName.equals("Avatar Name")){
                        Toast.makeText(getApplication(), "Please select avatar image and setting avatar name.", Toast.LENGTH_LONG).show();
                    } else {
                        saveSetting(true);
                    }
                }
            });
        }

        avatarPic = (ImageView) findViewById(R.id.imgProfileImage);
        avatarPic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence settingAvatar[] = new CharSequence[] {"Upload photo", "Select avatar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingAvatar.this);
                builder.setItems(settingAvatar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = (String) settingAvatar[which];
                        if (selected.toString().equals("Upload photo")) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ContextCompat.checkSelfPermission(SettingAvatar.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingAvatar.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    } else {
                                        Log.i(LOG_TAG, "else");
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    }
                                } else {
                                    Log.i(LOG_TAG, "ELSE");
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                }
                            } else {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SelectAvatarPic.class);
                            intent.putExtra("isSeparateUpdate",isSeparateUpdate);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });

        settingListView = (ListView) findViewById(R.id.settingListview);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent;
                switch(position) {
                    case 0 :
                        intent = new Intent(getApplicationContext(), SettingAvatarName.class);
                        intent.putExtra("isSeparateUpdate",isSeparateUpdate);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.i(LOG_TAG, "picturePath : " + picturePath);
            RequestParams params = new RequestParams();
            try {
                Bitmap bitmap = BitmapUtil.resizeBitmap(picturePath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] myByteArray = byteArrayOutputStream.toByteArray();
                params.put("imageFile", new ByteArrayInputStream(myByteArray));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(URL_POST_SERVLET, params, new AsyncHttpResponseHandler() {

                public void onStart() {
                    showLoadingDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    String responseString = new String(responseBody);
                    Log.i(LOG_TAG, "Path : " + responseString);
                    try {
                        imgURL = new JSONObject(responseString).getString("imgUrl");
                        Log.i(LOG_TAG, "imgURL : " + imgURL);
                        String imgFile = imgURL.substring(imgURL.lastIndexOf("/")+1);
                        editor.putString("avatarPic",  new String(imgFile));
                        editor.commit();
                        DownloadImageUtils.setImageAvatar(SettingAvatar.this, avatarPic, imgFile);
                        closeLoadingDialog();
                        saveSetting(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                    closeLoadingDialog();
                    Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            });
        }
    }

    private boolean isComeFromRegisterActivity(Bundle extras) {
        return extras != null && extras.getString("state").equals("register");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.i(LOG_TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "if onRequestPermissionsResult");
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);

                } else {
                    Log.i(LOG_TAG, "else onRequestPermissionsResult");
                }
                return;
            }
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        avatarName = sharedPref.getString("avatarName", "Avatar Name");
        avatarPicName = sharedPref.getString("avatarPic", "default_avatar");
        String avatarPicName = sharedPref.getString("avatarPic", "default_avatar");
        SettingAvatarAdapter settingAvatarAdapter = new SettingAvatarAdapter(this, avatarName);
        settingListView.setAdapter(settingAvatarAdapter);
        DownloadImageUtils.setImageAvatar(SettingAvatar.this, avatarPic, avatarPicName);
    }

    private void saveSetting(boolean flag){
        UserModel userModel = getUserModelFromSharedPreferences();
        callWebService(userModel, flag);
    }

    private UserModel getUserModelFromSharedPreferences() {
        UserModel userModel = new UserModel();
        userModel.set_id(sharedPref.getString("_id", null));
        userModel.set_rev(sharedPref.getString("_rev", null));
        userModel.setAccountId(sharedPref.getString("accountId", null));
        userModel.setEmpName(sharedPref.getString("empName", null));
        userModel.setEmpEmail(sharedPref.getString("empEmail", null));
        userModel.setActivate(sharedPref.getBoolean("activate", true));
        userModel.setType(sharedPref.getString("type", null));
        userModel.setAvatarPic(sharedPref.getString("avatarPic", null));
        userModel.setAvatarName(sharedPref.getString("avatarName", null));
        userModel.setBirthday(sharedPref.getString("birthday", null));
        return userModel;
    }

    private void callWebService(UserModel userModel, final boolean flag){
        try {
            String jsonString = new ObjectMapper().writeValueAsString(userModel);
            Log.i(LOG_TAG, URL);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.put(this, URL, new StringEntity(jsonString,"utf-8"),
                RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                        Log.i(LOG_TAG, "New rev : "+new String(response));
                        editor.putString("_rev",  new String(response));
                        editor.commit();
                        closeLoadingDialog();
                        if(flag){
                            callNextActivity();
                        }
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

    private void showLoadingDialog() {
        progress = ProgressDialog.show(this, null,
                "Processing", true);
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

    private void callNextActivity(){
        Intent intent = new Intent(SettingAvatar.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
