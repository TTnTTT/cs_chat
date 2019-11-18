package com.dt.chat.model;

import java.io.Serializable;

/**
 * description: socketͨ�ŵ���Ϣģ�ͣ���װ��ͨ�������ͨ�����ݣ������շ�˫�������ɹ�ͬʶ��ı��Ļ���<br>
 * �ر�ע�⣬��Ϊ��Ϣ������Ҫͨ�����������ϵ�л��뷴���л������Ը�����Ҫʵ�����л��ӿڣ�Serializable��
 * @author �򺣱� 
 * @date 2018/12/28
 */
public class Message implements Serializable {
	/**
	 * Ⱥ��
	 */
    public static final int SEND_ALL = 0;
    /**
     * ˽��
     */
    public static final int SEND_ONE = 1;
    /**
     * ����
     */
    public static final int ADD = 2;
    /**
     * ��ȡ�����û��б�
     */
    public static final int USER_LIST=3;
    /**
     * ����
     */
    public static final int DELETE = 4;
    /**
     * �رջ�Ͽ�
     */
    public static final int CLOSE = 5;
    private int command;//����
    private Object content;//����
    private String from;//��Ϣ���Ͷ�
    private String to;//��Ϣ���ն�

    /**
     * Ĭ����Ⱥ����Ϣ
     * @param content
     */
    public Message(Object content) {
        this.command= SEND_ALL;
        this.content=content;
    }

    /**
     * @param command ����
     * @param content ����
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
