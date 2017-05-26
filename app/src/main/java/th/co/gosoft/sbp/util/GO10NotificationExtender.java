package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.shortcutbadger.ShortcutBadger;

import cz.msebera.android.httpclient.Header;
import th.co.gosoft.sbp.R;

/**
 * Created by ASUS on 14/2/2560.
 */

public class GO10NotificationExtender extends NotificationExtenderService {

    private String LOG_TAG = "GO10Extender";
    private String GET_BADGE_NUMBER_URL;
    private SharedPreferences sharedPref;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

        try {
            boolean foreground = new GO10ForegroundCheckTask().execute(getApplicationContext()).get();
            Log.i(LOG_TAG, "IS FOREGOUD "+foreground);

            if(!foreground) {
                GET_BADGE_NUMBER_URL = PropertyUtility.getProperty("httpsUrlSite", getApplicationContext())+PropertyUtility.getProperty("contextRoot", getApplicationContext())+"api/" + PropertyUtility.getProperty("versionServer", getApplicationContext())
                        + "topic/getbadgenumbernotification";
                sharedPref = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                Log.i(LOG_TAG, "receive notification");
                String email = sharedPref.getString("empEmail", null);
                String concatString = GET_BADGE_NUMBER_URL + "?empEmail=" + email;
                SyncHttpClient client = new SyncHttpClient();
                client.get(getApplicationContext(), concatString, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        int badgeCount = Integer.parseInt(new String(responseBody));
                        Log.i(LOG_TAG, "badgeCount : " + badgeCount);
                        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e(LOG_TAG, "Error code : " + statusCode + ", " + e.getMessage(), e);
                    }
                });
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}