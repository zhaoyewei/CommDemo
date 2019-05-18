package com.zhaotongxue;

import java.io.IOException;

/**
 * PairComm
 */
public class PairComm {
    private User user;
    private String cmd;

    public PairComm(User user, String strCmd) {
        this.user = user;
        this.cmd = strCmd;
    }

    public boolean joinPairComm() throws IOException {
        String[] cmds = cmd.split(" ");
        if (cmds.length != 2) {
            return false;
        }
        this.user.send(String.format("%s %s", CommandsConverter.getConverter().getStrCmd(Commands.PAIRCOMM), cmds[1]));
        String recv = user.recvMsg();
        if (recv.equals("1")) {
            System.out.println("you joined pair comm with "+cmds[1]);
            return true;
        } else {
            System.out.println("failed");
            return false;
        }
    }

}