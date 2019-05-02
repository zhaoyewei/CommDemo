package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;

public class FileTransfer {
    public FileTransfer(User user,User recv) throws IOException {
        //接收方接受地址
        recv.send(user.getAddr().toString());
        String recvStr=recv.recv();
        //接收方建立好accept之后
        if(recvStr=="READY"){
            //通知发送方可以连接
            user.send("READY");
            if(user.recv()=="READY"){
                System.getLogger("FileTransfer").log(System.Logger.Level.INFO,"two sides of file transfer is " +
                        "connected");
            }
            else {
                user.send("自己这里好像出了点问题");
            }
        }else {
            user.send("对方似乎出了点问题");
        }
        return;
    }
}
