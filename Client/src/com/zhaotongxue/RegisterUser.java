package com.zhaotongxue;

import java.io.IOException;

/**
 * RegisterUser
 */
public class RegisterUser {
    User user = null;
    String cmd;

    public RegisterUser(User user, String cmd) {
        this.user = user;
        this.cmd = cmd;
    }

    public boolean reg() throws IOException {
        String[] cmds=this.cmd.split(" ");
        if (cmds.length != 3) {
            return false;
        } 
        else{
            if (cmds[1].length() > 6 && cmds[1].length() <= 36) {
                if (cmds[2].length() > 8 && cmds[2].length() <= 16) {
                    this.user.send(String.format("%s %s %s",
                            CommandsConverter.getConverter().getStrCmd(Commands.REGISTER), cmds[1], cmds[0]));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        
    }

}