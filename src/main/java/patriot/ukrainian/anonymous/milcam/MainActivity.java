package patriot.ukrainian.anonymous.milcam;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Switch;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;


public class MainActivity extends Activity {

    private Switch enableMilCamSwitch;
    public SharedPreferences milCamSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableMilCamSwitch = (Switch) findViewById(R.id.enableMilCamSwitch);
        milCamSettings = getSharedPreferences(Utils.MILCAM_PREFERENCES, Context.MODE_PRIVATE);
        // allow network operations from GUI
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void enableMilCamSwitchChange(View view) {
        if (!milCamSettings.contains(Utils.MILCAM_PREFERENCES_BACKEND_URL) || !milCamSettings.contains(Utils.MILCAM_PREFERENCES_DEVICE_ID)) {
            enableMilCamSwitch.setChecked(false);
            Toast emptySettingsToast = Toast.makeText(getApplicationContext(), R.string.empty_settings_toast_text, Toast.LENGTH_SHORT);
            emptySettingsToast.show();
        } else {
            if (enableMilCamSwitch.isChecked()) {
                startService(new Intent(this, MakePhotoService.class));
            } else {
                stopService(new Intent(this, MakePhotoService.class));
            }
        }
    }

}
