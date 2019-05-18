package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

/**
 * @author zhao
 * @version 1.0
 * @Date 2019年5月11日
 * Handler
 */

enum CommStatus {
    NO_COMM, PAIR_COMM, GROUP_COMM
}

public class Handler {
    private User user = null;
    // 测试，假设直接登录
    private boolean session = false;

    // 创建后不停处理操作,IOException表示用户退出
    public Handler(User user) throws IOException, SQLException, InterruptedException {
        this.user = user;
        handle();
    }

    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }

    private void handle() throws IOException, SQLException, InterruptedException {
        CommStatus commStatus = CommStatus.NO_COMM;

        // PairMaster负责当前用户与其他用户的端到端通信
        PairMaster pairMaster = new PairMaster(user);
        user.setPairMaster(pairMaster);
        while (true) {

            // 获得用户输入命令
            String strLine = user.recv();

            // 输入命令阶段
            if (commStatus == CommStatus.NO_COMM) {
                if (strLine.equals("") || strLine == null) {
                    continue;
                }
                String[] commandLine = strLine.split(" ");
                String cmd = commandLine[0];

                // 注册
                if (isSession() == false && cmd.equals("Reg")) {
                    if (commandLine.length == 3) {
                        int res = LoginIdentify.getLoginIdentify().Register(commandLine[
                                1], commandLine[2]);
                        if (res == 1) {
                            user.send("1");
                        } else if (res == -1) {
                            user.send("-1");
                        } else {
                            user.send("0");
                        }
                    }
                }

                // 登陆命令
                else if ((isSession() == false) && cmd.equals("Login")) {

                    // Login userName password
                    if (commandLine.length == 3) {
                        String res = LoginIdentify.getLoginIdentify().IdentifyUserNameAndPassword(commandLine[1],
                                commandLine[2], user);
                        if (res != null) {
                            setSession(true);
                            user.send(res);
                            //登录成功之后，服务器连接客户端，专门传送别的客户端发过来的消息
                                Socket s = new Socket(user.getAddr(), 2019);
                                user.setMsgSocket(s);
                        } else {
                        setSession(false);
                        user.send("0");
                    }
                } else {
                    setSession(false);
                    user.send("0");
                }
            }

            // 传输文件命令
            else if (isSession() && cmd.equals("FileTransfer")) {
                // FileTransfer userRecv
                if (commandLine.length >= 3) {
                    //find user
                    User recvUser = ListMaster.getListMaster().findUser(commandLine[1]);
                    //found
                    if (recvUser != null) {
                        //Get File Name
                        String fileName="";
                        for(int i=2;i<commandLine.length;i++){
                            fileName+=commandLine[i];
                        }
                        //Create Thread
                        Thread fileTransferThread = new Thread(
                                new FileTransferProcess(user, recvUser, fileName));
                        fileTransferThread.start();
                    } else {
                        user.send("-1");
                    }
                }
            }

            // 端到端通信命令
            else if (isSession() && cmd.equals("PairComm")) {
                if (commandLine.length == 2) {
                    User recvUser = ListMaster.getListMaster().findUser(commandLine[1]);
                    if (recvUser != null && !recvUser.equals(user)) {
                        commStatus = CommStatus.PAIR_COMM;
                        // PairMaster
                        pairMaster.setRecvUser(recvUser);
                        user.send("1");
                    } else {
                        user.send("0");
                    }
                }
            }

            // 群组通信命令
            else if (session && cmd.equals("GroupComm")) {
                commStatus = CommStatus.GROUP_COMM;
                GroupMaster.getGroupMaster().addUser(user);
                user.send("welcome to group comm");
            }

            // 历史记录查询命令
            else if (session && cmd.equals("History")) {
                if (commandLine.length == 2) {
                    new HistoryQuery(user, commandLine[1]).QueryHistory();
                }
            }

            // 获得列表命令
            else if (session && cmd.equals("GetList")) {
                // 发送列表给这个用户
                user.getList();
            }

            // 用户退出命令
            else if (session && cmd.equals("//EXIT")) {
                ListMaster.getListMaster().removeUser(user);
                System.out.println("User:" + user.getName() + " Exited");
                return;
            }
        } else if (commStatus == CommStatus.PAIR_COMM) {
            if (!pairMaster.TalkTo(strLine)) {
                user.send("//EXITPAIR");
                commStatus = CommStatus.NO_COMM;
            }
        } else if (commStatus == CommStatus.GROUP_COMM) {
            if (!GroupMaster.getGroupMaster().sendMsg(user, strLine)) {
                user.send("//EXITGROUP");
                commStatus = CommStatus.NO_COMM;
            }
        }
    }
}
}

// 创建子线程用于文件传输
class FileTransferProcess implements Runnable {
    private User user = null, recvUser = null;
    private String fileName = null;

    public FileTransferProcess(User user, User recvUser, String fileName) {
        this.user = user;
        this.recvUser = recvUser;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            new FileTransfer(user, recvUser, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
