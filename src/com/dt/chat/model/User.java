package com.dt.chat.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * description: socketͨ�ŵ��û�ģ�ͣ����ڱ�ʶ��ͬ�����ߵ�������״̬��<br>
 * Ҳ������ע�ᡢ��¼����չ���ܵ�ʵ�֡�����Ϣģ��һ������Ҳ��Ҫʵ�����л��ӿڡ�
 * @author ������
 * @date 2018/12/28
 */
public class User implements Serializable{

    private String userId;//�û�ID����ݵ�Ψһ��ʶ
    private String userName;//�û�����
    private String password;//�û�����
    private String ip;//ip��ַ
    private int state;//�û�״̬������0-������ԣ�1-��������

    public User(){
        UUID uuid = UUID.randomUUID();//��Ϊû�и����ݿ����ӣ�������ʱ���������������һ������ִ�
        userId = uuid.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {//���ǳ��۵ķ������û��ڼ����п���������ͬ�����Ĳ�ͬUser���������Ҫ��
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName);
    }

    @Override
    public String toString() {//���ǳ���ķ�����������JList�ȿؼ��п�����ȷ��ʾ�û�����
        return userName;
    }
}