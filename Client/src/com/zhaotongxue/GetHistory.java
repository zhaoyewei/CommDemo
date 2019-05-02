package com.zhaotongxue;

/**
 * GetHistory
 */
public class GetHistory {

    private User user;
    private String strCmd;
    public GetHistory(User user,String cmd){
        this.user = user;
        this.strCmd = cmd;
    }

    public boolean getHistory() {
        String[] cmds = strCmd.split(" ");
        if (cmds.length != 2){
            return false;
        } else {
            this.user.send(String.format("%s %s",CommandsConverter.getConverter().getStrCmd(Commands.HISTORY),cmds[1]);
            String his=this.user.recv();
            if(his==null){
                return false;
            }else{
                return true;
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