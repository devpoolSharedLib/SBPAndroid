package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.util.DownloadImageUtils;
import th.co.gosoft.sbp.util.PropertyUtility;

/**
 * Created by manitkan on 27/06/16.
 */
public class RoomAdapter  extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "RoomAdapter";
    private Target target ;
    private Context context;
    private int resourceId ;
    private String URL;

    public RoomAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        this.context = context;
        URL = PropertyUtility.getProperty("httpUrlSite", context )+"GO10WebService/DownloadServlet" ;
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
                DownloadImageUtils.setImageAvatar(getContext(), holder.imageView, topicMap.get("avatarPic").toString());

                if((Boolean) topicMap.get("statusRead") == false) {
                    convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorUnreadTopic));
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

    private void setAvatarImage(final ViewHolder holder, Map<String, Object> topicMap) {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
        final String fileName = topicMap.get("avatarPic").toString();
        final File imgFile = new File(directory, fileName);
        Resources resources = context.getResources();
        Log.i(LOG_TAG, "Exits file : "+imgFile.exists());
        if(imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setTag(bitmap);
        } else if(isExitInDrawable(fileName)) {
            Log.i(LOG_TAG,"ELSF IF : "+resourceId);
            resourceId = resources.getIdentifier(topicMap.get("avatarPic").toString(), "drawable",
                    context.getPackageName());
            Log.i(LOG_TAG, "Resource : " + resourceId);
            holder.imageView.setImageResource(resourceId);
        } else {
            final ViewHolder finalHolder = holder;
            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(imgFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        finalHolder.imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            String imageURL = URL + "?imageName="+fileName;
            Log.i(LOG_TAG,"myImage : "+imageURL);
            Picasso.with(context)
                    .load(imageURL)
                    .into(target);
        }
    }

    private boolean isExitInDrawable(String fileName) {
        Resources resources = context.getResources();
        resourceId = resources.getIdentifier(fileName, "drawable",
                context.getPackageName());
        return resourceId != 0;

    }

    private static class ViewHolder {
        TextView txtRowSubject;
        TextView txtLikeCount;
        TextView txtRowDate;
        ImageView imageView;
    }
}
