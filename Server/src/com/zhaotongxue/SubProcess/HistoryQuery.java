package com.zhaotongxue.SubProcess;

import com.zhaotongxue.DAO.HistoryMsg;
import com.zhaotongxue.DAO.UserMsgDBHandler;
import com.zhaotongxue.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class HistoryQuery {
    private UserMsgDBHandler userMsgDBHandler=null;
    private User user=null;
    public HistoryQuery(User user1,User user2) throws SQLException {
        this(user1,user2.getName());
    }
    //一个是传入当前的用户名，另一个是对方用户名,数据库名称为user1_user2,user1_user2按照字典序排序
    public HistoryQuery(User user1,String user2Name) throws SQLException {
         userMsgDBHandler=new UserMsgDBHandler(user1.getName(),user2Name);
         this.user=user1;
    }
    //把文件历史记录对象返回
    public void QueryHistory() throws IOException {
        HistoryMsg historyMsg=userMsgDBHandler.getHistory();
        /*
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int msgSize=historyMsg.getMsgSize();
        String history=""+msgSize;
        for(int i=0;i<msgSize;i++){
            history+="\n"+historyMsg.getMsgUserid(i)+"//"+historyMsg.getMsgContent(i)+"//"+simpleDateFormat.format(historyMsg.getMsgDate(i));
        }
        user.send(history);
         */
        user.sendObj(historyMsg);
    }
}
