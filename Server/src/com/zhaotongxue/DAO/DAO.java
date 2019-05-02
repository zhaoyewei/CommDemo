package com.zhaotongxue.DAO;

import com.zhaotongxue.User;

import java.sql.ResultSet;

public interface DAO {
    public String IdentifyUserNameAndPassword(String userName, String password, User user);
    public HistoryMsg getHistory();
}
