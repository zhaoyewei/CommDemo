package com.zhaotongxue.SubProcess;

import com.zhaotongxue.DAO.UserMsgDBHandler;
import com.zhaotongxue.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PairMaster {
    private User user1 = null, user2 = null;
    // 用来添加历史记录
    private UserMsgDBHandler userMsgDBHandler = null;

    public PairMaster(User user) {
        this.user1 = user;
    }

    public void setRecvUser(User recvUser) throws SQLException {
        this.user2 = recvUser;
        if (recvUser != null) {
            userMsgDBHandler = new UserMsgDBHandler(user1, user2);
        }
    }

    private boolean TalkTo(String msg,String priMsg, Date date) throws IOException, SQLException {
        //msg is only messeage with out date
        userMsgDBHandler.addHistory(user1, msg, date);
        user2.send(priMsg);
        return true;
    }
    public User getPair(){
        return user2;
    }
    public boolean TalkTo(String msg) throws IOException, SQLException {
        //parse msg to msg and datetime
        if (user2 == null) {
            return false;
        }
        if (msg.equals("//EXITPAIR")) {
            this.setRecvUser(null);
            return false;
        }
        if(msg.equals("//EXIT")){
            throw  new IOException();
        }
        Date inDate = null;
        String[] msgSplit = msg.split("//DATE:");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (msgSplit.length == 1) inDate = new Date();
            else inDate = sdf.parse(msgSplit[1]);
        } catch (ParseException e) {
            inDate=new Date();
        }
        return this.TalkTo(msgSplit[0],msg, inDate);
    }

    /*
    class ConnectedState{
        boolean connectedState=false;

        public boolean isConnectedState() {
            return connectedState;
        }

        public void setConnectedState(boolean connectedState) {
            this.connectedState = connectedState;
        }

        public ConnectedState(boolean connectedState) {
            this.connectedState = connectedState;
        }
    }
    */
    //创建两个线程，分别用来监听对方输入，然后发给另外一方,userMsgDBHandler负责保存发送的信息到数据库
    private void CommUsers(User user1, User user2) throws IOException {
        /*
        final ConnectedState isConnected=new ConnectedState(false);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    while(true){
                        synchronized (isConnected){
                            String user1Msg=user1.recv();
                            if(user1Msg=="//EXIT") isConnected.setConnectedState(false);
                            if(!isConnected.isConnectedState()) return;
                            Date date=new Date();
                            userMsgDBHandler.addHistory(user1,user1Msg,date);
                            user2.send(user1Msg);
                        }
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    while(true){
                        synchronized (isConnected){
                            String user2Msg=user2.recv();
                            if(user2Msg=="//EXIT") isConnected.setConnectedState(false);
                            if(!isConnected.isConnectedState()) return;
                            Date date=new Date();
                            userMsgDBHandler.addHistory(user2,user2Msg,date);
                            user1.send(user2Msg);
                        }
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
        Thread u1_u2=new Thread(new listener(user1,user2,userMsgDBHandler,isConnected));
        Thread u2_u1=new Thread(new listener(user2,user1,userMsgDBHandler,isConnected));
        u1_u2.start();
        u2_u1.start();
         */
        //逻辑是一直接收消息，至于怎么显示那是客户端message的事，这里只是转发
        /*
        while (true) {
            String user1Msg = null;
            user1Msg = user1.recv();
            Date date = new Date();
            userMsgDBHandler.addHistory(user1, user1Msg, date);
            user2.send(user1Msg);
        }
         */
    }
}
/*
class listener implements Runnable {
    private boolean isConnected=true;
    private User user1=null,user2=null;
    private UserMsgDBHandler userMsgDBHandler=null;
    public listener(User user1,User user2,UserMsgDBHandler userMsgDBHandler) {
        this.user1 = user1;
        this.user2=user2;
        this.userMsgDBHandler=userMsgDBHandler;
    }

    @Override
    public void run() {
            while(true){
                try {
                    String user1Msg= null;
                    user1Msg = user1.recv();
                    Date date=new Date();
                    userMsgDBHandler.addHistory(user1,user1Msg,date);
                    user2.send(user1Msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
 */
