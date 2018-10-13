package com.example.administrator.sqt.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.sqt.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class SharedActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
    }

    public void sharedClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shared:
                sendToWeiXin("微信分享",
                        "www.baidu.com",
                        "分享",
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                        1);
                break;
            case R.id.btn_format:
                break;
            default:
                break;
        }
    }

    /**
     *  * amr转换MP3
     *  * @param url   ffmpeg目录不包含ffmpeg文件
     *  * @param audiopath  amr文件路径
     *  * @param target     MP3文件路径
     *  
     */
    public static void changeToMp3(String url, String audiopath, String target) {
        try { //windows下面的是ffmpeg.exe   linux如下
            Process process = Runtime.getRuntime().exec(url + File.separator + "ffmpeg -i " + audiopath + " " + target);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null)
                System.out.println(line);
            int exitVal = process.waitFor();
            System.out.println("Process exitValue: " + exitVal);
        } catch (Exception e) {
            System.err.println("IOException " + e.getMessage());
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
}
