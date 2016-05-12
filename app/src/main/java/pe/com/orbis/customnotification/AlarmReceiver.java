package pe.com.orbis.customnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by carlos on 11/05/16.
 * Alias: CarlitosDroid
 */
public class AlarmReceiver extends BroadcastReceiver{

    private final static String sample_url = "http://www.blackenterprise.com/files/2010/12/android-gingerbread-logo1.jpg";

    private static NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/ding");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        new CreateNotification(context).execute();
    }

    /**
     * Notification AsyncTask to create and return the
     * requested notification.
     *
     * @see CreateNotification#CreateNotification(Context)
     */
    public class CreateNotification extends AsyncTask<Void, Void, Void> {

        Context context;

        public CreateNotification(Context context) {
            this.context = context;
        }

        /**
         * Creates the notification object.
         */
        @Override
        protected Void doInBackground(Void... params) {
            Notification noti;

            noti = setSmallIconStyleNotification(context);

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            mNotificationManager.notify(0, noti);

            return null;

        }
    }

    private Notification setSmallIconStyleNotification(Context context) {
        Bitmap remote_picture = null;

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);

        // TaskStackBuilder ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_android_green_500_24dp)
                .setColor(ContextCompat.getColor(context,R.color.colorAccent))
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "One", resultPendingIntent)
                .addAction(R.drawable.ic_explore_orange_500_24dp, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Three", resultPendingIntent)
                .setContentTitle("Normal Notification")
                .setContentText("This is an example of a Normal Style.").build();
    }
}
