package com.zhaotongxue;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
class SeriableSimpDateFormat extends SimpleDateFormat implements Serializable{
    private static final long serialVersionUID = 1L;

    public SeriableSimpDateFormat(String s) {
        super(s);
    }
}
public class HistoryMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Msg> msgs = new ArrayList<Msg>();
    private SeriableSimpDateFormat simpleDateFormat=new SeriableSimpDateFormat("yyyy-MM-dd hh:mm:ss");
    public boolean addMsg(String userId,String content,Date msgDate){
        return msgs.add(new Msg(userId,content,msgDate));
    }
    public boolean addMsg(String userId,String content,String strDateTime){
        try{
            Date msgDate=simpleDateFormat.parse(strDateTime);
            addMsg(userId,content,msgDate);
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public Msg getMsg(int i){
        return msgs.get(i);
    }
    public String getMsgContent(int i){
        return msgs.get(i).getContext();
    }
    public Date getMsgDate(int i){
        return msgs.get(i).getDate();
    }
    public String getMsgUserid(int i){
        return msgs.get(i).getUserId();
    }
    public int getMsgSize(){
        return msgs.size();
    }
}