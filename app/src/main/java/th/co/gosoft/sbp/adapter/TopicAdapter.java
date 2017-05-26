package th.co.gosoft.sbp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import th.co.gosoft.sbp.R;
//import th.co.gosoft.go10.fragment.PollFragment;
import th.co.gosoft.sbp.model.LikeModel;
import th.co.gosoft.sbp.util.DownloadImageUtils;
import th.co.gosoft.sbp.util.LikeButtonOnClick;
import th.co.gosoft.sbp.util.OnDataPass;
import th.co.gosoft.sbp.util.PropertyUtility;
import th.co.gosoft.sbp.util.URLImageParser;

public class TopicAdapter extends BaseAdapter {

    private final String LOG_TAG = "TopicAdapter";
    private final String TYPE_HOST = "host";

    private Context context;
    private Map<Integer,Integer> rowLayoutMap;
    private List<Map> topicModelMapList;
    private LikeModel likeModel;
    private List<Map> pollModelMap;
    private Integer countAcceptPoll;
    private boolean isClick = false;
    private ViewHolder holder = null;
    private OnDataPass onDataPass;
    private boolean canComment;
    private LayoutInflater layoutInflater;
    private String URL ;
    private ProgressDialog progress;
    private SharedPreferences sharedPref;
    private String empEmail;


    public TopicAdapter(Context context, OnDataPass onDataPass, List<Map> topicMap, LikeModel likeModel, boolean canComment) {
        URL = PropertyUtility.getProperty("httpUrlSite", context)+PropertyUtility.getProperty("contextRoot", context)+"api/"+ PropertyUtility.getProperty("versionServer", context)
                +"topic/deleteObj";
        this.layoutInflater =  LayoutInflater.from(context);
        this.topicModelMapList = (List<Map>) topicMap.get(0).get("boardContentList");
        this.pollModelMap = (List<Map>) topicMap.get(0).get("pollModel");
        this.countAcceptPoll = (Integer) topicMap.get(0).get("countAcceptPoll");
        this.context = context;
        this.likeModel = likeModel;
        this.onDataPass = onDataPass;
        this.canComment = canComment;
        rowLayoutMap = new HashMap<>();
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        if (canComment) {
            rowLayoutMap.put(0, R.layout.host_row_can_comment);
        } else {rowLayoutMap.put(0, R.layout.host_row_not_comment);
        }
        rowLayoutMap.put(1, R.layout.comment_row);
    }

