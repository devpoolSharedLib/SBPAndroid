package th.co.gosoft.sbp.util;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by manitkan on 07/09/16.
 */
public class URLDrawable extends BitmapDrawable {

    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        if(drawable != null) {
            drawable.draw(canvas);
        }
    }
}
