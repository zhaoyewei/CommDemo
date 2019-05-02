package com.zhaotongxue.SubProcess;

import com.zhaotongxue.DAO.UserMsgDBHandler;
import com.zhaotongxue.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginIdentify {
    private UserMsgDBHandler userMsgDBHandler=null;
    public static LoginIdentify getLoginIdentify() {
        return loginIdentify;
    }

    private static LoginIdentify loginIdentify;

    static {
        try {
            loginIdentify = new LoginIdentify();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public LoginIdentify() throws SQLException {
        this.userMsgDBHandler=new UserMsgDBHandler();
    }

    public String IdentifyUserNameAndPassword(String userName, String password, User user) throws SQLException {
        //先不验证，直接验证通过
        //return true;
        return userMsgDBHandler.IdentifyUserNameAndPassword(userName,password,user);
    }
    public boolean Register(String userName,String password) throws SQLException {
        return userMsgDBHandler.Register(userName,password);
    }
}
