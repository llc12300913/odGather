package com.odgather.utils;


import com.alibaba.fastjson.JSONObject;
import com.odgather.menu.Button;
import com.odgather.menu.ClickButton;
import com.odgather.menu.Menu;
import com.odgather.menu.ViewButton;
import com.odgather.po.AccessToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/17.
 */
public class WeixinUtil {
    private static final String APPID = "wxe9a70e0d79ac1d0d";

    private static final String APPSECRET = "191480c0d6371687d2d659f52c8684c8";

    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    private static final String JS_AUTHOR_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&" +
            "redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

    private static final String JS_AUTHOR_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&" +
            "secret=SECRET&code=CODE&grant_type=authorization_code";

    private static final String JS_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";


    private static Map<String, Object> GLOBAL_WEIXIN_CACHE = new HashMap<String, Object>();

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONObject doGetStr(String url) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        JSONObject jsonObject = null;

        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }

        return jsonObject;
    }

    /**
     * post请求
     *
     * @param url
     * @param outStr
     * @return
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, String outStr) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        JSONObject jsonObject = null;

        httpPost.setEntity(new StringEntity(outStr, "UTF-8"));
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
        HttpResponse response = httpClient.execute(httpPost);

        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        jsonObject = JSONObject.parseObject(result);

        return jsonObject;
    }

    /**
     * 获取access_token
     *
     * @return
     * @throws IOException
     */
    @SuppressWarnings(value = "all")
    public static AccessToken getAccessToken() throws IOException {
        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);

        JSONObject jsonObject = null;

        if (GLOBAL_WEIXIN_CACHE.get("ACCESS_TOKEN") == null) {
            jsonObject = doGetStr(url);
            GLOBAL_WEIXIN_CACHE.put("ACCESS_TOKEN", jsonObject.getString("access_token"));
            GLOBAL_WEIXIN_CACHE.put("ACCESS_TOKEN_EXPIRES", jsonObject.getLong("expires_in") + new Date().getTime());

            token.setToken(jsonObject.getString("access_token"));
            token.setExepireIn((jsonObject.getLong("expires_in") + new Date().getTime()));
        } else {
            Long access_token_expires = (Long) GLOBAL_WEIXIN_CACHE.get("ACCESS_TOKEN_EXPIRES");
            if (new Date().getTime() > access_token_expires) {
                jsonObject = doGetStr(url);
                GLOBAL_WEIXIN_CACHE.put("ACCESS_TOKEN", jsonObject.getString("access_token"));
                GLOBAL_WEIXIN_CACHE.put("ACCESS_TOKEN_EXPIRES", jsonObject.getLong("expires_in") + new Date().getTime());

                token.setToken(jsonObject.getString("access_token"));
                token.setExepireIn((jsonObject.getLong("expires_in") + new Date().getTime()));
            } else {
                token.setToken((String) GLOBAL_WEIXIN_CACHE.get("ACCESS_TOKEN"));
                token.setExepireIn((Long) GLOBAL_WEIXIN_CACHE.get("ACCESS_TOKEN_EXPIRES"));
            }
        }

        return token;
    }

    /**
     * 上传文件
     * @param filePath
     * @param accessToken
     * @param type
     * @return
     * @throws IOException
     */

    public static String upload(String filePath, String accessToken,String type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()){
            throw new IOException("文件不存在");
        }

        String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

        URL urlObj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件以流文件的方式推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];

        while((bytes = in.read(bufferOut)) != -1){
            out.write(bufferOut, 0, bytes);
        }

        in.close();

        // 结尾部分，这里结尾表示整体的参数的结尾，结尾要用"--"作为结束，这些都是http协议的规定
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = JSONObject.parseObject(result);
//        System.out.println(jsonObj);
        String typeName = "media_id";

        if(!"image".equals(type)){
            typeName = type + "_media_id";
        }

        String mediaId = jsonObj.getString(typeName);
        return mediaId;
    }

    /**
     * 组装菜单
     * @return
     */
    public static Menu initMenu(){
        Menu menu = new Menu();
        ClickButton button11 = new ClickButton();
        button11.setName("click按钮");
        button11.setType("click");
        button11.setKey("11");

        ViewButton button21 = new ViewButton();
        button21.setName("view按钮");
        button21.setType("view");
        button21.setUrl("http://devllc.s1.natapp.cc/wxindex/author");

        ClickButton button31 = new ClickButton();
        button31.setName("扫码按钮");
        button31.setType("scancode_push");
        button31.setKey("31");

        ClickButton button32 = new ClickButton();
        button32.setName("地理位置按钮");
        button32.setType("location_select");
        button32.setKey("32");

        Button button = new Button();
        button.setName("菜单");
        button.setSub_button(new Button[]{button31, button32});

        menu.setButton(new Button[]{button11, button21, button});
        return menu;
    }

    /**
     * 生成菜单
     * @param token
     * @param menu
     * @return
     * @throws IOException
     */
    public static int createMenu(String token, String menu) throws IOException {
        int result = 0;
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doPostStr(url, menu);
        if (jsonObject != null){
            result = jsonObject.getIntValue("errcode");
        }

        return result;
    }

    /**
     * 生成网页授权url
     * @return
     */
    public static String genJsAuthorUrl(){
        String authorCallBackUrl = "http://devllc.s1.natapp.cc/wxindex/authorcallback";
        String url = JS_AUTHOR_URL.replace("APPID", APPID).replace("REDIRECT_URI", URLEncoder.encode(authorCallBackUrl)).replace("SCOPE", "snsapi_userinfo");
        return url;
    }

    /**
     * 生成网页授权ACCESSTOKEN URL
     * @return
     */
    public static String genJsAuthorAccessTokenUrl(String code){
        String url = JS_AUTHOR_ACCESS_TOKEN_URL.replace("APPID", APPID).replace("SECRET", APPSECRET).replace("CODE", code);
        return url;
    }


    /**
     * 生成获取用户信息URL
     */
    public static String genJSUserInfoUrl(String access_token, String openId){
        String url = JS_USERINFO_URL.replace("ACCESS_TOKEN", access_token).replace("OPENID", openId);
        return url;
    }
}
