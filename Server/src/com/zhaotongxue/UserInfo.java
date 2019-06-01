package com.zhaotongxue;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * @author zhao
 * 本来打算用UserInfo来传输，但是由于消息格式不一致不方便处理所以放弃
 * @version 1.0
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress ipAddr;
    private String name;

    public UserInfo(InetAddress inetAddress, String name) {
        this.ipAddr = inetAddress;
        this.name = name;
    }

    /**
     *
     * @return ip
     */
    public InetAddress getIpAddr() {
        return ipAddr;
    }

    /**
     *
     * @param ipAddr
     * 用户ip
     */
    public void setIpAddr(InetAddress ipAddr) {
        this.ipAddr = ipAddr;
    }

    /**
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * 设置用户名
     */
    public void setName(String name) {
        this.name = name;
    }
}
