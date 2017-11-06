package com.example.a502.drawex;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soo on 2017-10-14.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "GeofenceTransitionsIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // TAG가 작업 Thread의 이름이 된다.
        super(TAG);
    }

    /**
     * 들어오는 인텐트를 처리한다.
     * @param intent addGeofences가 호출 될 때 새로운 인텐트가 위치서비스에 제공 된다.
     *               여기서는 그 인텐트가 하는 일을 정의한다.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "geofence error");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // 지오펜스 범위 내일때, 상태가 어떻게 바뀌었는지 체크한다. 만약 들어온 상태로 바뀌었으면 체크!
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // 트리거 된 지오펜스의 리스트.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // 상태전환정보를 로그로 출력하고 알림을 보내자.
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "error");
        }
    }

    /**
     * Transition에 대한 세부사항을 가져오고 서식이 지정된 문자열로 반환.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // 트리거 된 지오펜스들의 ID 리스트를 뽑아낸다.
        // 우리 프로그램에서는 가게코드 리스트가 될 것이다.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }

        //들어왔다면 Enter : 들어온 지오펜스 리스트를 리턴한다.
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /*
     * 전환이 감지 될 떄 알림 표시줄에 알림을 게시한다.
     * 사용자가 알림을 클릭하면 MainActivity 창이 띄워진다.
     */
    private void sendNotification(String notificationDetails) {
        // 메인 액티비티를 시작하는 explicit content Intent를 만들자.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack. 테스크 스택을 만든다.
        // 요 녀석이 알림을 할 녀석이다.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        // 메인액티비티를 부모로 한다. 이 알림을 누르면 메인으로 가도록 설정.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // 알림 세팅!
        builder.setSmallIcon(R.drawable.icon)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.icon))
                .setColor(Color.RED)
                .setContentTitle("QPhone")
                .setContentText("근처에 쿠폰이 거의 다 모인 단골가게가 있어요!")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "ENTER";
            default:
                return "UNKNOWN";
        }
    }
}
