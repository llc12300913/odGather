package com.odgather.po;

/**
 * Created by Administrator on 2018/5/17.
 */
public class AccessToken {
    private String token;
    private Long exepireIn;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExepireIn() {
        return exepireIn;
    }

    public void setExepireIn(Long exepireIn) {
        this.exepireIn = exepireIn;
    }
}
