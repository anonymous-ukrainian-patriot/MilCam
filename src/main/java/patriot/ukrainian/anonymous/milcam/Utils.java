package patriot.ukrainian.anonymous.milcam;

import android.content.Context;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class Utils {
    // some constants
    public static final String MILCAM_PREFERENCES = "milcam_settings";
    public static final String MILCAM_PREFERENCES_BACKEND_URL = "backend_url";
    public static final String MILCAM_PREFERENCES_DEVICE_ID = "device_id";
    public static final String MILCAM_PREFERENCES_MAKE_PHOTO_INTERVAL = "interval";
    public static final String MILCAM_PREFERENCES_DEFAULT_BACKEND_URL = "http://www.milcam.tk/milcam/";
    public static final int MILCAM_PREFERENCES_DEFAULT_MAKE_PHOTO_INTERVAL = 1;
    public static int INTERVAL = 60000; // 60 sec
    public static int REQUEST_CODE = 18283848;

    protected static LocationManager locationManager;
    private static String provider;

    public static String getDeviceID(String backendURL) {
        String devID = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(getDeviceIDURL(backendURL));
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
            devID = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {}

        return devID;
    }

    public static String getDeviceIDURL(String backendURL) {
        return backendURL + "device/";
    }

    public static int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static String getPictureName() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator + UUID.randomUUID().toString() + ".jpg";
    }

    public static void deletePicture(String pictureName) {
        File picture = new File(pictureName);
        picture.delete();
    }

    public static Boolean savePicture(byte [] data, String pictureName) {
        try {
            File picture = new File(pictureName);
            FileOutputStream fos = new FileOutputStream(picture);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    public static String getUploadURL(String backendURL) {
        return backendURL + "photo/";
    }

    public static void uploadPhoto(Location location, String pictureName, String uploadURL, String devID) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(uploadURL);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.RFC6532);
        File picture = new File(pictureName);
        try {
            // create POST request
            entityBuilder.addTextBody("device", devID);
            entityBuilder.addTextBody("position_0", String.valueOf(location.getLatitude()));
            entityBuilder.addTextBody("position_1", String.valueOf(location.getLongitude()));
            entityBuilder.addPart("image", new FileBody(picture));
            HttpEntity entity = entityBuilder.build();
            httpost.setEntity(entity);
            HttpResponse response = httpclient.execute(httpost);
        } catch (Exception error) {}

    }

    public static class CameraLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }

    public static Location getLocation(Context context) {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            locationManager.requestLocationUpdates(provider, 0, 0, new CameraLocationListener());
            return locationManager.getLastKnownLocation(provider);
        } catch (Exception error) {
            return null;
        }

    }

}
