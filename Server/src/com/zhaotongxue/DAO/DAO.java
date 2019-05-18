package com.zhaotongxue.DAO;

import com.zhaotongxue.HistoryMsg;
import com.zhaotongxue.User;

public interface DAO {
    public String IdentifyUserNameAndPassword(String userName, String password, User user);
    public HistoryMsg getHistory();
}
