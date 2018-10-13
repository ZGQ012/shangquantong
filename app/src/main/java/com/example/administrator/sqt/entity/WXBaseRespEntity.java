package com.example.administrator.sqt.entity;

import java.io.Serializable;

/**
 * Created by zzcompany on 2018/10/11.
 */

public class WXBaseRespEntity implements Serializable {
    private String code;
    private String country;
    private int errCode;
    private String lang;
    private String state;
    private int type;
    private String url;

    public WXBaseRespEntity() {
    }

    public WXBaseRespEntity(String code, String country, int errCode, String lang, String state, int type, String url) {
        this.code = code;
        this.country = country;
        this.errCode = errCode;
        this.lang = lang;
        this.state = state;
        this.type = type;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
