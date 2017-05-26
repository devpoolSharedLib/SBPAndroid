package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.util.BadgeViewUtil;
import th.co.gosoft.sbp.util.DownloadImageUtils;

public class RoomGridAdapter extends ArrayAdapter<Map<String, Object>> {

    private final String LOG_TAG = "RoomGridAdapter";
    private Map<String, Integer> imageIdMap = new HashMap<>();
    private Context context;

    public RoomGridAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        this.context = context;
        generateImageToMap(imageIdMap);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(this.context);
                convertView = inflater.inflate(R.layout.room_grid, null);

                holder.imgRoomIcon = (ImageView) convertView.findViewById(R.id.roomIcon);
                holder.txtRoomName = (TextView) convertView.findViewById(R.id.roomName);
                holder.badge = new BadgeViewUtil(this.context, holder.imgRoomIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> roomMap = getItem(position);

            if (roomMap != null) {
                DownloadImageUtils.setImageRoom(this.context, holder.imgRoomIcon, roomMap.get("_id").toString());

                holder.txtRoomName.setText(roomMap.get("name").toString());
                int badge = (int) roomMap.get("badgeNumber");
                if (badge > 0) {
                    holder.badge.setText(String.valueOf(roomMap.get("badgeNumber")));
                    holder.badge.show();
                } else {
                    holder.badge.hide();
                }
            }

            return convertView;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        ImageView imgRoomIcon;
        TextView txtRoomName;
        BadgeViewUtil badge;
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}