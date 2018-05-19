import com.alibaba.fastjson.JSONObject;
import com.odgather.po.AccessToken;
import com.odgather.utils.WeixinUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2018/5/17.
 */
public class WeixinTest {
    public static void main(String[] args) {
        AccessToken token = null;
        try {
            token = WeixinUtil.getAccessToken();
            System.out.println("票据:" + token.getToken());
            System.out.println("过期时间" + token.getExepireIn());

//            String path = "D:/test.jpg";
//            String mediaId = WeixinUtil.upload(path, token.getToken(), "image");
//            System.out.println(mediaId);

//            String path = "D:/test2.jpg";
//            String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
//            System.out.println(mediaId);

            String menu = JSONObject.toJSON(WeixinUtil.initMenu()).toString();
            int result = WeixinUtil.createMenu(token.getToken(), menu);
            if (result == 0){
                System.out.println("创建菜单成功");
            }else{
                System.out.println("创建菜单失败，错误码：" + result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
