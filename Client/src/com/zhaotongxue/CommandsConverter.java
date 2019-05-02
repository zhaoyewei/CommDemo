package com.zhaotongxue;

import com.zhaotongxue.Commands;

public class CommandsConverter {
    private static CommandsConverter converter = new CommandsConverter();

    public Commands toCmds(String s) {
        String str = s.split(" ")[0];
        switch (str) {
        case "Reg":
            return Commands.REGISTER;
        case "Login":
            return Commands.LOGIN;
        case "PairComm":
            return Commands.PAIRCOMM;
        case "//EXITPAIR":
            return Commands.EXITPAIR;
        case "GroupComm":
            return Commands.GROUPCOMM;
        case "//EXITGROUP":
            return Commands.EXITGROUP;
        case "//EXIT":
            return Commands.EXIT;
        case "FileTransfer":
            return Commands.FILETRANSFER;
        case "History":
            return Commands.HISTORY;
            case "GetList":
            return Commands.GETLIST;
        default:
            return Commands.NONE;
        }
    }

    public String getStrCmd(Commands cmd) {
        switch (cmd) {
        case REGISTER:
            return "Reg";
        case LOGIN:
            return "Login";
        case PAIRCOMM:
            return "PairComm";
        case EXITPAIR:
            return "//EXITPAIR";
        case GROUPCOMM:
            return "GroupComm";
        case EXITGROUP:
            return "//EXITGROUP";
        case EXIT:
            return "//EXIT";
        case FILETRANSFER:
            return "FileTransfer";
        case HISTORY:
            return "History";
        case GETLIST:
            return "GetList";
        default:
            return null;
        }
    }

    /**
     * @return the converter
     */
    public static CommandsConverter getConverter() {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public static void setConverter(CommandsConverter converter) {
        CommandsConverter.converter = converter;
    }

}