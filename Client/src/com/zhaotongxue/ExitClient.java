package com.zhaotongxue;

import java.io.IOException;

/**
 * ExitClient
 */
public class ExitClient {

    public ExitClient(User user) throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.EXIT));
    }
}