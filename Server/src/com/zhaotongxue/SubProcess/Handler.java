package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author zhao
 * @version 1.0
 */
enum CommStatus {
    NO_COMM, PAIR_COMM, GROUP_COMM;
}

public class Handler {
    private User user = null;

    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }

    //测试，假设直接登录
    private boolean session = true;

    // 创建后不停处理操作,IOException表示用户退出
    public Handler(User user) throws IOException, SQLException {
        this.user = user;
        handle();
    }

    private void handle() throws IOException, SQLException {
        CommStatus commStatus = CommStatus.NO_COMM;
        //PairMaster负责当前用户与其他用户的端到端通信
        PairMaster pairMaster = new PairMaster(user);
        while (true) {
            // 获得用户输入命令
            String strLine = user.recv();
            // 输入命令阶段
            if (commStatus == CommStatus.NO_COMM) {
               if(strLine.equals("")||strLine==null)  continue;
                String[] commandLine = strLine.split(" ");
                String cmd = commandLine[0];
                if(isSession()==false&&cmd.equals("Reg")){
                    if(commandLine.length==3)  {
                        if(LoginIdentify.getLoginIdentify().Register(commandLine[1],commandLine[2])) {
                            user.send("Regsiter succeed");
                        }
                        else{
                            user.send("Register failed");
                        }
                    }
                } // 登陆命令
                else if ((isSession() == false) && cmd.equals("Login")) {
                    // Login userName password
                    if (commandLine.length == 3) {
                        String res=LoginIdentify.getLoginIdentify().IdentifyUserNameAndPassword(commandLine[1],
                                commandLine[2],user);
                        if (res!=null) {
                            setSession(true);
                            user.send(String.format("Login Succeed,you last login time is %s,lost login ip is %s.",
                                    res.split("//")[0],res.split("//")[1]));
                        }else {
                            setSession(false);
                            user.send("Login Failed");
                        }
                    }
                }
                // 传输文件命令
                else if (isSession() && cmd.equals("FileTransfer")) {
                    // FileTransfer userRecv
                    if (commandLine.length == 2) {
                        User recvUser = ListMaster.getListMaster().findUser(commandLine[1]);
                        if (recvUser != null) {
                            Thread fileTransferThread = new Thread(new FileTransferProcess(user, recvUser));
                            fileTransferThread.start();
                        } else {
                            user.send("Can't find user: " + commandLine[1]);
                        }
                    }
                }
                // 端到端通信命令
                else if (isSession() && cmd.equals("PairComm")) {
                    // PairComm userNme
                    if (commandLine.length == 2) {
                        User recvUser = ListMaster.getListMaster().findUser(commandLine[1]);
                        if (recvUser != null&&!recvUser.equals(user)) {
                            commStatus = CommStatus.PAIR_COMM;
                            // PairMaster
                            pairMaster.setRecvUser(recvUser);
                        } else {
                            user.send("Can't find user: " + commandLine[1]);
                        }
                    }
                }
                // 群组通信命令
                else if (session && cmd.equals("GroupComm")) {
                    // GroupComm
                    // new GroupMaster(user);
                    commStatus = CommStatus.GROUP_COMM;
                    GroupMaster.getGroupMaster().addUser(user);
                    user.send("welcome to group comm");
                } // 历史记录查询命令
                else if (session && cmd.equals("History")) {
                    if (commandLine.length == 2) {
                        new HistoryQuery(user, commandLine[1]).QueryHistory();
                    }
                } // 获得列表命令
                else if (session && cmd.equals("GetList")) {
                    // 发送列表给这个用户
                    user.getList();
                } // 用户退出命令
                else if (session && cmd.equals("//EXIT")) {
                    // 退出
                    ListMaster.getListMaster().removeUser(user);
                    System.out.println("User:" + user.getName() + " Exited");
                    return;
                }
            }
            else if (commStatus == CommStatus.PAIR_COMM) {
                /*
                 * if (strLine == "//EXIT") { pairMaster.setRecvUser(null); commStatus =
                 * CommStatus.NO_COMM; } else { pairMaster.TalkTo(strLine, new Date()); }
                 */
                if (!pairMaster.TalkTo(strLine)) {
                    User recvUser=pairMaster.getPair();
                    if(recvUser!=null) user.send("you exited comm with "+recvUser.getName());

                    else user.send("you exited pair comm");
//                    pairMaster.setRecvUser(null);
                    commStatus = CommStatus.NO_COMM;
                }
            } else if (commStatus == CommStatus.GROUP_COMM) {
                if (!GroupMaster.getGroupMaster().sendMsg(user, strLine)) {
                    commStatus = CommStatus.NO_COMM;
                    user.send("you exited group comm");
                }
            }
        }
    }
}

// 创建子线程用于文件传输
class FileTransferProcess implements Runnable {
    private User user = null, recvUser = null;

    public FileTransferProcess(User user, User recvUser) {
        this.user = user;
        this.recvUser = recvUser;
    }

    @Override
    public void run() {
        try {
            new FileTransfer(user, recvUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
