package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.util.OnDataPass;

public class PollAdapter extends BaseAdapter {

    private final String LOG_TAG = "PollAdapter";

    private LayoutInflater layoutInflater;
    private List<Map> questionMaster;
    private Context context;
    private ViewHolder holder = null;
    private OnDataPass onDataPass;
    private Map<Integer, Integer> selectedMap = new HashMap<>();

    public PollAdapter(Context context, OnDataPass onDataPass, List<Map> pollModel) {
        this.layoutInflater = LayoutInflater.from(context);
        this.questionMaster = (List<Map>) pollModel.get(0).get("questionMaster");
        this.context = context;
        this.onDataPass = onDataPass;
        Log.i(LOG_TAG, "PollModel : "+pollModel);
    }

    @Override
    public int getCount() {
        return questionMaster.size();
    }

    @Override
    public Object getItem(int position) {
        return questionMaster.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG,"position : "+position);
        try {
            Map<String, Object> questionMaster = (Map<String, Object>) getItem(position);
            List<Map<String, Object>> choiceMasterList = (List<Map<String, Object>>) questionMaster.get("choiceMaster");

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.question_row, null);
                holder.txtTopicQuestion = (TextView) convertView.findViewById(R.id.txtTopicQuestion);
                holder.linearChoice = (LinearLayout) convertView.findViewById(R.id.linearChoice);
                convertView.setTag(holder);
            } else {
                holder = (PollAdapter.ViewHolder) convertView.getTag();
            }

            holder.txtTopicQuestion.setText((String) questionMaster.get("questionTitle"));
            holder.linearChoice.removeAllViews();

            RadioGroup radioGroup = new RadioGroup(this.context);
            radioGroup.setId(position*10);
            radioGroup.setOrientation(RadioGroup.VERTICAL);
            for(int i=0; i<choiceMasterList.size(); i++){
                Map<String, Object> choiceMaster = choiceMasterList.get(i);
                RadioButton radioButton = new RadioButton(this.context);
                radioButton.setText((String) choiceMaster.get("choiceTitle"));
                radioButton.setId(i+1);
                radioGroup.addView(radioButton);
            }
//            radioGroup.check(0);
            if(selectedMap.get(position*10) != null) {
                Log.i(LOG_TAG, "selected : "+selectedMap.get(position*10));
                radioGroup.check(selectedMap.get(position*10));
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Log.i(LOG_TAG, "group : "+group.getId()+", checkedId : "+checkedId);
                    selectedMap.put(group.getId(), checkedId);
                    onDataPass.onDataPass(selectedMap);
                }
            });

            holder.linearChoice.addView(radioGroup);
            return convertView;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public class ViewHolder {
        TextView txtTopicQuestion;
        LinearLayout linearChoice;
    }

}