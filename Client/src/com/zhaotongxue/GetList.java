package com.zhaotongxue;
import com.zhaotongxue.UserInfo;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zhao
 * @version 1.0
 * @Date 2019年5月11日
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
            
        ArrayList<UserInfo> usreList = (ArrayList<UserInfo>) user.readObject(); 
        return usreList;
        } catch (IOException | ClassNotFoundException|ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
