package th.co.gosoft.sbp.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.adapter.PollAdapter;
import th.co.gosoft.sbp.model.ChoiceTransactionModel;
import th.co.gosoft.sbp.util.OnDataPass;
import th.co.gosoft.sbp.util.PropertyUtility;

/**
 * Created by ASUS on 30/3/2560.
 */

public class PollFragment extends Fragment implements OnDataPass {

    private final String LOG_TAG = "PollFragment";
    private String POST_POLL_URL;

    private List<Map> pollModel;
    private ListView pollListView;
    private Context context;
    private PollAdapter pollAdapter;
    private String empEmail;
    private SharedPreferences sharedPref;
    private Map<Integer, Integer> selectedMap;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        setHasOptionsMenu(true);

        POST_POLL_URL = PropertyUtility.getProperty("httpsUrlSite", this.context)+PropertyUtility.getProperty("contextRoot", this.context)+"api/"+PropertyUtility.getProperty("versionServer", this.context)
                +"poll/savePoll";

        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        empEmail = sharedPref.getString("empEmail", null);

        Bundle bundle = getArguments();
        pollModel = (List<Map>) bundle.get("pollModel");

        Log.i(LOG_TAG, "pollModel : " + pollModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_poll, container ,false);
        pollListView = (ListView) view.findViewById(R.id.pollList);
        Log.i(LOG_TAG,"OnCreateView ");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart ");
        generateListView();
    }

    private void generateListView(){
        pollListView.setAdapter(null);
        pollAdapter = new PollAdapter(context, this, pollModel);
        pollListView.setAdapter(pollAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poll_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSavePoll:
                if(validatePollInput()) {
                    List<ChoiceTransactionModel> choiceTransactionModelList = generateChoiceTransactionModelList(selectedMap, empEmail, pollModel);
                    Log.i(LOG_TAG, "choiceTransactionModelList size : "+choiceTransactionModelList.size());
                    callPostWebService(choiceTransactionModelList, POST_POLL_URL);
                } else {
                    alertMessage("Please complete all question!");
                }
                return true;
            default:
                break;
        }

        return false;
    }

    private void callPostWebService(List<ChoiceTransactionModel> choiceTransactionModelList, String webServiceURL) {
        try {
            String jsonString = new ObjectMapper().writeValueAsString(choiceTransactionModelList);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(this.context, webServiceURL, new StringEntity(jsonString, "utf-8"),
                    RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() { showLoadingDialog();}

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            closeLoadingDialog();
                            callNextActivity((String) pollModel.get(0).get("topicId"));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            Log.e(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.e(LOG_TAG, "AsyncHttpClient returned error", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
        }
    }

    private void callNextActivity(String _id) {
        Bundle data = new Bundle();
        data.putString("_id", _id);
        Fragment fragment = new BoardContentFragment();
        fragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("tag").commit();
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(getActivity(), null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private boolean validatePollInput() {
        List<Map<String, Object>> questionMasterList = (List<Map<String, Object>>) pollModel.get(0).get("questionMaster");
        if(selectedMap != null && questionMasterList.size() == selectedMap.size()) {
            return true;
        } else {
            return false;
        }
    }

    private List<ChoiceTransactionModel> generateChoiceTransactionModelList(Map<Integer, Integer> selectedMap, String empEmail, List<Map> pollModelList) {
        try {
            List<ChoiceTransactionModel> resultList = new ArrayList<>();
            Map<String, Object> pollModel = pollModelList.get(0);
            for (int i=0; i<selectedMap.size(); i++){
                ChoiceTransactionModel choiceTransactionModel = new ChoiceTransactionModel();
                choiceTransactionModel.setPollId((String) pollModel.get("_id"));

                List<Map<String, Object>> questionMasterList = (List<Map<String, Object>>) pollModel.get("questionMaster");
                Map<String, Object> questionMaster = questionMasterList.get(i);
                String questionId = (String) questionMaster.get("questionId");
                choiceTransactionModel.setQuestionId(questionId);

                choiceTransactionModel.setChoiceKey(parseIdToChoiceKey(questionId, selectedMap.get(i*10)));
                choiceTransactionModel.setEmpEmail(empEmail);
                choiceTransactionModel.setType("choice");
                Log.i(LOG_TAG, choiceTransactionModel.getPollId()+", "+choiceTransactionModel.getQuestionId()+", "+choiceTransactionModel.getChoiceKey()
                        +", "+choiceTransactionModel.getEmpEmail()+", "+choiceTransactionModel.getType());
                resultList.add(choiceTransactionModel);
            }
            return resultList;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String parseIdToChoiceKey(String questionId, Integer integer) {
        return questionId+"c"+integer;
    }

    @Override
    public void onDataPass(Object data) {
        selectedMap = (Map<Integer, Integer>) data;
    }

    private void alertMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
