package th.co.gosoft.sbp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import richeditor.classes.RichEditor;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.model.TopicModel;
import th.co.gosoft.sbp.util.BitmapUtil;
import th.co.gosoft.sbp.util.ImageResolutionUtil;
import th.co.gosoft.sbp.util.PropertyUtility;

public class WritingTopicFragment extends Fragment {

    private final String LOG_TAG = "WritingTopicFragmentTag";
    private final int RESULT_LOAD_IMAGE = 7;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 89;

    private String URL;
    private String URL_POST_SERVLET;
    private ProgressDialog progress;
    private String room_id;
    private RichEditor mEditor;
    private EditText edtHostSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        URL = PropertyUtility.getProperty("httpUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"api/"+PropertyUtility.getProperty("versionServer", getActivity())
                +"topic/post";
        URL_POST_SERVLET = PropertyUtility.getProperty("httpUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"UploadServlet";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_writing_topic, container, false);

        edtHostSubject = (EditText) view.findViewById(R.id.txtHostSubject);

        mEditor = (RichEditor) view.findViewById(R.id.richHostContent);
        mEditor.setEditorFontSize(22);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Write something ...");

        Bundle bundle = getArguments();
        room_id = bundle.getString("room_id");
        Log.i(LOG_TAG, "room_id : " + room_id);

        view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        view.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if(!mEditor.hasFocus()){
                    mEditor.focusEditor();
                }

                if (Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                            Log.i(LOG_TAG, "else");
                            requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        }
                    }else{
                        Log.i(LOG_TAG, "ELSE");
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                }

            }
        });

        view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
            mEditor.insertLink();
            }
        });

        return view;
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

    private void callPostWebService(TopicModel topicModel){

        try {
            String jsonString = new ObjectMapper().writeValueAsString(topicModel);
            Log.i(LOG_TAG, URL);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(getActivity(), URL, new StringEntity(jsonString, "utf-8"),
                RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                        Log.i(LOG_TAG, "new id : "+new String(response));
                        closeLoadingDialog();
                        callNextActivity(new String(response));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e(LOG_TAG, "Error code : " + statusCode + ", " + e.getMessage(), e);
                        closeLoadingDialog();
                    }
                });

        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(getActivity(), null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private void callNextActivity(String _id) {
        Bundle data = new Bundle();
        data.putString("_id", _id);
        data.putString("room_id", room_id);
        Fragment fragment = new BoardContentFragment();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();
    }

    private AlertDialog.Builder showErrorDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.i(LOG_TAG, "picturePath : "+picturePath);
            RequestParams params = new RequestParams();

            try {
                Bitmap bitmap = BitmapUtil.resizeBitmap(picturePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

                byte[] myByteArray = stream.toByteArray();
                params.put("imageFile", new ByteArrayInputStream(myByteArray));
            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(URL_POST_SERVLET, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    showLoadingDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    String responseString = new String(responseBody);
                    Log.i(LOG_TAG, "Path : "+responseString);

                    try {
                        String imgURL =  new JSONObject(responseString).getString("imgUrl");
                        Log.i(LOG_TAG, "imgURL : "+imgURL);

                        Map<String, Integer> imageResolutionMap = ImageResolutionUtil.calculateResolution(BitmapUtil.width, BitmapUtil.height);
                        mEditor.insertImage(imgURL, imageResolutionMap.get("width"), imageResolutionMap.get("height"), "insertImageUrl");

                        closeLoadingDialog();
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                    closeLoadingDialog();
                    alertMessage("Error while uploading image");
                    Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            });
        }
    }

    private Map<String,Integer> calculateResolution(int width, int height) {
        Log.i(LOG_TAG, "calculateResolution width - height : "+width+" * "+height);
        Double ratio = (Math.round(((float) width / (float) height)*100.0) / 100.0);
        Log.i(LOG_TAG, "calculateResolution Ration Bitmap : "+ratio);
        Map<String,Integer> resultMap = new HashMap<>();
        if(ratio > 1) {
            if(ratio == 1.33) {
                Log.i(LOG_TAG, "4:3 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 222);
            } else if(ratio == 1.78 || ratio == 1.77) {
                Log.i(LOG_TAG, "16:9 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 166);
            } else {
                Log.i(LOG_TAG, "16:9 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 166);
            }
        } else if(ratio < 1) {
            if(ratio == 0.75) {
                Log.i(LOG_TAG, "3:4 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 307);
            } else if(ratio == 0.56) {
                Log.i(LOG_TAG, "9:16 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 410);
            } else {
                Log.i(LOG_TAG, "9:16 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 410);
            }
        } else if(ratio == 1) {
            Log.i(LOG_TAG, "1:1 square");
            resultMap.put("width", 295);
            resultMap.put("height", 295);
        }
        return resultMap;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.writing_post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();
        switch (item.getItemId()) {
            case R.id.btnPost:
                Log.i(LOG_TAG, "click post");
                String hostSubjectString = edtHostSubject.getText().toString();
                String hostContentString = mEditor.getHtml();

                Log.i(LOG_TAG, "title : " + hostSubjectString);
                Log.i(LOG_TAG, "Content : " + hostContentString);

                if(hostSubjectString == null || isEmpty(hostSubjectString) || hostContentString == null || isEmpty(hostContentString)){
                    Log.i(LOG_TAG, "empty title & message");
                    alertMessage("Please enter your Title and Comment message.");
                } else {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                    TopicModel topicModel = new TopicModel();
                    topicModel.setSubject(hostSubjectString);
                    topicModel.setContent(hostContentString);
                    topicModel.setEmpEmail(sharedPref.getString("empEmail", null));
                    topicModel.setAvatarName(sharedPref.getString("avatarName", null));
                    topicModel.setAvatarPic(sharedPref.getString("avatarPic", null));
                    topicModel.setType("host");
                    topicModel.setRoomId(room_id);
                    callPostWebService(topicModel);
                }
                return true;
            default:
                break;
        }

        return false;
    }

    private void alertMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmpty(String htmlString) {
        if(htmlString.contains("<img")){
            return false;
        } else {
            String replaceString = htmlString.replace("&nbsp;", " ");
            String string = Jsoup.parse(replaceString).text();
            Log.i(LOG_TAG, "String is Empty : "+string.isEmpty());
            return string.trim().length() == 0;
        }
    }

    private void hideKeyboard(){
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            Log.i(LOG_TAG, "view null hide keyboard");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
