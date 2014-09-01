package patriot.ukrainian.anonymous.milcam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

public class MakePhotoService extends Service {
    public MakePhotoService() {
    }

    private final IBinder mBinder = new LocalBinder();
    public SharedPreferences milCamSettings;

    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        startService();
    }

    @Override
    public void onDestroy() {
        if (alarmManager != null) {
            Intent intent = new Intent(this, UploadPhotoService.class);
            alarmManager.cancel(PendingIntent.getBroadcast(this, Utils.REQUEST_CODE, intent, 0));
        }
    }

    private void startService() {

        Intent intent = new Intent(this, UploadPhotoService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Utils.REQUEST_CODE, intent, 0);
        milCamSettings = getSharedPreferences(Utils.MILCAM_PREFERENCES, Context.MODE_PRIVATE);
        int INTERVAL = Utils.INTERVAL * milCamSettings.getInt(Utils.MILCAM_PREFERENCES_MAKE_PHOTO_INTERVAL, Utils.MILCAM_PREFERENCES_DEFAULT_MAKE_PHOTO_INTERVAL);
        int FIRST_RUN = Utils.INTERVAL / 2;

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + FIRST_RUN, INTERVAL, pendingIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class LocalBinder extends Binder {
        public MakePhotoService getService() {
          return MakePhotoService.this;
        }
    }

}
