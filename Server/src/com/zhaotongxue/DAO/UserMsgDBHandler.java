package com.zhaotongxue.DAO;

import com.zhaotongxue.HistoryMsg;
import com.zhaotongxue.User;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhao
 * 处理数据库操作
 * 名字起得不好，容易误会
 * @version 1.0
 */
public class UserMsgDBHandler extends DBUtils implements DAO {
    private String user1Name = null, user2Name = null;
    private Connection conn = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement=null;

    /**
     *
     * @param user1Name
     * @param user2Name
     * @throws SQLException
     */
    public UserMsgDBHandler(String user1Name, String user2Name) throws SQLException {
        if(user2Name!=null&&user1Name!=null) {
            //user1,user2按照字典序排序
            this.user1Name = user1Name.compareTo(user2Name) < 0 ? user1Name : user2Name;
            this.user2Name = user1Name.compareTo(user2Name) > 0 ? user1Name : user2Name;
            conn = DBUtils.getConnection();
            statement = conn.createStatement();
            InitTwoUsers();
        }
    }

    /**
     * 重载方法
     * @param user1
     * @param user2
     * @throws SQLException
     */
    public UserMsgDBHandler(User user1, String user2) throws SQLException {
        /*
        if(user1!=null&&user2!=null) {
            //user1,user2按照字典序排序
            this.user1Name = user1.getName().compareTo(user2) < 0 ? user1.getName() : user2;
            this.user2Name = user1.getName().compareTo(user2) > 0 ? user1.getName() : user2;
            conn = DBUtils.getConnection();
            statement = conn.createStatement();
            InitTwoUsers();
        }
        */
        this(user1.getName(), user2);
    }

    /**
     * 重载方法
     * @param user1
     * @param user2
     * @throws SQLException
     */
    public UserMsgDBHandler(User user1, User user2) throws SQLException {

        /*
        if(user1!=null&&user2!=null) {
            //user1,user2按照字典序排序
            this.user1Name = user1.getName().compareTo(user2.getName()) > 0 ? user1.getName() : user2.getName();
            this.user2Name = user1.getName().compareTo(user2.getName()) < 0 ? user1.getName() : user2.getName();
            conn = DBUtils.getConnection();
            statement = conn.createStatement();
            InitTwoUsers();
        }
         */
        this(user1.getName(), user2.getName());
    }

    /**
     * 初始化
     * @throws SQLException
     */
    public UserMsgDBHandler() throws SQLException {
        conn = DBUtils.getConnection();
        statement = conn.createStatement();
    }

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 验证登录信息
     * @param userName
     * @param password
     * @param user
     * @return 登录则返回最后一次登陆的ip和时间，否则返回null
     */
    @Override
    public String IdentifyUserNameAndPassword(String userName, String password,User user) {
        String res=null;
        try {
            String checkUser="SELECT username,lastlogintime,lastloginip FROM userinfo WHERE username=? AND password=?;";
            preparedStatement=conn.prepareStatement(checkUser);
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,password);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                res=resultSet.getString("lastlogintime")+"//"+resultSet.getString("lastloginip");
            }
            else{
                return res;
            }
            String updateInfoStr="UPDATE userinfo SET lastlogintime=?,lastloginip=? WHERE username=? AND password=?;";
            preparedStatement=conn.prepareStatement(updateInfoStr);
            preparedStatement.setString(1,simpleDateFormat.format(new Date()));
            preparedStatement.setString(2,user.getAddr().toString().substring(1));
            preparedStatement.setString(3,userName);
            preparedStatement.setString(4,password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     *
     * @return 历史消息
     */
    @Override
    public synchronized HistoryMsg getHistory() {
        if(user1Name==null||user2Name==null||user1Name==user2Name) return null;
        HistoryMsg historyMsg = new HistoryMsg();
        Object[] param = {user1Name.replace('.','_'), user2Name.replace('.','_')};
        try {
            String historyExistsSQL=String.format("SHOW TABLES LIKE 'msg_%s_%s';",param);
            ResultSet resultSet=statement.executeQuery(historyExistsSQL);
            if(!resultSet.next())  return null;
            String sql = String.format("SELECT * FROM msg_%s_%s;", param);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                historyMsg.addMsg(resultSet.getString("sender"), resultSet.getString("content"), resultSet.getDate( "msgdate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyMsg;
    }

    /**
     * 插入消息到数据库
     * @param userSent
     * @param user1Msg
     * @param date
     * @throws SQLException
     */
    @Override
    public  void addHistory(User userSent, String user1Msg, Date date) throws SQLException {
        if(user1Name==null||user2Name==null||userSent==null) return;
        Object[] param = {user1Name.replace('.','_'), user2Name.replace('.','_'), userSent.getName(), user1Msg, simpleDateFormat.format(date)};
        String sql=String.format("INSERT INTO msg_%s_%s VALUES(?,?,?);",user1Name.replace('.','_'),
                user2Name.replace('.','_'));
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,userSent.getName());
            preparedStatement.setString(2,user1Msg);
            preparedStatement.setString(3,simpleDateFormat.format(date));
            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 两个用户还没有聊过就创建个数据库
     * 老师说一般不这么写
     * 但是懒得改了
     * @throws SQLException
     */
    private synchronized void InitTwoUsers() throws SQLException {
        String createTable =
                String.format("CREATE TABLE IF NOT EXISTS msg_%s_%s (sender VARCHAR(36),content VARCHAR (200),msgdate" +
                        " DATETIME);", user1Name.replace('.','_'), user2Name.replace('.','_'));
        statement.execute(createTable);
    }

    /**
     * 注册用户
     * @param userName
     * @param password
     * @return 用户注册状态返回代码
     * @throws SQLException
     */
    @Override
    public int Register(String userName,String password) throws SQLException {
        String dupSQL="SELECT username FROM userinfo WHERE username=?;";
        preparedStatement=conn.prepareStatement(dupSQL);
        preparedStatement.setString(1,userName);
        if(preparedStatement.executeQuery().next()) return -1;
        String regSQL="INSERT INTO userinfo VALUES(?,?,null,null );";
        preparedStatement=conn.prepareStatement(regSQL);
        preparedStatement.setString(1,userName);
        preparedStatement.setString(2,password);
        if(!preparedStatement.execute())return 1;
        else {
            return 0;
        }
    }
}
