package com.example.tsapp;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;


import android.view.Window;
import android.webkit.JavascriptInterface;

import com.example.tsapp.utils.X5WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static boolean main_initialized = false;
    private X5WebView mWebView;
    private Context mContext = null;
    private String routerPath;
    private boolean isShow;

    @JavascriptInterface
    public static String showInfoFromJs(String url) {
        android.util.Log.i("tag", "来自JS的传参 :" + url);
        String result = "";
        BufferedReader in = null;// 读取响应输入流
        try {
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");
            // 建立实际的连接
            httpConn.connect();
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), StandardCharsets.UTF_8));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            android.util.Log.i("tag", "来自JS的传参 :出错了");
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        android.util.Log.i("tag", "来自JS的传参 :" + result);
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 进行全屏
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mContext = this;
        if (!main_initialized) {
            this.new_init();
        }
    }

    private void new_init() {
        android.util.Log.i("tag", "来自JS的传参 :我启动了");
//        修改 res/layout 里面的 webview为 x5weiview
        mWebView = (X5WebView) this.findViewById(R.id.webview);
        mWebView.addJavascriptInterface(this, "androidinfo");//添加js监听 这样html就能调用客户端
        mWebView.loadUrl("file:android_asset/index.html");
        main_initialized = true;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        android.util.Log.i("tag", "来自JS的传参 :我到后台了");
        if (!isAppOnForeground()) {
            //app 进入后台
            android.util.Log.i("tag", "来自JS的传参 :我进入后台了");
//            startService(new Intent(this, DeskService.class));
            //全局变量isActive = false 记录当前已经进入后台
        }
    }

    @Override
    protected void onDestroy() {
        android.util.Log.i("tag", "来自JS的传参 :我死了");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        String[] strArr = new String[]{"BookSearch", "AddOne"};
        List<String> list = Arrays.asList(strArr);
        boolean result = list.contains(routerPath);
        System.out.println(result); // true
        if (isShow) {
            mWebView.loadUrl("javascript:callJsFunction()");
        } else {
            if (mWebView.canGoBack()) {
                if (result) {
                    moveTaskToBack(false);
                } else {
                    mWebView.loadUrl("javascript:callJsFunction()");
                }
            } else {
                moveTaskToBack(false);
//                mWebView.goBack();
//            super.onBackPressed();
            }
        }

    }

    @JavascriptInterface
    public void saveCurrentPath(String path) {
        routerPath = path;
    }

    @JavascriptInterface
    public void saveShow(boolean show) {
        isShow = show;
    }
}
