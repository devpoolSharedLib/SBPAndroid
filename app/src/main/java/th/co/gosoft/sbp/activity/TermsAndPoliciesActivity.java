package th.co.gosoft.sbp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import th.co.gosoft.sbp.R;

public class TermsAndPoliciesActivity extends AppCompatActivity {

    private final String LOG_TAG = "TermsAndPolicies";
    TextView termsAndPolicies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_policies_activity);
        Log.i(LOG_TAG, "onCreate()");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.terms_and_policies);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}
