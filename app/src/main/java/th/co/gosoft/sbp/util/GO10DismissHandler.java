package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatus;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatusListener;

/**
 * Created by ASUS on 26/1/2560.
 */

public class GO10DismissHandler extends MFPPushNotificationDismissHandler {
    private String LOG_TAG = "DismissHandler" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        MFPPush.getInstance().setNotificationStatusListener(new MFPPushNotificationStatusListener() {
            @Override
            public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
                Log.d(LOG_TAG, "Dismiss Status in HANDLER : "+status);
            }
        });
        super.onReceive(context, intent);
    }
}