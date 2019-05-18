package com.zhaotongxue;

import java.io.IOException;

/**
 * GetHistory
 */
public class GetHistory {

    private User user;
    private String strCmd;

    public GetHistory(User user, String cmd) {
        this.user = user;
        this.strCmd = cmd;
    }

    public HistoryMsg getHistory() throws IOException, ClassNotFoundException {
        String[] cmds = strCmd.split(" ");
        if (cmds.length != 2){
            return null;
        } else {
            this.user.send(String.format("%s %s",CommandsConverter.getConverter().getStrCmd(Commands.HISTORY),cmds[1]));
            //String his=this.user.recvMsg();
//            HistoryMsg obj = (HistoryMsg) user.readObject();
            String str=user.recvMsg();
            HistoryMsg historyMsg=new HistoryMsg();
            String[] strs=str.split("//MSG:");
            for(int i=1;i<strs.length;i++){
                String id=strs[i].split("//")[0];
                String content=strs[i].split("//")[1];
                String date=strs[i].split("//")[2];
                historyMsg.addMsg(id,content,date);
                System.out.println(strs[i]);
            }
            HistoryMsg obj=historyMsg;
            if(obj==null){
                return null;
            }else{
                return obj;
            }
        }


    }
    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the strCmd
     */
    public String getStrCmd() {
        return strCmd;
    }

    /**
     * @param strCmd the strCmd to set
     */
    public void setStrCmd(String strCmd) {
        this.strCmd = strCmd;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}