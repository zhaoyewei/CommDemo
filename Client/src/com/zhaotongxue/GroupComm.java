package com.zhaotongxue;

import java.io.IOException;

/**
 * @author zhao
 * 处理群组通信
 * @date 2019年6月1日
 * @version 1.0
 */
public class GroupComm {
    private User user;

    public GroupComm(User user) {
        this.user = user;
    }

    public boolean joinGroup(User user) throws IOException {
        this.user.send(CommandsConverter.getConverter().getStrCmd(Commands.GROUPCOMM));
        String recv = user.recvMsg();
        if (!recv.equals("")) {
            System.out.println(recv);
            return true;
        }else{
            System.out.println("Join group Comm failed");
            return false;
        }
    }
    
}
/*
class GroupListener implements Runnable {
    private User user;

    public GroupListener(User user) {
        this.user = user; 
    }
    @Override
    public void run() {
        String strRecvMsg = user.recvMsg();
        if(strRecvMsg.equals(CommandsConverter.getConverter().getStrCmd(Comm))
    }
}
*/