package com.zhaotongxue;

import java.io.IOException;

/**
 * ExitPair
 */
public class ExitPair {

    private User user;

    public ExitPair(User user) throws IOException {
        this.user = user;
        exitPair();
    }

    public void exitPair() throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.EXITPAIR));
    }
}