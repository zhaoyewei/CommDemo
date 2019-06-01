package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zhao
 * 群组通信管理
 * @version 1.0
 */
public class GroupMaster {
    //唯一的管理对象
    private static GroupMaster groupMaster = new GroupMaster();
    //群组通信里面的用户列表
    private static ArrayList<User> groupUsers = new ArrayList<User>();

    /**
     *
     * @return 获得群组管理对象列表
     */
    public ArrayList<User> getGroupUsers() {
        return groupUsers;
    }

    /**
     *
     * @return 获得群组管理对象列表
     */
    public static GroupMaster getGroupMaster() {
        return groupMaster;
    }

    /**
     * @param msg
     * 消息
     * @param user
     * 消息内容
     *发送消息，如果是//EXIT则退出，如果出现异常则抛出异常，通知主线程退出
      */
    public boolean sendMsg(User user, String msg) throws IOException {
        try {
            boolean exit = false;
            if (msg.equals("//EXITGROUP")) {
                msg = user.getName() + " Exited group talk,left:" + getGroupUserNumber();
                for (int i = 0; i < getGroupUserNumber(); i++) {
                    if (!getUser(i).equals(user)) {
                        getUser(i).send(msg);
                    }
                }
                GroupMaster.getGroupMaster().removeUser(user);
                return false;
            }
            else if (msg.equals("//EXIT")) {
                msg = user.getName() + " Exited group talk,left:" + getGroupUserNumber();
                for (int i = 0; i < getGroupUserNumber(); i++) {
                    if (!getUser(i).equals(user)) {
                        getUser(i).send(msg);
                    }
                }
                groupMaster.removeUser(user);
                removeUser(user);
                return false;
            }
            for (int i = 0; i < getGroupUserNumber(); i++) {
                if (!getUser(i).equals(user)) {
                    getUser(i).send(user.getName() + ":" + msg);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("User:" + user.getName() + " eixted");
            removeUser(user);
            try {
                ListMaster.getListMaster().removeUser(user);
            } catch (IOException ex) {
                ex.printStackTrace();
                // 抛出异常让进程退出
            }
            throw new IOException();
        }
    }

    /**
     *
     * @param user
     * 加入群组通信的用户
     */
    public void addUser(User user) {
        groupUsers.add(user);
    }


    /**
     *
     * @return 群组中用户数量
     */
    public int getGroupUserNumber() {
        return getGroupUsers().size();
    }

    /**
     *
     * @param user
     * 推出的用户
     */
    public void removeUser(User user) {
        getGroupUsers().remove(user);
    }

    /**
     *
     * @param i
     * 下标
     * @return 第i个群组中的用户
     */
    public User getUser(int i) {
        return getGroupUsers().get(i);
    }

    /*
     * public GroupMaster(User user) throws IOException { if(user!=null){
     * ListMaster.getListMaster().addUser(user); } }
     */
}

/*
 * class GroupProcess implements Runnable { User user = null;
 * 
 * public GroupProcess(User user) { this.user = user; }
 * 
 * @Override public void run() { while (true) { try { String msg = user.recv();
 * System.out.println("User " + user.getName() + ":" + msg + "\n"); boolean exit
 * = false; if (msg == "//EXIT") {
 * GroupMaster.getGroupMaster().removeUser(user); msg = user.getName() +
 * " 退出群组聊天"; exit = true; } for (int i = 0; i <
 * GroupMaster.getGroupMaster().getGroupUserNumber(); i++) { if
 * (!GroupMaster.getGroupMaster().getUser(i).equals(user)) {
 * GroupMaster.getGroupMaster().getUser(i).send(msg); } } if (exit) {
 * GroupMaster.getGroupMaster().removeUser(user); return; } } catch (IOException
 * e) { System.out.println("User:" + user.getName() + " eixted");
 * System.out.println(e.getMessage());
 * GroupMaster.getGroupMaster().removeUser(user); try {
 * ListMaster.getListMaster().removeUser(user); } catch (IOException ex) {
 * ex.printStackTrace(); } return; } } } }
 * 
 */
