package patriot.ukrainian.anonymous.milcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.pm.PackageManager.NameNotFoundException;

public class AboutActivity extends Activity {

    private TextView aboutVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutVersionText = (TextView) findViewById(R.id.aboutVersionText);
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            aboutVersionText.setText(version);
        } catch (NameNotFoundException e) {}
    }

}
