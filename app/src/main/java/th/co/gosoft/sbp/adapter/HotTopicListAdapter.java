package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.util.DownloadImageUtils;

public class HotTopicListAdapter extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "HotTopicListAdapter";
    private Map<String, Integer> imageIdMap = new HashMap<>();

    public HotTopicListAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        generateImageToMap(imageIdMap);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        try{
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.hot_topic_row, null);

                holder.txtRowSubject = (TextView) convertView.findViewById(R.id.rowSubject);
                holder.txtLikeCount = (TextView) convertView.findViewById(R.id.txtLikeCount);
                holder.txtRowDate = (TextView) convertView.findViewById(R.id.rowDate);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iconImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> topicMap = getItem(position);

            if (topicMap != null) {
                holder.txtRowSubject.setText(topicMap.get("subject").toString());
                holder.txtLikeCount.setText(topicMap.get("countLike") == null ? "0" : topicMap.get("countLike").toString());
                holder.txtRowDate.setText(topicMap.get("date").toString());
                DownloadImageUtils.setImageRoom(getContext(), holder.imageView, topicMap.get("roomId").toString());
                Log.i(LOG_TAG,"Read : "+(Boolean) topicMap.get("statusRead"));
                if((Boolean) topicMap.get("statusRead") == false) {
                    convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorUnreadTopic));
                } else {
                    convertView.setBackgroundColor(0);
                }
            }

            return convertView;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView txtRowSubject;
        TextView txtLikeCount;
        TextView txtRowDate;
        ImageView imageView;
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

}