    @Override
    public int getCount() {
        return topicModelMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicModelMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (topicModelMapList.get(position).get("type").equals(TYPE_HOST)) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return rowLayoutMap.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            Log.i(LOG_TAG, "Position : "+position);

            final Map<String, Object> topicModelMap = (Map<String, Object>) getItem(position);
            Log.i(LOG_TAG, "Item Type : "+topicModelMap.get("type"));

            int rowType = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                if(rowType == 0) {
                    empEmail = sharedPref.getString("empEmail", null);
                    convertView = layoutInflater.inflate(rowLayoutMap.get(0), null);
                    holder.subject = (TextView) convertView.findViewById(R.id.hostSubject);
                    holder.content = (TextView) convertView.findViewById(R.id.hostContent);
                    holder.user = (TextView) convertView.findViewById(R.id.hostUsername);
                    holder.date = (TextView) convertView.findViewById(R.id.hostTime);
                    holder.likeCount = (TextView) convertView.findViewById(R.id.txtLikeCount);
                    holder.imageView =(ImageView) convertView.findViewById(R.id.hostImage);
                    holder.btnLike = (Button) convertView.findViewById(R.id.btnLike);
                    holder.btnComment = (Button) convertView.findViewById(R.id.btnComment);
                    holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);

                    if(this.pollModelMap != null){
                        holder.countAcceptIcon = (ImageView) convertView.findViewById(R.id.countAcceptIcon);
                        holder.countAcceptIcon.setVisibility(View.VISIBLE);
                        holder.countAcceptPoll = (TextView) convertView.findViewById(R.id.countAcceptPoll);
                        holder.countAcceptPoll.setVisibility(View.VISIBLE);
                    }
                    
                } else if(rowType == 1) {
                    convertView = layoutInflater.inflate(rowLayoutMap.get(1), null);
                    holder.content = (TextView) convertView.findViewById(R.id.commentContent);
                    holder.user = (TextView) convertView.findViewById(R.id.commentUsername);
                    holder.date = (TextView) convertView.findViewById(R.id.commentTime);
                    holder.imageView =(ImageView) convertView.findViewById(R.id.commentImage);
                    holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (rowType == 0) {
                holder.subject.setText((String) topicModelMap.get("subject"));
                URLImageParser urlImageParser = new URLImageParser(holder.content, this.context);
                Spanned htmlSpan = Html.fromHtml((String) topicModelMap.get("content"), urlImageParser, null);
                holder.content.setText(htmlSpan);
                holder.user.setText((String) topicModelMap.get("avatarName"));
                holder.date.setText((String) topicModelMap.get("date"));
                Log.i(LOG_TAG, "countLike : "+topicModelMap.get("countLike"));
                holder.likeCount.setText(topicModelMap.get("countLike") == null ? "0" : String.valueOf((Integer) topicModelMap.get("countLike")));
                DownloadImageUtils.setImageAvatar(context, holder.imageView, topicModelMap.get("avatarPic").toString());

                if(this.pollModelMap != null) {
                holder.countAcceptPoll.setText((countAcceptPoll == null ? "0" : String.valueOf(countAcceptPoll)));
                }
                if(likeModel != null && likeModel.isStatusLike()){
                    holder.btnLike.setTextColor(this.context.getResources().getColor(R.color.colorLikeButton));
                    isClick = true;
                }
                holder.btnLike.setOnClickListener(new LikeButtonOnClick(this.context, holder.btnLike, holder.likeCount, isClick));
                if(canComment){
                    holder.btnComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDataPass.onDataPass("comment");
                        }
                    });
                }
                if(empEmail.equals(topicModelMap.get("empEmail"))) {
                    holder.btnDelete.setVisibility(View.VISIBLE);
                    Log.i(LOG_TAG,"EMAIL : "+empEmail.equals(topicModelMap.get("empEmail")));
                    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final CharSequence settingAvatar[] = new CharSequence[]{"Delete"};
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            Log.i(LOG_TAG, "Position : " + position);
                            builder.setItems(settingAvatar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String selected = (String) settingAvatar[which];
                                    if (selected.toString().equals("Delete")) {
                                        Log.i(LOG_TAG, "Topic : " + topicModelMap);
                                        callPostWebService(topicModelMap,true);
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
                }else{
                    holder.btnDelete.setVisibility(View.INVISIBLE);
                }
            } else if(rowType == 1) {
                URLImageParser urlImageParser = new URLImageParser(holder.content, this.context);
                Spanned htmlSpan = Html.fromHtml((String) topicModelMap.get("content"), urlImageParser, null);
                holder.content.setText(htmlSpan);
                holder.user.setText((String) topicModelMap.get("avatarName"));
                holder.date.setText((String) topicModelMap.get("date"));
                DownloadImageUtils.setImageAvatar(context, holder.imageView, topicModelMap.get("avatarPic").toString());
                if(empEmail.equals(topicModelMap.get("empEmail"))) {
                    holder.btnDelete.setVisibility(View.VISIBLE);
                    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final CharSequence settingAvatar[] = new CharSequence[]{"Delete"};
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setItems(settingAvatar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String selected = (String) settingAvatar[which];
                                    Log.i(LOG_TAG, "Position : " + position);
                                        if (selected.toString().equals("Delete")) {
                                        Log.i(LOG_TAG, "Comment : " + topicModelMap);
                                        callPostWebService(topicModelMap,false);
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
                }else{
                    holder.btnDelete.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        TextView subject;
        TextView content;
        TextView user;
        TextView date;
        TextView likeCount;
        ImageView imageView;
        Button btnLike;
        Button btnComment;
        ImageButton btnDelete;
        TextView countAcceptPoll;
        ImageView countAcceptIcon;
    }

    private void callPostWebService(Object topicModelMap, final boolean flag){
        Log.i(LOG_TAG,"TOPIC MODEL : "+topicModelMap);
        try {
            String jsonString = new ObjectMapper().writeValueAsString(topicModelMap);
            Log.i(LOG_TAG, jsonString);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, URL, new StringEntity(jsonString,"utf-8"),
                    RequestParams.APPLICATION_JSON, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            showLoadingDialog();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            Log.i(LOG_TAG, String.format(Locale.US, "Return Status Code: %d", statusCode));
                            Log.i(LOG_TAG, "New id : "+new String(response));
                            closeLoadingDialog();
                            if(flag){
                                callBackActivity();
                               }else{
                                onDataPass.onDataPass("refresh");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                            Log.e(LOG_TAG, "Error code : " + statusCode + ", " + e.getMessage(), e);
                            closeLoadingDialog();
                        }
                    });

        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonProcessingException : "+e.getMessage(), e);
            showErrorDialog().show();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    private void showLoadingDialog() {
        progress = ProgressDialog.show(context, null,
                "Processing", true);
    }

    private void closeLoadingDialog(){
        progress.dismiss();
    }

    private android.app.AlertDialog.Builder showErrorDialog(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context);
        alert.setMessage("Error while loading content.");
        alert.setCancelable(true);
        return alert;
    }

    private void callBackActivity() {
        Activity activity = (Activity) context;
        FragmentManager fragmentManager = activity.getFragmentManager();
        Log.i(LOG_TAG,"backStackName : "+fragmentManager.getBackStackEntryCount());
        FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
        String str = backEntry.getName();

        Log.i(LOG_TAG,"backStackName : "+str);
        if(str.equals("tag")){
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack("tag",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

}