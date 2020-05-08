package com.kiprogram.kitimetable.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.db.helper.KiSQLiteOpenHelper;
import com.kiprogram.kitimetable.db.table.Subject;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;
import com.kiprogram.kitimetable.sp.KiSpKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EveryMorningAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "default";

    private static Map<Integer, List<KiSpKey>> DAY_OF_WEEK_TO_SP_KEY_CLASSES = new HashMap<Integer, List<KiSpKey>>() {
        {
            put(Calendar.MONDAY, new ArrayList<KiSpKey>() {
                {
                    add(KiSpKey.MON_FIRST_SUBJECT_ID);
                    add(KiSpKey.MON_SECOND_SUBJECT_ID);
                    add(KiSpKey.MON_THIRD_SUBJECT_ID);
                    add(KiSpKey.MON_FOURTH_SUBJECT_ID);
                }
            });
            put(Calendar.TUESDAY, new ArrayList<KiSpKey>() {
                {
                    add(KiSpKey.TUE_FIRST_SUBJECT_ID);
                    add(KiSpKey.TUE_SECOND_SUBJECT_ID);
                    add(KiSpKey.TUE_THIRD_SUBJECT_ID);
                    add(KiSpKey.TUE_FOURTH_SUBJECT_ID);
                }
            });
            put(Calendar.WEDNESDAY, new ArrayList<KiSpKey>() {
                {
                    add(KiSpKey.WED_FIRST_SUBJECT_ID);
                    add(KiSpKey.WED_SECOND_SUBJECT_ID);
                    add(KiSpKey.WED_THIRD_SUBJECT_ID);
                    add(KiSpKey.WED_FOURTH_SUBJECT_ID);
                }
            });
            put(Calendar.THURSDAY, new ArrayList<KiSpKey>() {
                {
                    add(KiSpKey.THU_FIRST_SUBJECT_ID);
                    add(KiSpKey.THU_SECOND_SUBJECT_ID);
                    add(KiSpKey.THU_THIRD_SUBJECT_ID);
                    add(KiSpKey.THU_FOURTH_SUBJECT_ID);
                }
            });
            put(Calendar.FRIDAY, new ArrayList<KiSpKey>() {
                {
                    add(KiSpKey.FRI_FIRST_SUBJECT_ID);
                    add(KiSpKey.FRI_SECOND_SUBJECT_ID);
                    add(KiSpKey.FRI_THIRD_SUBJECT_ID);
                    add(KiSpKey.FRI_FOURTH_SUBJECT_ID);
                }
            });
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        // 土曜日、日曜日の場合は再通知をいれて終了
        if (dow == Calendar.SATURDAY
                || dow == Calendar.SUNDAY) {
            // 再通知
            Intent reIntent = new Intent(context, EveryMorningAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, reIntent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            if(am != null){
                Calendar alarm = getAlarmTime();
                am.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pi);
            }
            return;
        }

        // タイトル
        String title = context.getString(R.string.app_name);

        // メッセージ
        StringBuilder message = new StringBuilder();
        List<KiSpKey> spKeyClassList = DAY_OF_WEEK_TO_SP_KEY_CLASSES.get(dow);
        KiSQLiteOpenHelper oh = new KiSQLiteOpenHelper(context);
        KiSharedPreferences sp = new KiSharedPreferences(context);
        try {
            for (int i = 0; i < spKeyClassList.size(); i++) {
                String subjectId = sp.getString(spKeyClassList.get(i));
                message.append(i + 1).append("限目:");
                if (subjectId == null) {
                    message.append("無し ");
                } else {
                    EnumMap<Subject.Field, CharSequence> param = new EnumMap<>(Subject.Field.class);
                    param.put(Subject.Field.ID, sp.getString(spKeyClassList.get(i)));
                    Subject subject = new Subject(oh, param);
                    message.append(subject.getValue(Subject.Field.NAME)).append(" ");
                }
            }
        } finally {
            oh.close();
        }

        // 音設定
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Notification Channel 設定
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title , NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(message.toString());
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setSound(defaultSoundUri, null);
        channel.setShowBadge(true);

        // NotificationManager 取得
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    // android標準アイコンから
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .build();

            // 通知
            notificationManager.notify(R.string.app_name, notification);
        }

        // 再通知
        Intent reIntent = new Intent(context, EveryMorningAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, reIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        if(am != null){
            Calendar alarm = getAlarmTime();
            am.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pi);
        }
    }

    public static Calendar getAlarmTime() {
        // 現在時刻を取得
        Calendar now = Calendar.getInstance();
        // アラーム時間設定用 (朝7時)
        Calendar alarm = Calendar.getInstance();
        alarm.set(Calendar.SECOND, 0);
        alarm.set(Calendar.MINUTE, 0);
        alarm.set(Calendar.HOUR_OF_DAY, 7);

        // 現在時刻とその日のアラーム時間と比較
        if (now.compareTo(alarm) >= 0) {
            // アラーム時間を過ぎていれば、次の日のアラーム時間に設定 土日についてはアラーム側で制御
            alarm.add(Calendar.DATE, 1);
        }
        return alarm;
    }
}
