package th.co.gosoft.sbp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import th.co.gosoft.sbp.R;

/**
 * Created by manitkan on 06/06/16.
 */
public class SettingAvatarAdapter extends BaseAdapter {

    private Context context;
    private String avatarName;

    public SettingAvatarAdapter (Context context, String avatarName) {
        this.context = context;
        this.avatarName = avatarName;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return this.avatarName;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.setting_avatar_name_row, null);

        TextView textView = (TextView) convertView.findViewById(R.id.txtAvatarName);
        textView.setText(avatarName);

        return convertView;
    }

}
