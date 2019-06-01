package com.zhaotongxue;
import com.zhaotongxue.UserInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @author zhao
 * @version 1.0
 * GetList
 */
public class GetList {
    
    private User user;

    public GetList(User user) {
        this.user = user;
    }

    public ArrayList<UserInfo> getList(){
        try {
            user.send(CommandsConverter.getConverter().getStrCmd(Commands.GETLIST));
            String str=user.recvMsg();
            String[] strs=str.split("//MSG:");
            ArrayList<UserInfo> userList=new ArrayList<>();
            for(int i=1;i<strs.length;i++){
                UserInfo userInfo=new UserInfo(strs[i].split("//")[0],strs[i].split("//")[1]) ;
                userList.add(userInfo);
            }
        //ArrayList<UserInfo> usreList = (ArrayList<UserInfo>) user.readObject();
        return userList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
