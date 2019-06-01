package com.zhaotongxue.DAO;

import com.zhaotongxue.HistoryMsg;
import com.zhaotongxue.User;

import java.sql.SQLException;
import java.util.Date;

public interface DAO {
    public String IdentifyUserNameAndPassword(String userName, String password, User user);
    public HistoryMsg getHistory();
    public void addHistory(User userSent, String user1Msg, Date date) throws SQLException;
    public int Register(String userName, String password) throws SQLException;
}
