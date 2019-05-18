package com.zhaotongxue;

import java.io.IOException;

/**
 * @author zhao
 * @version 1.0
 * @Date 2019年5月11日
 * Login
 */
public class Login {

    private User user;
    private String strCmd;
    private String lastLoginIp = null;
    private String lastLoginTime = null;

    public Login(User user, String strCmd) {
        this.user = user;
        this.strCmd = strCmd;
    }

    /**
     * @return the lastLoginIp
     */
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    /**
     * @param lastLoginIp the lastLoginIp to set
     */
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    /**
     * @return the lastLoginTime
     */
    public String getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @param lastLoginTime the lastLoginTime to set
     */
    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean login() throws IOException {
        String[] cmds = strCmd.split(" ");
        if (cmds.length == 3) {
            // 用户名长度在6-36之间
            if (cmds[1].length() > 6 && cmds[1].length() < 36) {
                // 密码在6-16之间
                if (cmds[2].length() > 6 && cmds[1].length() <= 16) {
                    this.user.send(String.format("%s %s %s", CommandsConverter.getConverter().getStrCmd(Commands.LOGIN),
                            cmds[1], cmds[2]));
                    synchronized (user){
                        String returnMsg = user.recvMsg();
                    if (!returnMsg.equals("0")) {
                        this.setLastLoginIp(returnMsg.split("//")[0]);
                        this.setLastLoginTime(returnMsg.split("//")[1]);
                        return true;
                    } else {
                        return false;
                    }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the strCmd
     */
    public String getStrCmd() {
        return strCmd;
    }

    /**
     * @param strCmd the strCmd to set
     */
    public void setStrCmd(String strCmd) {
        this.strCmd = strCmd;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

}