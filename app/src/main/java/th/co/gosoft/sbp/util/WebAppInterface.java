package th.co.gosoft.sbp.util;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by manitkan on 14/06/16.
 */
public class WebAppInterface
{
    private final String LOG_TAG = "WebAppInterface";

    @JavascriptInterface
    public void callback(String value)
    {
        Log.v(LOG_TAG, "SELECTION:" + value);
    }
}
