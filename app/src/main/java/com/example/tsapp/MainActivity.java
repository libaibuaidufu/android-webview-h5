package com.example.tsapp;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private WebView mWebView;
    private String routerPath;
    private boolean isShow;
    private WebChromeClient webChromeClient;//web浏览器服务，用于回调当前加载进度以及网页中的title
    private String TAG = "musci";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 进行全屏
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) this.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        String appCachePath = getApplication().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setDatabaseEnabled(true);
//        mWebView.addJavascriptInterface(this, "androidinfo");//添加js监听 这样html就能调用客户端
        //        mWebView.loadUrl("file:android_asset/index.html");

//        String url = "http://music.baidu.com";
        String url = "https://x6g.com";
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new MyWebViewClient());
        webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //这里判断，当前是否获取到了网页的title
                if (title.length() > 12) {
                    title = title.substring(0, 12);
                    //如果当前title的长度没有超过12，则表示没有获得到title，用...来表示
                    Log.i("webview", title + "...");
                } else {
                    Log.i("webview", title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress != 10) {
                    //加载成功
                }
            }

        };
        mWebView.setWebChromeClient(webChromeClient);
    }


    /**
     * webview客户端
     * 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                //在这里你可以拦截url，然后自己处理一些事情，比如跳转app内部网页
                view.loadUrl(url);
                return true;
            } catch (Exception e) {
                Log.i("webview", "该链接无效");
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: 我要返回了");
        //点击返回键返回url的上一个页面，而不是返回app中的上个页面
        if (mWebView.canGoBack()) {
            // goBack()表示返回WebView的上一页面
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 当前页面暂停后
     */
    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: 我被暂停了");
        //如果当前web服务不是null
        if (webChromeClient != null)
            //通知app当前页面要隐藏它的自定义视图。
            webChromeClient.onHideCustomView();
        //让webview重新加载，用于停掉音视频的声音
        mWebView.reload();
        //先重载webview再暂停webview，这时候才真正能够停掉音视频的声音，api 2.3.3 以上才能暂停
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mWebView.onPause(); // 暂停网页中正在播放的视频
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        //重新开始webview，这样做的目的是为了不让webview重复进入的时候出现无法加载url出现空白
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: 我被摧毁了");
        super.onDestroy();
        //webview停止加载
        mWebView.stopLoading();
        //webview销毁
        mWebView.destroy();
        //webview清理内存
        mWebView.clearCache(true);
        //webview清理历史记录
        mWebView.clearHistory();
    }



//    @Override
//    protected void onDestroy() {
//        android.util.Log.i("tag", "来自JS的传参 :我死了");
//        mWebView.loadUrl("javascript:saveLocalPlayTime()");
//        super.onDestroy();
//    }

//    @Override
//    protected void onPause ()  {
//        Log.i("tag", "来自JS的传参 :我被暂停了");
//        mWebView.reload ();
//        super.onPause ();
//    }

//    @Override
//    protected void onStop() {
//        Log.i("tag", "来自JS的传参 :我被停止了");
//        super.onStop();
//    }

//    @JavascriptInterface
//    public static String showInfoFromJs(String url) {
//        android.util.Log.i("tag", "来自JS的传参 :" + url);
//        String result = "";
//        BufferedReader in = null;// 读取响应输入流
//        try {
//            CookieManager cookieManager = new CookieManager();
//            CookieHandler.setDefault(cookieManager);
//            // 创建URL对象
//            java.net.URL connURL = new java.net.URL(url);
//            // 打开URL连接
//            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
//                    .openConnection();
//            // 设置通用属性
//            httpConn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
//            httpConn.setRequestProperty("Connection", "Keep-Alive");
////            httpConn.setRequestProperty("cookie", "__51uvsct__JKeSBtlVceVtWkQX=1; __51vcke__JKeSBtlVceVtWkQX=125d11e5-fd92-5ab5-b524-81f181345ac0; __51vuft__JKeSBtlVceVtWkQX=1643545052464; Hm_lvt_03d3be6be07713090d718aa439330170=1643545157; _ga=GA1.2.893703207.1643545158; _gid=GA1.2.1437478780.1643545158; uid=35689; token=f355ce5b6b5b7e2a3999f71f736949a1; gonggaotime=1643545145; _gat_gtag_UA_198991932_1=1; __vtins__JKeSBtlVceVtWkQX=%7B%22sid%22%3A%20%22506ca2f3-c118-5ea9-aa97-5447098b953a%22%2C%20%22vd%22%3A%2010%2C%20%22stt%22%3A%20175488%2C%20%22dr%22%3A%206591%2C%20%22expires%22%3A%201643547027950%2C%20%22ct%22%3A%201643545227950%7D; Hm_lpvt_03d3be6be07713090d718aa439330170=1643545230");
//            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");
//            Map<String, List<String>> headerss = httpConn.getRequestProperties();
//            // 遍历所有的响应头字段
//            for (String key : headerss.keySet()) {
//                android.util.Log.i("tag", key + headerss.get(key));
//            }
//            // 建立实际的连接
//            httpConn.connect();
//            // 响应头部获取
//            Map<String, List<String>> headers = httpConn.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : headers.keySet()) {
//                android.util.Log.i("tag", key + headers.get(key));
//            }
//            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
//            in = new BufferedReader(new InputStreamReader(httpConn
//                    .getInputStream(), StandardCharsets.UTF_8));
//            String line;
//            // 读取返回的内容
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//        } catch (Exception e) {
//            android.util.Log.i("tag", "来自JS的传参 :出错了");
//            e.printStackTrace();
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        android.util.Log.i("tag", "来自JS的传参 :" + result);
//        return result;
//    }
//
//    @Override
//    public void onBackPressed() {
//        Log.i("tag", "来自JS的传参 :fuck");
//        String[] strArr = new String[]{"BookSearch", "AddOne"};
//        List<String> list = Arrays.asList(strArr);
//        boolean result = list.contains(routerPath);
//        System.out.println(result); // true
//        if (isShow) {
//            mWebView.loadUrl("javascript:callJsFunction()");
//        } else {
//            if (mWebView.canGoBack()) {
//                if (result) {
//                    moveTaskToBack(true);
//                } else {
//                    mWebView.loadUrl("javascript:callJsFunction()");
////                mWebView.goBack();
//                }
//            } else {
//                moveTaskToBack(true);
////            super.onBackPressed();
//            }
//        }
//
//    }
//
//    @JavascriptInterface
//    public void saveCurrentPath(String path) {
//        routerPath = path;
//    }
//
//    @JavascriptInterface
//    public void saveShow(boolean show) {
//        isShow = show;
//    }
//
//    @JavascriptInterface
//    public static String showLogs(String log) {
//        android.util.Log.i("tag", log);
//        return log;
//    }
}