package com.zhaotongxue;

import com.zhaotongxue.SubProcess.ListMaster;
import com.zhaotongxue.SubProcess.PairMaster;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author zhao
 * @version  1.0
 */
public class User{
    /**
     * User 包括socket，用来通信，Writer，Reader用来实现具体通信
     */
    private static PairMaster pairMaster=null;
    private Socket msgSocket=null;
    private Socket socket=null;
    private BufferedWriter bufferedWriter=null;
    private BufferedReader bufferedReader=null;
    private InetAddress addr=null;
    private String name=null;
    private BufferedWriter msgWriter=null;
    private boolean logined=false;

    /**
     * @return 当前对象的群组对话管理器
     */
    public PairMaster getPairMaster() {
        return pairMaster;
    }

    /**
     * 设置群组对话管理器
     */
    public void setPairMaster(PairMaster pairMaster) {
        this.pairMaster = pairMaster;
    }

    /**
     *
     * @param msgSocket
     * 专门用来把消息发送给client主线程之外的通话
     * @throws IOException
     * 异常则中断
     */
    public void setMsgSocket(Socket msgSocket) throws IOException {
        this.msgSocket = msgSocket;
        msgWriter=new BufferedWriter(new OutputStreamWriter(msgSocket.getOutputStream()));
    }

    /**
     *
     * @param str
     * 发送消息
     * @throws IOException
     * 有异常直接退出
     */
    public void msgSend(String str) throws IOException {
        msgWriter.write(str+"\n");
        msgWriter.flush();
    }

    /**
     *
     * @param socket
     * client主进程socket
     * @throws IOException
     */
    public User(Socket socket) throws IOException {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket=socket;
        addr=this.socket.getInetAddress();
    }

    /**
     * 用户名只要相等两个用户就相同
     * @param obj
     * 传入user
     * @return 两个用户是否相同
     */
    @Override
    public boolean equals(Object obj) {
        try{
            User userObj=(User)obj;
            if(userObj.getName().equals(this.getName())) return true;
            return false;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @param  msg
     * 主线程发送消息方法
     * 不加锁，枷锁会出现阻塞
     */
    public void send(String msg) throws IOException {
        //发送消息并刷新
        bufferedWriter.write(msg+"\n");
        bufferedWriter.flush();
    }
    String recvMsg;
    /**
     *接收Client 主线程消息
     */
    public String recv() throws IOException {
        //获得消息并且返回
        recvMsg=bufferedReader.readLine();
        return recvMsg;
    }

//    public String getName(){return getAddr().toString().substring(1);}

    /**
     *
     * @return 用户名
     */
    public String getName(){
        return this.name;
    }

    /**
     *
     * @return ip地址
     */
    public InetAddress getAddr() {
        return addr;
    }

    /**
     * 发送全部用户列表
     * @throws IOException
     */
    public void getList() throws IOException {
        //获得用户列表并且发送
//        ArrayList<UserInfo> userList= ListMaster.getListMaster().getUserInfoList();
        ArrayList<User> userList=ListMaster.getListMaster().getUserList();

        //直接发送对象在客户端不方便处理
        //sendObj(userList);

        //转化为String传输
        String s="";
        for(int i=0;i<userList.size();i++){
           s+="//MSG:"+userList.get(i).getAddr().toString().substring(1)+"//"+userList.get(i).getName();
        }
        send(s);
    }

    /**
     * 断开连接
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.socket.close();
        throw new IOException();
    }

    /**
     *
     *发送对象，主要是用来传输历史记录
     */
    /*
    public void sendObj(Serializable obj) throws IOException {
        OutputStream outputStream=socket.getOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(outputStream);
        oos.writeObject(obj);
        oos.flush();
        bufferedWriter.flush();
        oos=null;
        return;
    }
     */

    /**
     *
     * @param b
     * 设置登录状态
     */
    public void setSession(boolean b) {
        this.logined=b;
    }

    /**
     *
     * @param s
     * 设置用户名
     */
    public void setName(String s) {
        this.name=s;
    }

    /**
     *
     * @return 登陆状态
     */
    public boolean isLogined() {
        return logined;
    }
}
