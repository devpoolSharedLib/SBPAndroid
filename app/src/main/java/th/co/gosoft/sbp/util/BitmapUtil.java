package th.co.gosoft.sbp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by manitkan on 23/05/16.
 */
public class BitmapUtil {

    public static final String LOG_TAG = "BitmapUtil";
    public static final int RESOLUTION = 250;

    public static int height;
    public static int width;

    public static Bitmap resizeBitmap(String picturePath) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, RESOLUTION, RESOLUTION);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;
        sizeOptions.inScaled = false;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        height = options.outHeight;
        width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.i(LOG_TAG, "inSampleSize : "+ inSampleSize);
        }

        return inSampleSize;
    }
}
