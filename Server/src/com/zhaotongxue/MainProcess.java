package com.zhaotongxue;

import com.zhaotongxue.SubProcess.Handler;
import com.zhaotongxue.SubProcess.ListMaster;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * @author zhao
 * 服务器主进程
 * @date 2019年6月1日
 * @version 1.0
 */

public class MainProcess {
    private static final int MAXUSERNUMBER=60;
    public static void main(String[] args) throws IOException {
        System.out.println(String.format("ip addr is:%s",Inet4Address.getLocalHost()));
        //监听1919端口
        ServerSocket serverSocket=new ServerSocket(1919);
        while(true){
            Socket socket=serverSocket.accept();
            if(ListMaster.getListMaster().getUserNumbers()>MAXUSERNUMBER) {
                System.getLogger("ConnectionInfo").log(System.Logger.Level.INFO,"当前用户以达到最大限制,限制连接");
                continue;
            }
            //新用户，包含Socket以及用户相关信息
            User user=new User(socket);
            //list管理器添加用户
            ListMaster.getListMaster().addUser(user);
            //咱们这里也看看
            System.out.println("用户连接，ip:"+socket.getInetAddress());
            //子进程创建，用来处理
            Thread handlerProcess=new Thread(new HandlerProcess(user));
            //通过ip设置线程名，方面调试
            handlerProcess.setName(user.getAddr().toString());
            handlerProcess.start();
        }
    }
}
class HandlerProcess implements Runnable{
    User user;
    public HandlerProcess(User user) {
        this.user=user;
    }

    /**
     * @exception IOException
     * 用户异常退出
     */
    @Override
    public void run() {
        try {
            //该进程内只要有IO异常抛出就认为用户因为各种原因中断了Socket，直接退出
            //线程内都是抛出IOException
            new Handler(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.getLogger("Thread Error").log(System.Logger.Level.INFO,e.getMessage());
            System.out.println(user.getName()+"用户退出");
            try {
                ListMaster.getListMaster().removeUser(user);
                user.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库操作失败");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
