package com.zhaotongxue;

import java.io.IOException;

/**
 * ExitPair
 */
public class ExitGroup {

    private User user;

    public ExitGroup(User user) throws IOException {
        this.user = user;
        exitGroup();
    }

    public void exitGroup() throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.EXITGROUP));
    }
}