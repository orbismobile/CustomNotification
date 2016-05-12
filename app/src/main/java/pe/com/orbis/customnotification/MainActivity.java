package pe.com.orbis.customnotification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by carlos on 11/05/16.
 * Alias: CarlitosDroid
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final static int RQS_1 = 1;
    private final static String sample_url = "http://www.blackenterprise.com/files/2010/12/android-gingerbread-logo1.jpg";
    private final static int NORMAL = 0x00;
    private final static int BIG_TEXT_STYLE = 0x01;
    private final static int BIG_PICTURE_STYLE = 0x02;
    private final static int INBOX_STYLE = 0x03;

    private static NotificationManager mNotificationManager;

    Button btnSmallIconStyle;
    Button btnNormalStyle;
    Button btnBigTextStyle;
    Button btnBigPictureStyle;
    Button btnInboxStyle;
    Button btnCustomView;
    TextView textAlarmPrompt;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAlarmPrompt = (TextView) findViewById(R.id.textAlarmPrompt);
        btnSmallIconStyle = (Button) findViewById(R.id.btnSmallIconStyle);
        btnNormalStyle = (Button) findViewById(R.id.btnNormalStyle);
        btnBigTextStyle = (Button) findViewById(R.id.btnBigTextStyle);
        btnBigPictureStyle = (Button) findViewById(R.id.btnBigPictureStyle);
        btnInboxStyle = (Button) findViewById(R.id.btnInboxStyle);
        btnCustomView = (Button) findViewById(R.id.btnCustomView);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        btnNormalStyle.setOnClickListener(this);
        btnSmallIconStyle.setOnClickListener(this);
        btnBigTextStyle.setOnClickListener(this);
        btnBigPictureStyle.setOnClickListener(this);
        btnInboxStyle.setOnClickListener(this);
        btnCustomView.setOnClickListener(this);
    }

    private void openTimePickerDialog(boolean is24r){
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                R.style.DialogTheme,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener
            = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if(calSet.compareTo(calNow) <= 0){
                //Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet);
        }};

    private void setAlarm(Calendar targetCal){
        textAlarmPrompt.setText(getString(R.string.programmed_alarm_at, targetCal.getTime()));
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSmallIconStyle:
                openTimePickerDialog(false);
                break;
            case R.id.btnNormalStyle:
                setNormalStyle(MainActivity.this);
                break;
            case R.id.btnBigTextStyle:
                setBigTextStyle(MainActivity.this);
                break;
            case R.id.btnBigPictureStyle:
                setBigPictureStyle(MainActivity.this);
                break;
            case R.id.btnInboxStyle:
                setInboxStyle(MainActivity.this);
                break;
            case R.id.btnCustomView:
                setCustomViewNotification(MainActivity.this);
                break;
            default:
                break;
        }
    }

    public void setNormalStyle(Context context) {
        new CreateNotification(context, NORMAL).execute();
    }

    public void setBigTextStyle(Context context) {
        new CreateNotification(context, BIG_TEXT_STYLE).execute();
    }

    public void setBigPictureStyle(Context context) {
        new CreateNotification(context, BIG_PICTURE_STYLE).execute();
    }

    public void setInboxStyle(Context context) {
        new CreateNotification(context, INBOX_STYLE).execute();
    }

    /**
     * Notification AsyncTask to create and return the
     * requested notification.
     *
     * @see CreateNotification#CreateNotification(Context ,int)
     */
    public class CreateNotification extends AsyncTask<Void, Void, Void> {

        int style = NORMAL;
        Context context;

        /**
         * Main constructor for AsyncTask that accepts the parameters below.
         * @see #doInBackground
         */
        public CreateNotification(Context context, int style) {
            this.context = context;
            this.style = style;
        }

        /**
         * Creates the notification object.
         *
         * @see #setNormalNotification
         * @see #setBigTextStyleNotification
         * @see #setBigPictureStyleNotification
         * @see #setInboxStyleNotification
         */
        @Override
        protected Void doInBackground(Void... params) {
            Notification noti = new Notification();

            switch (style)
            {
                case NORMAL:
                    noti = setNormalNotification(context);
                    break;
                case BIG_TEXT_STYLE:
                    noti = setBigTextStyleNotification(context);
                    break;
                case BIG_PICTURE_STYLE:
                    noti = setBigPictureStyleNotification(context);
                    break;
                case INBOX_STYLE:
                    noti = setInboxStyleNotification(context);
                    break;
                default:
                    break;
            }

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            mNotificationManager.notify(0, noti);

            return null;
        }
    }

    /**
     * Normal Notification
     *
     * @return Notification
     * @see CreateNotification
     */
    private Notification setNormalNotification(Context context) {
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
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "One", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Three", resultPendingIntent)
                .setContentTitle("Normal Notification")
                .setContentText("This is an example of a Normal Style.").build();
    }

    /**
     * Big Text Style Notification
     *
     * @return Notification
     * @see CreateNotification
     */
    private Notification setBigTextStyleNotification(Context context) {
        Bitmap remote_picture = null;

        // Create the style object with BigTextStyle subclass.
        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
        notiStyle.setBigContentTitle("Big Text Expanded");
        notiStyle.setSummaryText("Nice big text.");

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the big text to the style.
        CharSequence bigText = "This is an example of a large string to demo how much " +
                "text you can show in a 'Big Text Style' notification.";
        notiStyle.bigText(bigText);

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_android_green_500_24dp)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "One", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Three", resultPendingIntent)
                .setContentTitle("Big Text Normal")
                .setContentText("This is an example of a Big Text Style.")
                .setStyle(notiStyle).build();
    }

    /**
     * Big Picture Style Notification
     *
     * @return Notification
     * @see CreateNotification
     */
    private Notification setBigPictureStyleNotification(Context context) {
        Bitmap remote_picture = null;

        // Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle("Big Picture Expanded");
        notiStyle.setSummaryText("Nice big picture.");

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_android_green_500_24dp)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Completed", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Verify", resultPendingIntent)
                .setContentTitle("Big Picture Normal")
                .setContentText("This is an example of a Big Picture Style.")
                .setStyle(notiStyle).build();
    }

    /**
     * Inbox Style Notification
     *
     * @return Notification
     * @see CreateNotification
     */
    private Notification setInboxStyleNotification(Context context) {
        Bitmap remote_picture = null;

        // Create the style object with InboxStyle subclass.
        NotificationCompat.InboxStyle notiStyle = new NotificationCompat.InboxStyle();
        notiStyle.setBigContentTitle("Inbox Style Expanded");

        // Add the multiple lines to the style.
        // This is strictly for providing an example of multiple lines.
        for (int i=0; i < 5; i++) {
            notiStyle.addLine("(" + i + " of 6) Line one here.");
        }
        notiStyle.setSummaryText("+2 more Line Samples");

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(sample_url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_android_green_500_24dp)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "One", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_bug_report_blue_500_24dp, "Three", resultPendingIntent)
                .setContentTitle("Inbox Style Normal")
                .setContentText("This is an example of a Inbox Style.")
                .setStyle(notiStyle).build();
    }

    /**
     * Custom View Notification
     *
     * @see CreateNotification
     */

    public void setCustomViewNotification(Context context){

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(context, MainActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        long when = System.currentTimeMillis();

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_custom_remote);
        contentView.setImageViewResource(R.id.image, R.drawable.image_monkey);
        contentView.setTextViewText(R.id.title, "Custom notification");
        contentView.setTextViewText(R.id.text, "This is a custom layout");

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_android_green_500_24dp)
                .setContent(contentView)
                .setLargeIcon(convertImageToBitmap(R.drawable.ic_android_green_500_24dp))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setWhen(when)
                .setContentTitle("Custom Notification").build();

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        notification.bigContentView = contentView;

        mNotificationManager.notify(1, notification);

    }

    public Bitmap convertImageToBitmap(int resource){
        return BitmapFactory.decodeResource(getResources(), resource);
    }

}
