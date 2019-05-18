package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;

import java.io.IOException;
import java.util.ArrayList;

public class GroupMaster {
    private static GroupMaster groupMaster = new GroupMaster();
    private static ArrayList<User> groupUsers = new ArrayList<User>();

    public ArrayList<User> getGroupUsers() {
        return groupUsers;
    }

    public static GroupMaster getGroupMaster() {
        return groupMaster;
    }

    // 发送消息，如果是//EXIT则退出，如果出现异常则抛出异常，通知主线程退出
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
                user.send("1");
                return false;
            }
            if (msg.equals("//EXIT")) {
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

    public void addUser(User user) {
        groupUsers.add(user);
    }

    // public GroupMaster() {
    // this.groupUsers = new ArrayList<User>();
    // }

    public int getGroupUserNumber() {
        return getGroupUsers().size();
    }

    public void removeUser(User user) {
        try {
            user.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getGroupUsers().remove(user);
    }

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
