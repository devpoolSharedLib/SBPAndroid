package th.co.gosoft.sbp.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.adapter.HotTopicListAdapter;
import th.co.gosoft.sbp.adapter.RoomGridAdapter;
import th.co.gosoft.sbp.util.PropertyUtility;

public class SelectRoomFragment extends Fragment {

    private final String LOG_TAG = "SelectRoomFragmentTag";

    private String URL_HOT;
    private String URL_ROOM;
    private ProgressDialog progress;
    private List<Map<String, Object>> topicModelList;
    private List<Map<String, Object>> roomModelList;
    private ListView hotTopicListView;
    private LinearLayout linearRoom;
    private boolean isLoadTopicDone = false;
    private boolean isLoadRoomDone = false;
    private PullRefreshLayout pullRefreshLayout;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        URL_HOT = PropertyUtility.getProperty("httpUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"api/"+PropertyUtility.getProperty("versionServer", getActivity())
                +"topic/gethottopiclist";
        URL_ROOM = PropertyUtility.getProperty("httpUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"api/"+PropertyUtility.getProperty("versionServer", getActivity())
                +"room/get";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView()");
        return inflater.inflate(R.layout.activity_select_room, container, false);

    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pullRefreshLayout = (PullRefreshLayout) getView().findViewById(R.id.activity_select_room_swipe_refresh_layout);

        try{
            hotTopicListView = (ListView) getView().findViewById(R.id.listViewSelectAvatar);
            linearRoom = (LinearLayout) getView().findViewById(R.id.linearRoom);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                callHotTopicApi();
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart()");
        hotTopicListView.setAdapter(null);
        callGetWebService();
    }

    private void callGetWebService(){
        try {
            showLoadingDialog();
            callRoomApi();
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            closeLoadingDialog();
            showErrorDialog().show();
        }
    }

    private void callHotTopicApi() {
        String urlHot = URL_HOT+"?empEmail="+sharedPref.getString("empEmail", null)+"&startDate="+sharedPref.getString("notificationDate", null);
        Log.i(LOG_TAG, "urlHot : "+urlHot);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cache-Control", "no-cache");
        client.get(urlHot, new BaseJsonHttpResponseHandler<List<Map<String, Object>>>() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<Map<String, Object>> response) {
                try {
                    topicModelList = response;
                    generateListView();
                    isLoadTopicDone = true;
                    if(isLoadTopicDone && isLoadRoomDone){
                        closeLoadingDialog();
                    }
                    Log.i(LOG_TAG, "Topic Model List Size : " + topicModelList.size());

                } catch (Throwable e) {
                    closeLoadingDialog();
                    Log.e(LOG_TAG, e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<Map<String, Object>> errorResponse) {
                Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage(), throwable);
                closeLoadingDialog();
            }

            @Override
            protected List<Map<String, Object>> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<Map<String, Object>>>(){});
            }

        });
    }

    private void callRoomApi(){
        String urlRoom = URL_ROOM+"?empEmail="+sharedPref.getString("empEmail", null);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cache-Control", "no-cache");
        client.get(urlRoom, new BaseJsonHttpResponseHandler<List<Map<String, Object>>>() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<Map<String, Object>> response) {
                try {
                    roomModelList = response;
                    generateGridView();
                    isLoadRoomDone = true;
                    insertUserRoleManagementToSharedPreferences(roomModelList);
                    Log.i(LOG_TAG, "Room Model List Size : " + roomModelList.size());
                    callHotTopicApi();
                } catch (Throwable e) {
                    closeLoadingDialog();
                    Log.e(LOG_TAG, e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, List<Map<String, Object>> errorResponse) {
                Log.e(LOG_TAG, "Error code : " + statusCode + ", " + throwable.getMessage(), throwable);
                closeLoadingDialog();
            }

            @Override
            protected List<Map<String, Object>> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : "+rawJsonData);
                return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<Map<String, Object>>>(){});
            }
        });
    }

    private void generateListView() {
        HotTopicListAdapter hostTopicListAdapter = new HotTopicListAdapter(getActivity(), R.layout.hot_topic_row, topicModelList);
        hotTopicListView.setAdapter(hostTopicListAdapter);
        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void generateGridView() {
        try{
            Log.i(LOG_TAG, "Generate Grid View");
            linearRoom.removeAllViews();
            RoomGridAdapter roomAdapter = new RoomGridAdapter(getActivity(), R.layout.room_grid, roomModelList);
            for (int i = 0; i < roomModelList.size(); i++) {
                View view = roomAdapter.getView(i, null, null);
                final int index = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goRoomActivity(index);
                    }
                });
                linearRoom.addView(view);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void goBoardContentActivity(int position) {
        try{
            Bundle data = new Bundle();
            data.putString("_id", topicModelList.get(position).get("_id").toString());
            Fragment fragment = new BoardContentFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void insertUserRoleManagementToSharedPreferences(List<Map<String, Object>> roomModelList) {
        for (Map<String, Object> roomMap : roomModelList) {
            editor.putStringSet("postUser"+roomMap.get("_id").toString(), createSet((List<String>) roomMap.get("postUser")));
            editor.putStringSet("commentUser"+roomMap.get("_id").toString(),  createSet((List<String>) roomMap.get("commentUser")));
        }
        editor.commit();
    }

    private Set<String> createSet(List<String> userList) {
        Set<String> stringSet = new HashSet<>();
        for (String name : userList) {
            stringSet.add(name);
        }
        return stringSet;
    }

    private void goRoomActivity(int position) {
        try{
            Bundle data = new Bundle();
            data.putString("room_id", (String) roomModelList.get(position).get("_id"));
            data.putString("roomName", (String) roomModelList.get(position).get("name"));

            Fragment fragment = new RoomFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(getActivity(), null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private AlertDialog.Builder showErrorDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }
}

