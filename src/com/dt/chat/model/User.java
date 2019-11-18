package com.dt.chat.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * description: socket通信的用户模型，用于标识不同聊天者的姓名、状态等<br>
 * 也可用于注册、登录等扩展功能的实现。跟消息模型一样，它也需要实现序列化接口。
 * @author 唐世节
 * @date 2018/12/28
 */
public class User implements Serializable{

    private String userId;//用户ID，身份的唯一标识
    private String userName;//用户姓名
    private String password;//用户密码
    private String ip;//ip地址
    private int state;//用户状态。比如0-代表禁言，1-代表启用

    public User(){
        UUID uuid = UUID.randomUUID();//因为没有跟数据库连接，所以暂时采用随机函数产生一个随机字串
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
    public boolean equals(Object o) {//覆盖超累的方法，用户在集合中可以区分相同姓名的不同User对象。这很重要！
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
    public String toString() {//覆盖超类的方法，用于在JList等控件中可以正确显示用户名称
        return userName;
    }
}