package com.dt.chat.model;

import java.io.Serializable;

/**
 * description: socket通信的消息模型，封装了通信命令和通信内容，便于收发双方建立可共同识别的报文机制<br>
 * 特别注意，因为消息对象需要通过输入输出流系列化与反序列化，所以该类需要实现序列化接口（Serializable）
 * @author 向海彪 
 * @date 2018/12/28
 */
public class Message implements Serializable {
	/**
	 * 群发
	 */
    public static final int SEND_ALL = 0;
    /**
     * 私发
     */
    public static final int SEND_ONE = 1;
    /**
     * 上线
     */
    public static final int ADD = 2;
    /**
     * 获取在线用户列表
     */
    public static final int USER_LIST=3;
    /**
     * 下线
     */
    public static final int DELETE = 4;
    /**
     * 关闭或断开
     */
    public static final int CLOSE = 5;
    private int command;//命令
    private Object content;//内容
    private String from;//消息发送端
    private String to;//消息接收端

    /**
     * 默认是群发消息
     * @param content
     */
    public Message(Object content) {
        this.command= SEND_ALL;
        this.content=content;
    }

    /**
     * @param command 命令
     * @param content 内容
     */
    public Message(int command, Object content) {
        this.command=command;
        this.content=content;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
