package com.zhaotongxue.SubProcess;

import com.zhaotongxue.User;
import com.zhaotongxue.UserInfo;;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zhao
 * 管理所有连接用户
 * @version 1.0
 */
final public class ListMaster {
    //用户信息List
    private static ArrayList<UserInfo> userInfoList=new ArrayList<UserInfo>();
    //用户管理List
    private static ArrayList<User> userList=new ArrayList<User>();
    //唯一的ListMaster对象，所有用户连接都由他管理
    private static ListMaster listMaster=new ListMaster();
    /**
     * @return 获得唯一的listMaster对象
     */
    public static ListMaster getListMaster() {
        return listMaster;
    }

    /**
     *
     * @param userName
     * 根据提供的UserName查询是否已经登录
     * @return 指定用户名用户是否已经登录
     */
    public boolean isAlreadyLogined(String userName){
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).isLogined()&&userList.get(i).getName().equals(userName)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param userName
     * 想要寻找的用户名
     * @return 找到了就返回用户名，没有就返回null
     */
    public User findUser(String userName) {
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).isLogined()&&userList.get(i).getName().equals(userName)){
                return userList.get(i);
            }
        }
        return null;
    }

    /**
     * @return 获得当前连接用户数量
     */
    public int getUserNumbers(){
        return userList.size();
    }
    /**
     *@return 返回当前唯一的这一个userList
     */
    public ArrayList<User> getUserList(){
        return userList;
    }

    /**
     *
     * @param user
     * 添加用户
     * @throws IOException
     * 发送出现了问题，连接中断，用户退出
     */
    public void addUser(User user) throws IOException {
        userList.add(user);
        userInfoList.add(new UserInfo(user.getAddr(),user.getName()));
        try{
            user.send("Welcome to Server");
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     *
     * @param user
     * 用户退出
     * @throws IOException
     * 发生意外，抛出异常退出
     */
    public void removeUser(User user) throws IOException {
        try {
            user.disconnect();
            for(int i=0;i<userList.size();i++){
                if(userList.get(i).equals(user)){
                    userList.remove(i);
                    userInfoList.remove(i);
                    return;
                }
            }
        }catch (IOException e){

        }
    }

    /**
     *
     * @param i
     * 想要获得的用户的下标
     * @return 返回第i个用户
     */
    public User getUser(int i) {
        return ListMaster.getListMaster().getUserList().get(i);
    }

    /**
     *
     * @return 用户信息列表
     */
    public ArrayList<UserInfo> getUserInfoList() {
        return userInfoList;
    }
}
