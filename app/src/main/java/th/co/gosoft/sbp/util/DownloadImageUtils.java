package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by manitkannika on 2/20/2017 AD.
 */

public class DownloadImageUtils {

    private static final String LOG_TAG = "DownloadImageUtils";
    private static int resourceId ;
    private Context context;

    public static void setImageAvatar(Context context, ImageView imageView, String imageName) {
        String URL = PropertyUtility.getProperty("httpUrlSite", context )+PropertyUtility.getProperty("contextRoot", context )+"DownloadServlet";
        getResourceFromURL(context, imageView, imageName, URL, false);
    }

    public static void setImageRoom(Context context, ImageView imageView, String imageName) {
        String URL = PropertyUtility.getProperty("httpUrlSite", context )+PropertyUtility.getProperty("contextRoot", context )+"DownloadServlet";
        getResourceFromURL(context, imageView, imageName, URL, true);
    }

    private static void getResourceFromURL(final Context context, final ImageView imageView, String imageName, String URL, boolean flag) {
        final Resources resources = context.getResources();
        if(isExitInDrawable(context, imageName)) {
            resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
            imageView.setImageResource(resourceId);
        } else {
            if(flag) {
                imageName = concatFileType(imageName);
            }
            String imageURL = URL + "?imageName="+imageName;
            Log.i(LOG_TAG,"Loading Image : "+imageURL);

            if(flag) {
                Picasso.with(context)
                        .load(imageURL)
                        .into(imageView);
            } else {
                Picasso.with(context)
                        .load(imageURL)
                        .transform(new CropCircleTransformation())
                        .into(imageView);
            }

        }
    }

    private static String concatFileType(String imageName) {
        return imageName+".png";
    }

    private static boolean isExitInDrawable(Context context, String fileName) {
        Resources resources = context.getResources();
        resourceId = resources.getIdentifier(fileName, "drawable",
                context.getPackageName());
        return resourceId != 0;
    }

}
