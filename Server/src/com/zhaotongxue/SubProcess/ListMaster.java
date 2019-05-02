package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;
import com.zhaotongxue.SubProcess.UserInfo;;
import java.io.IOException;
import java.util.ArrayList;

final public class ListMaster {
    private static ArrayList<UserInfo> userInfoList=new ArrayList<UserInfo>();
    private static ArrayList<User> userList=new ArrayList<User>();
    private static ListMaster listMaster=new ListMaster();
    //获得唯一的listMaster对象
    public static ListMaster getListMaster() {
        return listMaster;
    }

    //寻找指定ip用户
    public User findUser(String userName) {
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).getName().equals(userName)){
                return userList.get(i);
            }
        }
        return null;
    }

    //获得当前连接用户数量
    public int getUserNumbers(){
        return userList.size();
    }
    //返回当前唯一的这一个userList
    public ArrayList<User> getUserList(){
        return userList;
    }
    //添加用户
    public void addUser(User user) throws IOException {
        userList.add(user);
        userInfoList.add(new UserInfo(user.getAddr(),user.getName()));
        try{
            user.send("Welcome to Server");
        } catch (IOException e) {
            throw e;
        }
    }
    //删除用户
    public void removeUser(User user) throws IOException {
        try {
            user.disconnect();
        }catch (IOException e){

        }
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).equals(user)){
                userList.remove(i);
                userInfoList.remove(i);
                return;
            }
        }
        return;
    }

    public User getUser(int i) {
        return ListMaster.getListMaster().getUserList().get(i);
    }

    public ArrayList<UserInfo> getUserInfoList() {
        return userInfoList;
    }
}
