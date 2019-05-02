package com.zhaotongxue;
import java.io.Serializable;
import java.sql.Date;

public class Msg implements Serializable {
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