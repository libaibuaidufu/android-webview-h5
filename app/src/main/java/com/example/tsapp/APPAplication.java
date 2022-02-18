package com.example.tsapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class APPAplication extends Application {

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initTbsSettings(map);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

//        QbSdk.setTbsListener(new TbsListener() {
//
//            @Override
//            public void onDownloadFinish(int i) {
//            //tbs 内核下载完成回调
//                Log.d("X5", " 内核下载完成");
//            }
//
//            @Override
//            public void onInstallFinish(int i) {
//                //内核安装完成回调，
//                Log.d("X5", " 内核安装完成");
//            }
//            @Override
//            public void onDownloadProgress(int i) {
//                //下载进度监听 百分比 ： i%
//                Log.d("X5", " 内核下载进度:" + i);
//            }
//        });
//        //判断是否要自行下载内核
//        boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
//        Log.d("X5", needDownload + "helloggg");
//        // 根据实际的网络情况下，选择是否下载或是其他操作
//        // 例如: 只有在wifi状态下，自动下载，否则弹框提示
//        if (needDownload) {
//            // 启动下载
//            Log.d("X5", "你不自动下载 我自己下载就好了狗日的");
//            TbsDownloader.startDownload(this);
//        }
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }



}
