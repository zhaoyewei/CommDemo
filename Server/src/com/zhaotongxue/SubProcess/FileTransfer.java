package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;

public class FileTransfer {
    public FileTransfer(User user,User recv,String fileName) throws IOException {
        //接收方接受地址
        recv.send("FileRecv");
        recv.send(fileName);
        //接收方建立好accept之后
        if(recv.recv().equals("READY")){
            //通知发送方可以连接
            user.send(recv.getAddr().toString());
            if(user.recv().equals("READY")){
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
