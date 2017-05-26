package th.co.gosoft.sbp.util;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import androidmads.updatehandler.app.UpdateHandler;

/**
 * Created by Plooer on 1/23/2017 AD.
 */

public class CheckUpdateUtil {
    private final String LOG_TAG = "CheckUpdate";

    public void checkUpdateVersion(AppCompatActivity activity) {
        Log.i( LOG_TAG, "checkUpdateVersion" );

        UpdateHandler updateHandler = new UpdateHandler(activity);
        updateHandler.start();
        updateHandler.showDefaultAlert(true);
    }
}
