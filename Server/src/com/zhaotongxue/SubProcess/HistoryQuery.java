package com.zhaotongxue.SubProcess;

import com.zhaotongxue.HistoryMsg;
import com.zhaotongxue.DAO.UserMsgDBHandler;
import com.zhaotongxue.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author zhao
 * 历史消息查询
 * @version 1.0
 */
public class HistoryQuery {
    private UserMsgDBHandler userMsgDBHandler=null;
    private User user=null;

    /**
     *
     * @param user1
     * 查询方用户
     * @param user2
     * 查询的对方的用户
     * @throws SQLException
     */
    public HistoryQuery(User user1,User user2) throws SQLException {
        this(user1,user2.getName());
    }

    /**
     *
     *一个是传入当前的用户名，另一个是对方用户名,数据库名称为user1_user2,user1_user2按照字典序排序
     * @param user1
     * 查询方用户
     * @param user2Name
     * 查询的对方的用户名
     * @throws SQLException
     */
    public HistoryQuery(User user1,String user2Name) throws SQLException {
         userMsgDBHandler=new UserMsgDBHandler(user1.getName(),user2Name);
         this.user=user1;
    }

    /**
     *
     *把文件历史记录对象返回,后来改为把消息拼接成String发送
     * @throws IOException
     */
    public void QueryHistory() throws IOException {
        HistoryMsg historyMsg=userMsgDBHandler.getHistory();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //拼接历史消息字符串
        String s="";
        for(int i=0;i<historyMsg.getMsgSize();i++){
           s=s+"//MSG:"+historyMsg.getMsgUserid(i)+"//"+historyMsg.getMsgContent(i)+"//"+simpleDateFormat.format(historyMsg.getMsgDate(i));
        }
        user.send(s);
    }
}
