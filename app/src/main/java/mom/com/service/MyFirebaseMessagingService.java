package mom.com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import mom.com.R;
import mom.com.activities.MainActivity;


/**
 * Created by NgocTri on 8/9/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final int GCM_NOTIF_ID = 0xCAFE;
    PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        String from = message.getFrom();
        Map data = message.getData();
        JSONObject jsonObject = new JSONObject(data);
//        Log.e("http fcm data ", "" + jsonObject);
//        Log.e("http fcm message ", "" + from);
//        Log.e("http fcm ", String.valueOf(data));

//        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);

//        showNotification(this);
//        sendNotificationConfirm(jsonObject.getString("orderId"), jsonObject.getString("name"), jsonObject.getString("partner_date"), jsonObject.getString("partner_time"), jsonObject.getString("totalCall"),jsonObject.getString("image_path"),jsonObject.getString("image_name"));
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("http token "+token);
        /*Map map=new HashMap<>();
        map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobileNumber());
        map.put("fcmToken",token);
        map.put("userType","delivery");
        ApiCallService.action(this,map,ApiCallService.Action.ACTION_SAVE_TOKEN);*/

    }


    private void showNotification(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setColor(Color.GREEN);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentTitle("MOM");
        mBuilder.setContentText("It's been a while you have checked out earthquake data!");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void sendNotificationConfirm(String id, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MOM")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        Intent intent1 = new Intent(getBaseContext(), MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("FROM", "3");
        startActivity(intent1);
    }


}
