package th.co.gosoft.sbp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import th.co.gosoft.sbp.BuildConfig;
import th.co.gosoft.sbp.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView txtTermsAndPolicies = (TextView) findViewById(R.id.txtTermsAndPolicies);
        txtTermsAndPolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, TermsAndPoliciesActivity.class);
                startActivity(intent);
            }
        });
        TextView txtAppVersion = (TextView) findViewById(R.id.app_version);
        txtAppVersion.setText("version "+BuildConfig.VERSION_NAME);
    }
}
