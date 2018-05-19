package com.odgather.web;

import com.odgather.po.TextMessage;
import com.odgather.utils.CheckUtil;
import com.odgather.utils.MessageUtil;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
            }
            System.out.println(message);
            response.getWriter().write(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @RequestMapping(value = "/test", produces="text/html;charset=UTF-8")
    @ResponseBody
    public String test(){
        return "您发送的消息：短发发送到";
    }

}
