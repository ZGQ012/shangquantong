package com.example.administrator.sqt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.administrator.sqt.activity.MyActivity;
import com.example.administrator.sqt.util.T;
import com.example.administrator.sqt.zxing.android.CaptureActivity;
import com.example.administrator.sqt.zxing.bean.ZxingConfig;
import com.example.administrator.sqt.zxing.common.Constant;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends MyActivity {

    private static final String TAG = "MainActivity";
    private int REQUEST_CODE_SCAN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //网页弹框监听
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {

            /*@Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }*/

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                select(uri);
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                select(request.getUrl());
                return false;
            }
        });
        webView.loadUrl("http://shangquan.zztv021.com/");
        setContentView(webView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i(TAG, "扫描的结果: " + content);
                T.showShort(this, content);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 选择
     *
     * @param uri
     */
    public void select(Uri uri) {
        Log.i(TAG, "加载地址: " + uri + "\n参数名称:" + uri.getQueryParameterNames() + "\n参数内容:" + uri.getQueryParameter("type"));
        if (!TextUtils.isEmpty(uri.getQueryParameterNames().toString())) {
                    /*登录*/
            if (uri.getQueryParameter("type") != null) {
                type(uri);
            }
                    /*分享*/
            else if (uri.getQueryParameter("Number") != null) {
                share(uri);
            }
        }
    }

    /**
     * 类型
     *
     * @param uri
     */
    public void type(Uri uri) {
        switch (uri.getQueryParameter("type")) {
            case "wx":
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                //req.scope = "snsapi_login";//提示 scope参数错误，或者没有scope权限
                req.state = "wechat_sdk_微信登录";
                api.sendReq(req);
                break;
            case "ewm":
                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                                config.setShake(true);//是否震动  默认为true
                                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                                config.setReactColor(R.color.white);//设置扫描框四个角的颜色 默认为淡蓝色
                                config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
                break;
            default:
                break;
        }
    }

    /**
     * 分享
     *
     * @param uri
     */
    public void share(Uri uri) {
        switch (uri.getQueryParameter("Number")) {
            case "0":
                Log.i(TAG, "微信: ");
                sendToWeiXin("分享给微信好友", "www.baidu.com", "微信好友", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 0);
                break;
            case "1":
                Log.i(TAG, "微信朋友圈: ");
                sendToWeiXin("分享到微信朋友圈", "www.baidu.com", "微信朋友圈", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 1);
                break;
            case "2":
                Log.i(TAG, "QQ: ");
                shareToQQ("分享给QQ好友", "QQ好友", "https://www.baidu.com", "https://www.baidu.com/img/bd_logo1.png", 2);
                break;
            case "3":
                Log.i(TAG, "QQ空间: ");
                shareToQQ("分享到QQ空间", "QQ空间", "https://www.baidu.com", "https://www.baidu.com/img/bd_logo1.png", 1);
                break;
            default:
                break;
        }
    }

    /**
     * @param title       分享的标题
     * @param openUrl     点击分享item打开的网页地址url
     * @param description 网页的描述
     * @param icon        分享item的图片
     * @param requestCode 0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendToWeiXin(String title, String openUrl, String description, Bitmap icon, int requestCode) {
        //初始化一个WXWebpageObject对象，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = openUrl;
        //Y用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;//网页标题
        msg.description = description;//网页描述
        msg.setThumbImage(icon);
        //构建一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "supplier";
        req.message = msg;
        req.scene = requestCode;
        api.sendReq(req);
    }

    /**
     * 官方参考文档地址：http://wiki.open.qq.com/index.php?title=Android_API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E&=45038#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89
     *
     * @param title       分享的内容title
     * @param openUrl     点击分享内容打开的地址
     * @param description 分享item的描述信息 分享的消息摘要，最长40个字。
     * @param imgUrl      分享item的图片地址
     * @param shareType   1分享到QQ空间 2分享到QQ好友
     */
    public void shareToQQ(String title, String description, String openUrl, String imgUrl, int shareType) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//消息类型 图文用默认的
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);//描述信息
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, openUrl);//这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);//分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, shareType);//分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        mTencent.shareToQQ(this, params, listener);
    }

}
