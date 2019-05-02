package com.zhaotongxue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * ClientMain
 */
public class ClientMain {
   private String host = "10.122.195.204";
   private int port = 1919;
   private BufferedReader cmdReader = null;
   private User user = null;
   private Socket socket = null;
   private Commands status = Commands.NONE;
   private String strCmd = null;
   private Date date=null;
   private Calendar calendar = Calendar.getInstance();
   private SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.MianProcess();
    }

    private void InitConnection() {
        while (true) {
            try {
                cmdReader = new BufferedReader(new InputStreamReader(System.in, "GBK"));
                socket = new Socket(host, port);
                user = new User(socket);
                // 接收消息吧
                Thread thread = new Thread(new RecvListener());
                thread.start();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Not find host,you can type them yourself:\n1:host");
                try {
                    host = cmdReader.readLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Not find host,you can type them yourself:\n1:host");
                try {
                    port = Integer.parseInt(cmdReader.readLine());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private void inCmdProcess(Commands inCmd) {
        switch (inCmd) {
        // 发送文件，那么直接发过去就行
        case FILETRANSFER:
            try {
                sendFile(user, strCmd);
            } catch (IOException e) {
            }
            status = Commands.NONE;
            break;
        case LOGIN:
            try {
                userLogin(user, strCmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case REGISTER:
            try {
                reg(user, strCmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case GETLIST:
            try {
                getList(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case HISTORY:
            try {
                getHistory(user, strCmd);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case EXIT:
            // new ExitClient(user);
            try {
                exitClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case EXITPAIR:
            // new ExitPair(user);
            try {
                exitPair(user);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case EXITGROUP:
            // new ExitGroup(user);
            try {
                exitGroup(user);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            status = Commands.NONE;
            break;
        case GROUPCOMM:
            try {
                if (pairComm(user, strCmd)) {
                    status = inCmd;
                } else {
                    status = Commands.NONE;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case PAIRCOMM:
            try {
                if (groupComm(user)) {
                    status = inCmd;
                } else {
                    status = Commands.NONE;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        default:
            status = inCmd;
            break;
        }
    }

    private void MianProcess() {
        InitConnection();
        // 处理本地用户输入操作
        status = Commands.NONE;
        while (true) {
            try {
                // 用户输入，并且转换为命令
                strCmd = cmdReader.readLine();
                // 在群组通信和端到端通信的时候，直接把输入传进去，至于如果这时候是退出通信的命令，那么就是在群组通信和端到端通信中再检测一次就行了，如果是的话改变Status，不是的话保持。
                if (status != Commands.GROUPCOMM && status != Commands.PAIRCOMM) {
                    Commands inCmd = CommandsConverter.getConverter().toCmds(strCmd);
                    inCmdProcess(inCmd);
                } else {
                    //通信过程中就直接发送
                    sendMsg(strCmd);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    new ExitClient(user);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Exit Program");
            }
        }
    }

    private void sendMsg(String strCmd) throws IOException {
        date = calendar.getTime();
        user.send(String.format("%s//DATE:", strCmd,slf.format(date)));
    }

    class RecvListener implements Runnable {

        @Override
        public void run() {
            String recvStrMsg = null;
            while (true) {
                try {
                    recvStrMsg = user.recvMsg();
                    if (CommandsConverter.getConverter().toCmds(recvStrMsg) == Commands.FILERECV) {
                        fileRecv(user);
                        continue;
                    }
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                switch (status) {
                // 啥都没有
                case NONE:
                    /*
                     * try { showInfo(user.recvMsg()); } catch (IOException e2) {
                     * e2.printStackTrace(); }
                     */
                    showInfo(recvStrMsg);
                    break;
                case PAIRCOMM:
                    /*
                     * try { if (pairComm(user, strCmd)) { showMsg(user.recvMsg()); } else { status
                     * = Commands.NONE; } } catch (IOException e1) { e1.printStackTrace(); status =
                     * Commands.NONE; }
                     */
                    showMsg(recvStrMsg);
                    break;
                case GROUPCOMM:
                    // join group,then show msg
                    /*
                     * try { if (groupComm(user)) { showMsg(user.recvMsg()); } else { status =
                     * Commands.NONE; } } catch (IOException e1) { e1.printStackTrace(); status =
                     * Commands.NONE; }
                     */
                    showMsg(recvStrMsg);
                    break;
                // case FILERECV:
                // fileRecv(user, 2019);
                // break;
                default:
                    // status = Commands.NONE;
                    break;
                }
            }
            /*
             * while (true) { try { //如果是getList状态或者 if() //接收消息 String recvdMsg =
             * user.recvMsg(); Commands recvCmd =
             * CommandsConverter.getConverter().toCmds(recvdMsg); //首先查看消息是不是接收文件命令 //是的话就接收
             * if (recvCmd == Commands.FILERECV) { fileRecv(user); } //否则的话就看现在的状态
             * //对话状态则显示消息 else if (status == Commands.PAIRCOMM || status ==
             * Commands.GROUPCOMM) { showMsg(recvdMsg); } //NONE状态则显示Info,Login也是, else if
             * (status == Commands.NONE||status==Commands.LOGIN) { showInfo(recvdMsg); }
             * else if (status == Commands.LOGIN) { showInfo() } /* else { //有状态就是在群聊啥的
             * Commands cmd = CommandsConverter.getConverter().toCmds(recvdMsg);
             * //服务器哪里没有发送退出指令 if (cmd == Commands.NONE) { showMsg(recvdMsg); } else {
             * //发出了退出指令 if (status == Commands.GROUPCOMM) { if (cmd == Commands.EXITGROUP)
             * { status = Commands.NONE; continue; } } else if (status == Commands.PAIRCOMM)
             * { if (cmd == Commands.EXITPAIR) { status = Commands.NONE; continue; } } } } }
             * catch (IOException e) { e.printStackTrace(); }
             * 
             * }
             */
        }

    }

    private void exitPair(User user) throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.EXITPAIR));
        String s = user.recvMsg();
        if (!s.equals("")) {
            System.out.println(s);
        } else {
            System.out.println("Exit Pair Comm failed");
        }
    }

    private void exitGroup(User user) throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.EXITGROUP));
        String s = user.recvMsg();
        if (!s.equals("")) {
            System.out.println(s);
        } else {
            System.out.println("Exit Group Comm failed");
        }
    }

    private void exitClient() throws IOException {
        user.disconnect();
        System.exit(1);
    }

    private void showInfo(String recvdMsg) {
        System.out.println(recvdMsg);
    }

    private void fileRecv(User user) throws IOException {
        int port = 2019;
        fileRecv(user, port);
    }

    private void fileRecv(User user, int port) throws IOException {
        FileTransfer fileTransfer = new FileTransfer(user, port);
        fileTransfer.recvFile();
    }

    private void getHistory(User user, String strCmd) throws IOException, ClassNotFoundException {
        GetHistory history = new GetHistory(user, strCmd);
        HistoryMsg historyMsg = history.getHistory();
        if (historyMsg != null) {
            for (int i = 0; i < historyMsg.getMsgSize(); i++) {
                showMsg(historyMsg.getMsg(i));
            }
        }
    }

    private void showMsg(String msg) {
        System.out.println(msg);
    }

    private void showMsg(Msg msg) {
        System.out.println(String.format("%s\t%s:\n%s", msg.getDate().toString(), msg.getUserId(), msg.getContext()));
    }

    private void getList(User user) throws IOException {
        GetList getUserList = new GetList(user);
        ArrayList<UserInfo> userList = getUserList.getList();
        if (userList == null)
            return;
        for (int i = 0; i < userList.size(); i++) {
            System.out.println(
                    String.format("%d:%s(ip is:%s)", i + 1, userList.get(i).getName(), userList.get(i).getIpAddr()));
        }
    }

    private boolean sendFile(User user, String strCmd) throws IOException {

        FileTransfer file = new FileTransfer(user, strCmd);
        if (file.sendFile()) {
            System.out.println("Send File Successfully");
            return true;
        } else {
            System.out.println("Send File failed");
            return false;
        }
    }

    private boolean pairComm(User user, String strCmd) throws IOException {
        PairComm pair = new PairComm(user, strCmd);
        return pair.joinPairComm();
    }

    private boolean groupComm(User user) throws IOException {
        GroupComm group = new GroupComm(user);
        return group.joinGroup(user);
    }

    private boolean userLogin(User user, String strCmd) throws IOException {
        Login login = new Login(user, strCmd);
        if (login.login()) {
            System.out.println("Login successful");
            return true;
        } else {
            System.out.println("Login filed");
            return false;
        }
    }

    private void reg(User user, String strCmd) throws IOException {
        RegisterUser registerUser = new RegisterUser(user, strCmd);
        if (registerUser.reg()) {
            System.out.println("Register successful!");
        } else {
            System.out.println("Register fail");
        }
    }
}

/*
 * class RecvMsg implements Runnable { private User user;
 * 
 * public RecvMsg(User user) { this.user = user; }
 * 
 * @Override public void run() { try { String recvMsg = user.recvMsg();
 * 
 * } catch (IOException e) { e.printStackTrace(); }
 * 
 * }
 * 
 * }
 */