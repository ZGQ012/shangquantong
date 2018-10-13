package com.example.administrator.sqt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.administrator.sqt.R;
import com.example.administrator.sqt.base.MyApplication;
import com.example.administrator.sqt.entity.QQUser;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;

public class LoginActivity extends MyActivity {

    private static final String TAG = "LoginActivity";
    private ImageView iv_head;

    private UserInfo userInfo;

    private String QQ_uid;//qq_openid

    private BaseUiListener listener = new BaseUiListener();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                JSONObject response = JSONObject.parseObject(String.valueOf(message.obj));
                Log.i(TAG, "UserInfo:" + JSON.toJSONString(response));
                QQUser user = JSONObject.parseObject(response.toJSONString(), QQUser.class);
                if (user != null) {
                    Log.i(TAG, "userInfo:昵称：" + user.getNickname() + "  性别:" + user.getGender() + "  地址：" + user.getProvince() + user.getCity());
                    Log.i(TAG, "头像路径：" + user.getFigureurl_qq_2());
                    Glide.with(LoginActivity.this).load(user.getFigureurl_qq_2()).into(iv_head);
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iv_head = findViewById(R.id.iv_head);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(MyApplication.getShared().getString("headUrl", "")).into(iv_head);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String headUrl = data.getStringExtra("headUrl");
            Log.i(TAG, "url:" + headUrl);
            Glide.with(this).load(headUrl).into(iv_head);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loginClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wx:
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
//                req.scope = "snsapi_login";//提示 scope参数错误，或者没有scope权限
                req.state = "wechat_sdk_微信登录";
                api.sendReq(req);
                break;
            case R.id.btn_qq:
                if (!mTencent.isSessionValid()) {
                    //注销登录 mTencent.logout(this);
                    mTencent.login(this, "all", listener);
                }
                break;
            case R.id.btn_sina:
                break;
            default:
                break;
        }
    }

    /**
     * 获取登录QQ腾讯平台的权限信息(用于访问QQ用户信息)
     *
     * @param jsonObject
     */
    public void initOpenidAndToken(org.json.JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                QQ_uid = openId;
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    Log.i(TAG, "onError: ");
                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    Log.i(TAG, "onComplete: " + response.toString());
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "登录取消: ");
                }
            };
            userInfo = new UserInfo(this, mTencent.getQQToken());
            userInfo.getUserInfo(listener);
        }
    }

    class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Log.i(TAG, "授权:" + o.toString());
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(o.toString());
                initOpenidAndToken(jsonObject);
                updateUserInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Log.i(TAG, "onError:code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel: ");
        }
    }
}
