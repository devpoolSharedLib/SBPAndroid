package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import th.co.gosoft.sbp.R;

/**
 * Created by manitkan on 12/09/16.
 */
public class LikeButtonOnClick implements View.OnClickListener {

    private final String LOG_TAG = "LikeButtonOnClick";
    private Context context;
    private Button button;
    private boolean isClick;
    private TextView txtLikeCount;
    private int likeCount;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public LikeButtonOnClick(Context context, View button, View txtLikeCount, boolean isClick){
        this.context = context;
        this.button = (Button) button;
        this.txtLikeCount = (TextView) txtLikeCount;
        this.likeCount = Integer.parseInt(String.valueOf(this.txtLikeCount.getText()));
        Log.i(LOG_TAG, "likeCount : "+likeCount);
        this.isClick = isClick;
        sharedPref = this.context.getSharedPreferences(this.context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    @Override
    public void onClick(View v) {
        try {
            if(isClick) {
                Log.i(LOG_TAG, "Dislike");
                isClick = false;
                likeCount--;
                txtLikeCount.setText(String.valueOf(likeCount));
                saveIsStatusToSharedPreference(false);
                button.setTextColor(this.context.getResources().getColor(R.color.colorButton));
            } else {
                Log.i(LOG_TAG, "Like");
                isClick = true;
                likeCount++;
                txtLikeCount.setText(String.valueOf(likeCount));
                saveIsStatusToSharedPreference(true);
                button.setTextColor(this.context.getResources().getColor(R.color.colorLikeButton));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void saveIsStatusToSharedPreference(boolean status) {
        try{
            editor.putBoolean("like_isStatusLike",  status);
            editor.commit();
            Log.i(LOG_TAG, "Like status : "+status);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
