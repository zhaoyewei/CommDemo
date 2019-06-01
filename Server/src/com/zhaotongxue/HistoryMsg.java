package com.zhaotongxue;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author zhao
 * 用来传输历史消息
 * @version 1.0
 * 本来打算用传输对象的形式来传输历史消息，后来因为消息格式不一致客户端不好处理所以转为字符串处理
 */
class SeriableSimpDateFormat extends SimpleDateFormat implements Serializable{
    public SeriableSimpDateFormat(String s){
        super(s);
    }
}
/**
 * @author zhao
 * 用来查询、传输历史消息
 * @version 1.0
 */
public class HistoryMsg implements Serializable {
    //消息列表
    private ArrayList<Msg> msgs=new ArrayList<Msg>();
    //日期格式
    private SeriableSimpDateFormat simpleDateFormat=new SeriableSimpDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     *
     * @param userId
     * 发送消息用户id
     * @param content
     * 内容
     * @param msgDate
     * 日期
     * @return 是否添加成功
     */
    public boolean addMsg(String userId,String content,Date msgDate){
        return msgs.add(new Msg(userId,content,msgDate));
    }
    /**
     *
     * @param userId
     * 发送消息用户id
     * @param content
     * 内容
     * @param strDateTime
     * 日期
     * @return 是否添加成功
     */
    public boolean addMsg(String userId,String content,String strDateTime){
        try{
            Date msgDate=simpleDateFormat.parse(strDateTime);
            addMsg(userId,content,msgDate);
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
        finally {
            return true;
        }

    }

    /**
     *
     * @param i
     *  消息下标
     * @return 第i个消息
     */
    public Msg getMsg(int i){
        return msgs.get(i);
    }
    /**
     *
     * @param i
     *  消息下标
     * @return 第i个消息内容
     */
    public String getMsgContent(int i){
        return msgs.get(i).getContext();
    }
    /**
     *
     * @param i
     *  消息下标
     * @return 第i个消息日期
     */
    public Date getMsgDate(int i){
        return msgs.get(i).getDate();
    }
    /**
     *
     * @param i
     *  消息下标
     * @return 第i个消息发送方id
     */
    public String getMsgUserid(int i){
        return msgs.get(i).getUserId();
    }
    /**
     *
     * @return 消息大小
     */
    public int getMsgSize(){
        return msgs.size();
    }
}

/**
 * @author zhao
 * Msg本来用于消息传输，后来作废
 * @version 1.0
 */
class Msg implements Serializable{
    private static final long serialVersionUID = 1L;
    private String content = null;
    private String userId=null;
    private Date date=null;

    public Msg(String context, String userId, Date date) {
        this.content = context;
        this.userId = userId;
        this.date = date;
    }

    public String getContext() {
        return content;
    }

    public void setContext(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
