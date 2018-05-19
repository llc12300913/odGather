package com.odgather.web;

import com.alibaba.fastjson.JSONObject;
import com.odgather.utils.CheckUtil;
import com.odgather.utils.MessageUtil;
import com.odgather.utils.WeixinUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "/wxindex")
public class IndexController {

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    private void  index(HttpServletRequest request, HttpServletResponse response){
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");


        if (CheckUtil.checkSignature(signature, timestamp, nonce)){
            try {
                response.getWriter().write(echostr);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    response.getWriter().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    private void  getMessage(HttpServletRequest request, HttpServletResponse response){
        try {
            response.setCharacterEncoding("UTF-8");

            Map<String, String> map = MessageUtil.xmlToMap(request);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");

            String message = null;
            if (MessageUtil.MESSAGE_TEXT.equals(msgType)){
                if ("1".equals(content)){
                    message = MessageUtil.initText(toUserName, fromUserName, "文字回复内容");
                }else if ("2".equals(content)){
                    message = MessageUtil.initNewsMessage(toUserName, fromUserName);
                }else if ("3".equals(content)){
                    message = MessageUtil.initImageMessage(toUserName, fromUserName);
                }else if ("4".equals(content)){
                    message = MessageUtil.initMusicMessage(toUserName, fromUserName);
                }
            }else if (MessageUtil.MESSAGE_EVNET.equals(msgType)){
                String  eventType = map.get("Event");
                if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){

                }else if (MessageUtil.MESSAGE_CLICK.equals(eventType)){
                    message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                }else if (MessageUtil.MESSAGE_VIEW.equals(eventType)){
//                    String url = map.get("EventKey");
//                    message = MessageUtil.initText(toUserName, fromUserName, url);
                }else if (MessageUtil.MESSAGE_SCANCODE.equals(eventType)){
//                    String key = map.get("EventKey");
//                    message = MessageUtil.initText(toUserName, fromUserName, key);
                }
            }else if (MessageUtil.MESSAGE_LOCATION.equals(msgType)){
//                String label = map.get("Label");
//                message = MessageUtil.initText(toUserName, fromUserName, label);
            }
            if (message != null){
                System.out.println(message);
                response.getWriter().write(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网页授权
     * @param response
     */
    @RequestMapping(value = "/author")
    public String jsAuthor(HttpServletResponse response){
        String authorUrl = WeixinUtil.genJsAuthorUrl();
//        try {
//            response.sendRedirect(authorUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "redirect:"+authorUrl;
    }

    /**
     * 网页授权回调地址
     * @throws IOException
     */
    @RequestMapping(value = "/authorcallback")
    public String authorCallBack(HttpServletRequest request){
        String code = request.getParameter("code");
        String url = WeixinUtil.genJsAuthorAccessTokenUrl(code);

        try {
            JSONObject jsonObject = WeixinUtil.doGetStr(url);
            String openid = jsonObject.getString("openid");
            String access_token = jsonObject.getString("access_token");

            url = WeixinUtil.genJSUserInfoUrl(access_token, openid);
            jsonObject = WeixinUtil.doGetStr(url);
            System.out.println(jsonObject);

            return "author";

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping(value = "/test", produces="text/html;charset=UTF-8")
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param1 = (String) request.getParameter("a");
        String param2 = (String) request.getParameter("b");

        response.getWriter().write(param1 + param2);

//        return "您发送的消息：短发发送到";
    }

}
