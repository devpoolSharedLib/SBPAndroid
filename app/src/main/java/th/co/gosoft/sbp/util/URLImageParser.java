package th.co.gosoft.sbp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by manitkan on 08/09/16.
 */
public class URLImageParser implements Html.ImageGetter {

    private final String LOG_TAG = "URLImageParser";
    private TextView mTv;
    Context container;

    public URLImageParser(TextView t, Context c) {
        this.mTv = t;
        this.container = c;
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable levelListDrawable = new LevelListDrawable();
        URLDrawable emptyDrawable = new URLDrawable();
        levelListDrawable.addLevel(0, 0, emptyDrawable);
        levelListDrawable.setBounds(0, 0, emptyDrawable.getIntrinsicWidth(), emptyDrawable.getIntrinsicHeight());

        new LoadImage().execute(source, levelListDrawable);

        return levelListDrawable;
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.i(LOG_TAG, "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i(LOG_TAG, "onPostExecute drawable " + mDrawable);
            Log.i(LOG_TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                Log.i(LOG_TAG, "Bitmap size : "+bitmap.getWidth()+", "+bitmap.getHeight());

                float scaleFactor = (float)mTv.getWidth()/(float)bitmap.getWidth();
                Log.i(LOG_TAG, "scaleFactor : "+scaleFactor);

                mDrawable.setBounds(0, 0, mTv.getWidth(), Math.round(bitmap.getHeight()*scaleFactor));
                mDrawable.setLevel(1);
                CharSequence t = mTv.getText();
                mTv.setText(t);
            }
        }
    }
}
