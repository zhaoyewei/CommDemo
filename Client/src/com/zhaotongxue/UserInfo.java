package com.zhaotongxue;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhao
 * @version 1.0
 * @Date 2019年5月11日
 * UserInfo
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress ipAddr;
    private String name;

    public UserInfo(InetAddress inetAddress, String name) {
        this.ipAddr = inetAddress;
        this.name = name;
    }

    public UserInfo(String s, String name) {
        try {
            this.ipAddr = InetAddress.getByName(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.name = name;
    }

    public InetAddress getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(InetAddress ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
