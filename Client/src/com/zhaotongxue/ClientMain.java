package com.zhaotongxue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.zhaotongxue.Commands;
import com.zhaotongxue.CommandsConverter;
import com.zhaotongxue.*;

/**
 * ClientMain
 */
public class ClientMain {

    public static void main(String[] args) {
        ClientMain clientMain=new ClientMain();
        clientMain.MianProcess();
    }

    public void MianProcess() {
        String host = "10.122.195.204";
        int port = 1919;
        BufferedReader cmdReader = null;
        User user = null;
        while (true) {
            try {
                cmdReader = new BufferedReader(new InputStreamReader(System.in, "GBK"));
                Socket socket = new Socket(host, port);
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
        String strCmd = null;
        Commands status = Commands.NONE;
        while (true) {
            try {
                strCmd = cmdReader.readLine();
                status = CommandsConverter.getConverter().toCmds(strCmd);
                switch (status) {
                case NONE:
                    break;
                case PAIRCOMM:
                    this.pairComm(user, strCmd);
                    break;
                case GROUPCOMM:
                    this.groupComm(user);
                    break;
                case EXITPAIR:
                    new ExitPair(user);
                    break;
                case EXITGROUP:
                    new ExitGroup(user);
                    break;
                case FILETRANSFER:
                    this.fileTransfer(user,strCmd);
                    break;
                case LOGIN:
                    this.login(user, strCmd);
                    break;
                case REGISTER:
                    this.reg(user, strCmd);
                    break;
                    case GETLIST:
                    this.getList(user);
                    case HISTORY:
                    this.getHistory(user, strCmd);
                case EXIT:
                    new ExitClient(user);
                    break;
                default:
                    break;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    new ExitClient(user);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println("Exit Program");
            }
        }
    }

    private void getHistory(User user, String strCmd) {
        History history = new History(user, strCmd);
    }

    private void getList(User user) {
        GetList getUserList = new GetList(user);
    }

    private void fileTransfer(User user, String strCmd) {
        FileTransfer file = new FileTransfer(user, strCmd);
    }

    private void pairComm(User user, String strCmd) {
        PairComm pair=new PairComm(user,strCmd);
    }

    private void groupComm(User user) {
        GroupComm group = new GroupComm(user);
    }

    private void login(User user, String strCmd) throws IOException {
        Login login = new Login(user, strCmd);
        if (login.login()) {
            System.out.println("Login successful");
        } else {
            System.out.println("Login filed");
        }
    }

    public void reg(User user, String strCmd) throws IOException {
        RegisterUser registerUser = new RegisterUser(user, strCmd);
        if (registerUser.reg()) {
            System.out.println("Register successful!");
        } else {
            System.out.println("Register fail");
        }
    }
}