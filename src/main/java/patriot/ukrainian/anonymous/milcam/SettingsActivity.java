package patriot.ukrainian.anonymous.milcam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    public SharedPreferences milCamSettings;
    private EditText backendURLInput;
    private TextView deviceIDText;
    private Button saveSettingsButton;
    private NumberPicker makePhotoIntervalPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // views
        backendURLInput = (EditText) findViewById(R.id.backendURLInput);
        deviceIDText = (TextView) findViewById(R.id.deviceIDText);
        saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        makePhotoIntervalPicker = (NumberPicker) findViewById(R.id.makePhotoIntervalPicker);
        // -----

        // set min/max values for interval picker
        makePhotoIntervalPicker.setMinValue(1);
        makePhotoIntervalPicker.setMaxValue(60);
        // --------------------------------------

        milCamSettings = getSharedPreferences(Utils.MILCAM_PREFERENCES, Context.MODE_PRIVATE);
        // fill settings
        if (!milCamSettings.contains(Utils.MILCAM_PREFERENCES_BACKEND_URL)) {
            backendURLInput.setText(Utils.MILCAM_PREFERENCES_DEFAULT_BACKEND_URL);
        } else {
            backendURLInput.setText(milCamSettings.getString(Utils.MILCAM_PREFERENCES_BACKEND_URL, Utils.MILCAM_PREFERENCES_DEFAULT_BACKEND_URL));
        }

        if (milCamSettings.contains(Utils.MILCAM_PREFERENCES_DEVICE_ID)) {
            deviceIDText.setText(milCamSettings.getString(Utils.MILCAM_PREFERENCES_DEVICE_ID, ""));
        }

        if (milCamSettings.contains(Utils.MILCAM_PREFERENCES_MAKE_PHOTO_INTERVAL)) {
            makePhotoIntervalPicker.setValue(milCamSettings.getInt(Utils.MILCAM_PREFERENCES_MAKE_PHOTO_INTERVAL, Utils.MILCAM_PREFERENCES_DEFAULT_MAKE_PHOTO_INTERVAL));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDeviceIDButtonClick(View view) {
        String devID = Utils.getDeviceID(backendURLInput.getText().toString());
        if (devID.length() != 0) {
            deviceIDText.setText(devID);
        } else {
            // if device ID from server response empty
            Toast getDeviceIDErrorToast = Toast.makeText(getApplicationContext(), R.string.get_device_id_toast_text, Toast.LENGTH_SHORT);
            getDeviceIDErrorToast.show();
        }
    }

    public void saveSettingsButtonClick(View view) {
        saveSettingsButton.setEnabled(false);
        // if settings present
        if (backendURLInput.getText().toString().length() == 0 || deviceIDText.getText().toString().length() == 0) {
            Toast emptySettingsToast = Toast.makeText(getApplicationContext(), R.string.save_empty_settings_toast_text, Toast.LENGTH_SHORT);
            emptySettingsToast.show();
        } else {
            setSaveSettings();
            Toast settingsSavedToast = Toast.makeText(getApplicationContext(), R.string.settings_saved_toast_text, Toast.LENGTH_SHORT);
            settingsSavedToast.show();
        }
        saveSettingsButton.setEnabled(true);
    }

    public void setSaveSettings() {
        SharedPreferences.Editor editor = milCamSettings.edit();
        editor.putString(Utils.MILCAM_PREFERENCES_BACKEND_URL, backendURLInput.getText().toString());
        editor.putString(Utils.MILCAM_PREFERENCES_DEVICE_ID, deviceIDText.getText().toString());
        editor.putInt(Utils.MILCAM_PREFERENCES_MAKE_PHOTO_INTERVAL, makePhotoIntervalPicker.getValue());
        editor.apply();
    }

}
