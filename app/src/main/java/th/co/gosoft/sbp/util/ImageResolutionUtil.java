package th.co.gosoft.sbp.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manitkan on 23/09/16.
 */

public class ImageResolutionUtil {

    public static final String LOG_TAG = "ImageResolutionUtil";

    public static Map<String,Integer> calculateResolution(int width, int height) {
        Log.i(LOG_TAG, "calculateResolution width - height : "+width+" * "+height);
        Double ratio = (Math.round(((float) width / (float) height)*100.0) / 100.0);
        Log.i(LOG_TAG, "calculateResolution Ration Bitmap : "+ratio);
        Map<String,Integer> resultMap = new HashMap<>();
        if(ratio > 1) {
            if(ratio == 1.33) {
                Log.i(LOG_TAG, "4:3 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 222);
            } else if(ratio == 1.78 || ratio == 1.77) {
                Log.i(LOG_TAG, "16:9 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 166);
            } else {
                Log.i(LOG_TAG, "16:9 landscape");
                resultMap.put("width", 295);
                resultMap.put("height", 166);
            }
        } else if(ratio < 1) {
            if(ratio == 0.75) {
                Log.i(LOG_TAG, "3:4 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 307);
            } else if(ratio == 0.56) {
                Log.i(LOG_TAG, "9:16 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 410);
            } else {
                Log.i(LOG_TAG, "9:16 portrait");
                resultMap.put("width", 230);
                resultMap.put("height", 410);
            }
        } else if(ratio == 1) {
            Log.i(LOG_TAG, "1:1 square");
            resultMap.put("width", 295);
            resultMap.put("height", 295);
        }
        return resultMap;
    }
}
