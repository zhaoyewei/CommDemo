package com.zhaotongxue;
import java.io.Serializable;
import java.net.InetAddress;

public class UserInfo implements Serializable {
    private InetAddress ipAddr;
    private String name;

    public UserInfo(InetAddress inetAddress, String name) {
        this.ipAddr = inetAddress;
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
