package com.odgather.po;

/**
 * Created by Administrator on 2018/5/16.
 */
public class TextMessage  extends  BaseMessage{

    private String Content;
    private Long MsgId;



    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public Long getMsgId() {
        return MsgId;
    }

    public void setMsgId(Long msgId) {
        this.MsgId = msgId;
    }
}
