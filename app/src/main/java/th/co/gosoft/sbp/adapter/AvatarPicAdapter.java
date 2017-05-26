package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import th.co.gosoft.sbp.R;

/**
 * Created by manitkan on 06/06/16.
 */
public class AvatarPicAdapter  extends ArrayAdapter<Integer> {

    private final String LOG_TAG = "AvatarPicAdapter";

    public AvatarPicAdapter(Context context, int resource, List<Integer> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater;
                inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.avatar_grid, null);

                holder.avatarPic  = (ImageView) convertView.findViewById(R.id.avatarPic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int resourceId = getItem(position);
            holder.avatarPic.setImageResource(resourceId);

            return convertView;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class ViewHolder {
        ImageView avatarPic;
    }

}
