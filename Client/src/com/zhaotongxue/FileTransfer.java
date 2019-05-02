package com.zhaotongxue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;

/**
 * FileTransfer
 */
public class FileTransfer {

    private static final long MAXSIZE = 1024 * 20;
    private User user;
    private File file;
    private int recvPort;

    public FileTransfer(User user) {
        this.user = user;
    }

    public FileTransfer(User user, int port) {
        this.user = user;
        this.recvPort = port;
    }

    public FileTransfer(User user, String path) {
        this.user = user;
        file = new File(path);
    }

    public String prepareSendFile() throws IOException {
        user.send(CommandsConverter.getConverter().getStrCmd(Commands.FILETRANSFER));
        return user.recvMsg();
    }

    public void recvFile() throws IOException {
        int port = 2019;
        if (this.recvPort != 0)
            port = recvPort;
        recvFile(port);
    }

    public void recvFile(int port) throws IOException {
        String path=user.recvMsg();
        String[] paths= path.split(File.pathSeparator);
        String fileName = paths[paths.length - 1];
        Thread fileRecvThread = new Thread(new FileRecvThread(port,fileName));
        fileRecvThread.start();
        user.send("READY");
    }

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
                if (!recvMsg.equals("READY")) {
                    System.out.println("Failed");
                    fis.close();
                    return false;
                } else {
                    Thread fileTransferThread = new Thread(new FileSendThread(user, file, recvMsg));
                    fileTransferThread.start();
                    return true;
                }
            }
        }
    }
}

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
        port = 2019;
        socket = new Socket(recvIp, port);
    }

    public FileSendThread(User user, FileChannel fileChannel, String recvIp) throws UnknownHostException, IOException {
        this.user = user;
        this.fileChannel = fileChannel;
        this.recvIp = recvIp;
        port = 2019;
        socket = new Socket(recvIp, port);
    }

    @Override
    public void run() {
        try {
            if (socket == null) {
                return;
            }
            user.send("READY");
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
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}

class FileRecvThread implements Runnable {
    private int port;
    private String fileName;

    public FileRecvThread(int port) {
        this.port = port;
    }

    public FileRecvThread(int port, String fileName) {
        this.port = port;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket recvSocket = serverSocket.accept();
            if (fileName == null)
                fileName = "recv";
            File f = new File(fileName);
            synchronized (f) {
                if (f.exists()){
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