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

/**
 * @author zhao
 * 用来处理与Client连接的子线程
 * @version 1.0
 */
public class Handler {

    //user 对象
    private User user = null;

    //登陆状态
    private boolean session = false;

    /**
     * @param user 每个子线程对应一个user
     *             创建后不停处理操作,IOException表示用户退出
     */
    public Handler(User user) throws IOException, SQLException, InterruptedException {
        this.user = user;
        handle();
    }

    /**
     * @return 登陆状态
     */
    public boolean isSession() {
        return session;
    }

    /**
     * @param session 设置登陆状态
     */
    public void setSession(boolean session) {
        this.session = session;
    }

    /**
     * @throws IOException  连接错误，抛出异常提醒主线程退出当前用户连接
     * @throws SQLException 服务器连接异常，退出吧
     */
    private void handle() throws IOException, SQLException {
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
                    //输入格式： Login userName password
                    //如果早就已经登陆了，就告知想要登陆的用户，之后退出
                    if (commandLine.length == 3 && (ListMaster.getListMaster().isAlreadyLogined(commandLine[1])==false)) {

                        //没有登录就进行验证
                        String res = LoginIdentify.getLoginIdentify().IdentifyUserNameAndPassword(commandLine[1],
                                commandLine[2], user);

                        //查询数据库找到用户信息
                        if (res != null) {
                            setSession(true);
                            user.send(res);
                            user.setName(commandLine[1]);
                            user.setSession(true);

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

                    // FileTransfer userRecv fileName
                    if (commandLine.length >= 3) {

                        //寻找用户
                        User recvUser = ListMaster.getListMaster().findUser(commandLine[1]);

                        //找到了
                        if (recvUser != null) {

                            //获得文件名,由于文件名可能含有空格，所以把后面几个字符串全部转化为文件名
                            String fileName = "";
                            for (int i = 2; i < commandLine.length; i++) {
                                fileName += commandLine[i];
                            }

                            //创建线程用于传输文件
                            Thread fileTransferThread = new Thread(
                                    new FileTransferProcess(user, recvUser, fileName));
                            fileTransferThread.start();

                        } else {
                            user.send("-1");
                        }
                    }else{
                        user.send("-1");
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
