package com.zhaotongxue.SubProcess;

import com.zhaotongxue.DAO.UserMsgDBHandler;
import com.zhaotongxue.User;

import java.sql.SQLException;

/**
 * @author zhao
 * 验证登录
 * @datetime 2019年6月1日
 * @version  1.0
 */
public class LoginIdentify {
    private UserMsgDBHandler userMsgDBHandler=null;

    /**
     *
     * @return 返回位的当前对象
     */
    public static LoginIdentify getLoginIdentify() {
        return loginIdentify;
    }

    //用户消息数据库管理器，管理全部登陆的数据库处理
    private static LoginIdentify loginIdentify;

    //静态初始化类
    static {
        try {
            loginIdentify = new LoginIdentify();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * 处理登录数据库的具体操作
     * @throws SQLException
     * 出现数据库操作异常
     */
    public LoginIdentify() throws SQLException {
        this.userMsgDBHandler=new UserMsgDBHandler();
    }

    /**
     *
     * @param userName
     * 输入的用户名
     * @param password
     * 密码
     * @param user
     * 用来回送消息的对象
     * @return 用户上一次登陆的时间和ip
     * @throws SQLException
     * 数据库异常
     */
    public String IdentifyUserNameAndPassword(String userName, String password, User user) throws SQLException {
        return userMsgDBHandler.IdentifyUserNameAndPassword(userName,password,user);
    }

    /**
     *
     * @param userName
     * 用户名
     * @param password
     * 密码
     * @return
     * 注册代码
     * @throws SQLException
     * 数据库异常
     */
    public int Register(String userName,String password) throws SQLException {
        return userMsgDBHandler.Register(userName,password);
    }
}
