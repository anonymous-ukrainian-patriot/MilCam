package patriot.ukrainian.anonymous.milcam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Location;
import android.os.StrictMode;

public class UploadPhotoService extends BroadcastReceiver {

    private String pictureName = Utils.getPictureName();
    public SharedPreferences milCamSettings;

    @Override
    public void onReceive(Context context, Intent intent) {
        // allow network operations from service
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        milCamSettings = context.getSharedPreferences(Utils.MILCAM_PREFERENCES, Context.MODE_PRIVATE);
        int cameraId = Utils.findBackFacingCamera();

        if (cameraId >= 0) {
            Camera camera = Camera.open(cameraId);
            camera.takePicture(null, null, new savePhotoCallback(context));
        }

    }

    private class savePhotoCallback implements Camera.PictureCallback {

        private final Context context;

        public savePhotoCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Boolean picture = Utils.savePicture(data, pictureName);
                camera.release();
                Location location = Utils.getLocation(context);
                if (location != null && picture) {
                    Utils.uploadPhoto(
                            location,
                            pictureName,
                            Utils.getUploadURL(
                                    milCamSettings.getString(
                                            Utils.MILCAM_PREFERENCES_BACKEND_URL,
                                            Utils.MILCAM_PREFERENCES_DEFAULT_BACKEND_URL
                                    )
                            ),
                            milCamSettings.getString(Utils.MILCAM_PREFERENCES_DEVICE_ID, "")
                    );
                }
                Utils.deletePicture(pictureName);
            } catch (Exception error) {}
        }
    }

}
