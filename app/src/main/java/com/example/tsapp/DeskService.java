package com.example.tsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;

/**
 * 描述:
 * <p>
 * Created by allens on 2018/1/31.
 */

public class DeskService extends Service {

    private static final String TAG = "DaemonService";
    public static final int NOTICE_ID = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");

        String ID = "com.example.tsapp";	//这里的id里面输入自己的项目的包的路径
        String NAME = "Channel One";
        Intent intent = new Intent(DeskService.this, MainActivityTBSX5.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification; //创建服务对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "DaemonService---->onCreate被调用，我是大于安卓8的");
            NotificationChannel channel = new NotificationChannel(ID, NAME, manager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(DeskService.this,"default").setChannelId(ID);
        } else {
            notification = new NotificationCompat.Builder(DeskService.this,"default");
        }
        notification.setContentTitle("听书app")
                .setContentText("我是他的后台")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentIntent(pendingIntent)
                .build();
        Notification notification1 = notification.build();
        startForeground(NOTICE_ID,notification1);

//        //如果API大于18，需要弹出一个可见通知
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            Notification.Builder builder = new Notification.Builder(this);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//            builder.setContentTitle("KeepAppAlive");
//            builder.setContentText("DaemonService is runing...");
//            startForeground(NOTICE_ID, builder.build());
//            // 如果觉得常驻通知栏体验不好
//            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
//            Intent intent = new Intent(this, CancelNoticeService.class);
//            startService(intent);
//        } else {
//            startForeground(NOTICE_ID, new Notification());
//        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");

        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "DaemonService---->onCreate被调用，我是大于安卓8的");
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), DeskService.class);
        startService(intent);
    }
}

