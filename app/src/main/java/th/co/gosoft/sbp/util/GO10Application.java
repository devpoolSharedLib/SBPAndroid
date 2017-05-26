package th.co.gosoft.sbp.util;

import android.app.Application;
import android.os.Bundle;

/**
 * Created by manitkan on 21/04/16.
 */
public class GO10Application extends Application {

    private Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
