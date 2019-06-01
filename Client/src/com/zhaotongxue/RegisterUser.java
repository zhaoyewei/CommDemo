package com.zhaotongxue;

import java.io.IOException;

/**
 * @author zhao
 * @version 1.0
 * 2019年5月11日
 * RegisterUser
 */
public class RegisterUser {
    User user = null;
    String cmd;

    public RegisterUser(User user, String cmd) {
        this.user = user;
        this.cmd = cmd;
    }

    public int reg() throws IOException {
        String[] cmds=this.cmd.split(" ");
        if (cmds.length != 3) {
            return -2;
        } 
        else{
            if (cmds[1].length() > 6 && cmds[1].length() <= 36) {
                if (cmds[2].length() > 8 && cmds[2].length() <= 16) {
                    this.user.send(String.format("%s %s %s",
                            CommandsConverter.getConverter().getStrCmd(Commands.REGISTER), cmds[1], cmds[2]));
                    String recvMsg=user.recvMsg();
                    if (recvMsg.equals("1")) {
                        return 1;
                    } 
                }
            }
            return 0;
                    /*
                     else if(recvMsg.equals("-1")){
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    return -2;
                }
            } else {
                return -2;
            }
            */
        }
        
    }

}