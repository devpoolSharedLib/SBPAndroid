package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by manitkan on 21/09/16.
 */
public class PropertyUtility {

    public static String getProperty(String key, Context context) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
