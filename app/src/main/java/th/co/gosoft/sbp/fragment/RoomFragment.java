package th.co.gosoft.sbp.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.adapter.RoomAdapter;
import th.co.gosoft.sbp.util.DownloadImageUtils;
import th.co.gosoft.sbp.util.PropertyUtility;

import static android.os.SystemClock.sleep;

public class RoomFragment extends Fragment {

    private final String LOG_TAG = "RoomFragmentTag";

    private String URL;
    private ProgressDialog progress;
    private Map<String, Integer> imageIdMap = new HashMap<>();
    private List<Map<String, Object>> topicModelList = new ArrayList<>();
    private ListView topicListView;
    private String room_id;
    private String roomName;
    private PullRefreshLayout pullRefreshLayout;
    private String READROOM_URL;

    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(LOG_TAG," onCreate RoomFragment");
        sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        URL = PropertyUtility.getProperty("httpUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"api/"+PropertyUtility.getProperty("versionServer", getActivity())
                +"topic/gettopiclistbyroom";
        READROOM_URL = PropertyUtility.getProperty("httpsUrlSite", getActivity())+PropertyUtility.getProperty("contextRoot", getActivity())+"api/"+PropertyUtility.getProperty("versionServer", getActivity())
                +"topic/readroom";
        Bundle bundle = getArguments();
        room_id = bundle.getString("room_id");
        roomName = bundle.getString("roomName");
        callWebAccess();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_room, container, false);
        Log.i(LOG_TAG, "room_id : " + room_id);
        Log.i(LOG_TAG, "roomName : " + roomName);
        generateImageToMap(imageIdMap);
        ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);
        DownloadImageUtils.setImageRoom(getActivity(), imageView, room_id);
        TextView txtRoomName = (TextView)  view.findViewById(R.id.txtRoomName);
        txtRoomName.setText(roomName);
        pullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.pullRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        topicListView = (ListView) getView().findViewById(R.id.listViewTopic);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart RoomFragment");
        topicListView.setAdapter(null);
        sleep(100);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                generateListView();
                pullRefreshLayout.setRefreshing(false);
            }
        });
        callGetWebService();
    }

    private void callWebAccess(){
        final String empEmail = sharedPref.getString("empEmail", null);
        final String concatReadRoom = READROOM_URL +"?empEmail="+empEmail+"&roomId="+room_id;
        Log.i(LOG_TAG,"AccessRoom : "+concatReadRoom);
        try {
            final AsyncHttpClient clientAccess = new AsyncHttpClient();
            clientAccess.get(concatReadRoom, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(LOG_TAG, "readRoom : " + clientAccess);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
        }
    }


    private void callGetWebService(){
        String concatString = URL+"?roomId="+room_id+"&empEmail="+sharedPref.getString("empEmail", null)+"&startDate="+sharedPref.getString("notificationDate", null);
        Log.i(LOG_TAG, "URL : " + concatString);
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Cache-Control", "no-cache");
            showLoadingDialog();
            client.get(concatString, new BaseJsonHttpResponseHandler<List<Map<String, Object>>>() {

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, List<Map<String, Object>> response) {
                    try {
                        topicModelList = response;
                        generateListView();
                        closeLoadingDialog();
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
                    Log.i(LOG_TAG, ">>>>>>>>>>>>>>>>.. Json String : " + rawJsonData);
                    return new ObjectMapper().readValue(rawJsonData, new TypeReference<List<Map<String, Object>>>() {
                    });
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "RuntimeException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    private void generateListView() {
        RoomAdapter roomtAdapter = new RoomAdapter(getActivity(), R.layout.hot_topic_row, topicModelList);
        topicListView.setAdapter(roomtAdapter);
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                goBoardContentActivity(position);
            }
        });
    }

    private void goBoardContentActivity(int position) {
        Bundle data = new Bundle();
        data.putString("_id", topicModelList.get(position).get("_id").toString());
        Fragment fragment = new BoardContentFragment();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();
    }

    private void generateImageToMap(Map<String, Integer> imageIdMap) {
        imageIdMap.put("rm01", R.drawable.rm01);
        imageIdMap.put("rm02", R.drawable.rm02);
        imageIdMap.put("rm03", R.drawable.rm03);
        imageIdMap.put("rm04", R.drawable.rm04);
        imageIdMap.put("rm05", R.drawable.rm05);
        imageIdMap.put("rm06", R.drawable.rm06);
        imageIdMap.put("rm07", R.drawable.rm07);
        imageIdMap.put("rm08", R.drawable.rm08);
        imageIdMap.put("rm09", R.drawable.rm09);
        imageIdMap.put("rm10", R.drawable.rm10);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isPostUser(room_id)) {
            inflater.inflate(R.menu.room_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    private boolean isPostUser(String room_id) {
        boolean result = false;
        String empEmail = sharedPref.getString("empEmail", null);
        Set<String> stringSet = sharedPref.getStringSet("postUser"+room_id, null);
        if (stringSet.contains("all") || stringSet.contains(empEmail)) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnNewTopic:
                Log.i(LOG_TAG, "click new topic");
                Bundle data = new Bundle();
                data.putString("room_id", room_id);
                Fragment fragment = new WritingTopicFragment();
                fragment.setArguments(data);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();
                return true;

            default:
                break;
        }

        return false;
    }

}