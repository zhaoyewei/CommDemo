package com.zhaotongxue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;

/**
 * FileTransfer
 * @author zhao
 * 处理文件的接收和发送
 * @date 2019年6月1日
 * @version 1.0
 */
public class FileTransfer {

    private static final long MAXSIZE = 1024 * 20;
    private User user;
    private String recvUserName;
    private File file;

    public FileTransfer(User user) {
        this.user = user;
    }


    public FileTransfer(User user, String path) {
        this.user = user;
        file = new File(path);
    }

    public FileTransfer(User user,String recvUserName,String path){
        this.user=user;
        file=new File(path);
        this.recvUserName=recvUserName;
    }

    /**
     *准备发送文件
     * @return 服务器准备装填
     * @throws IOException
     */
    public String prepareSendFile() throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.FILETRANSFER)+" "+recvUserName+" "+file.getName());
        return user.recvMsg();
    }

    /**
     * 接收文件
     * @param port
     * @param fileName
     * @throws IOException
     */
    public void recvFile(int port,String fileName) throws IOException {
        Thread fileRecvThread = new Thread(new FileRecvThread(user,port, fileName));
        fileRecvThread.start();
    }

    /*
    public void recvFile(int port) throws IOException {
//        String fileName = user.recvMsg();
//        String inetAddress= user.recvMsg();
        // String[] paths= path.split(File.pathSeparator);
        // String fileName = paths[paths.length - 1];
        Thread fileRecvThread = new Thread(new FileRecvThread(port, fileName));
        fileRecvThread.start();
        user.send("READY");
    }
     */

    /**
     * 具体处理发送文件
     * @return
     * @throws IOException
     */
    public boolean sendFile() throws IOException {
        if (!file.exists()) {
            System.out.println("File not exits");
            return false;
        } else if (!file.isFile()) {
            System.out.println("what you chose is not a file");
            return false;
        } else {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fileChannel = fis.getChannel();
            if (fileChannel.size() > MAXSIZE) {
                System.out.println("File is too big,it should be less than 20M");
                fis.close();
                return false;
            } else {
                fis.close();
                String recvMsg = prepareSendFile();
                //Find user,and server get file name;
                //recvMsg :-1 :not found. other:recvIP;
                if(!recvMsg.equals("-1")) {
                    Thread fileTransferThread = new Thread(new FileSendThread(user, file, recvMsg));
                    fileTransferThread.start();
                    return true;
                }else{
                    System.out.println("Can't find user");
                    return false;
                }
                /*
                 * if (!recvMsg.equals("READY")) { System.out.println("Failed"); fis.close();
                 * return false; } else { Thread fileTransferThread = new Thread(new
                 * FileSendThread(user, file, recvMsg)); fileTransferThread.start(); return
                 * true; }
                 */
            }
        }
    }
}

/**
 * @author zhao
 * 发送文件子进程
 * @date 2019年6月1日
 * @version 1.0
 */
class FileSendThread implements Runnable {

    private User user;
    private FileChannel fileChannel;
    private File file;
    private Socket socket;
    private String recvIp;
    private int port;

    public FileSendThread(User user, File f, String recvIp) throws UnknownHostException, IOException {
        this.user = user;
        this.file = f;
        this.recvIp = recvIp;
        port = 9009;
    }

    public FileSendThread(User user, FileChannel fileChannel, String recvIp) throws UnknownHostException, IOException {
        this.user = user;
        this.fileChannel = fileChannel;
        this.recvIp = recvIp;
        port =9009;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(recvIp.substring(1), port);
            if (socket == null) {
                return;
            }
//            user.send("READY");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int size = 0;
            // while ((size = fileChannel.read(buffer)) != -1) {
            while ((size = fis.read(buffer)) != -1) {
                socket.getOutputStream().write(buffer, 0, size);
                socket.getOutputStream().flush();
            }
            System.out.println(String.format("Send to %s successfully", recvIp));
            socket.close();
            fis.close();
            System.out.println("send successfully");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}

/**
 * @author zhao
 * 接收文件线程
 * @date 2019年6月1日
 * @version 1.0
 */
class FileRecvThread implements Runnable {
    private int port;
    private User user;
    private String fileName;

    public FileRecvThread(User user,int port) {
        this.port = port;
        this.user=user;
    }

    public FileRecvThread(User user,int port, String fileName) {
        this.user=user;
        this.port = port;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
//            user.send("READY");
            Socket recvSocket = serverSocket.accept();
            if (fileName == null)
                fileName = "recvFile";
            File f = new File(fileName);
            synchronized (f) {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                byte[] bytes = new byte[1024];
                while (recvSocket.getInputStream().read(bytes) != -1) {
                    fos.write(bytes);
                    fos.flush();
                }
                fos.close();
                serverSocket.close();
                System.out.println("Recv file successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}