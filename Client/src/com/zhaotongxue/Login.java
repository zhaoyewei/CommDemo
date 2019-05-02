package com.zhaotongxue;

import java.io.IOException;

/**
 * Login
 */
public class Login {

    private User user;
    private String strCmd;

    public Login(User user, String strCmd) {
        this.user = user;
        this.strCmd = strCmd;
    }

    public boolean login() throws IOException {
        String[] cmds = strCmd.split(" ");
        if (cmds.length != 3) {
            if (cmds[1].length() > 6 && cmds[1].length() < 36) {
                if (cmds[2].length() > 6 && cmds[1].length() <= 16) {
                    this.user.send(String.format("%s %s %s", CommandsConverter.getConverter().getStrCmd(Commands.LOGIN),cmds[1], cmds[2]));
                    String returnMsg = user.recvMsg();
                    if (returnMsg != "Login Failed") {
                        return true;
                    } else {
                        return false;
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