package com.example.administrator.sqt.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.administrator.sqt.auth.BaseUIListener;
import com.example.administrator.sqt.base.Config;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

public abstract class MyActivity extends AppCompatActivity {

   //微信
    public IWXAPI api;

    //QQ
    public Tencent mTencent;
    public BaseUIListener listener;

    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, Config.APP_ID_WX, true);
        //将应用的appid注册到微信
        api.registerApp(Config.APP_ID_WX);
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(Config.QQ_LOGIN_APP_ID, this);
        // 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
        // 初始化视图
        listener = new BaseUIListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView() {
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);//支持App内部JavaScript交互
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置允许JS弹窗
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);//支持存储简单数据
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setSupportZoom(true);//支持缩放
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();//支持多窗口
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.requestFocus();
        webView.requestFocusFromTouch();
    }
}
