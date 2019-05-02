package com.zhaotongxue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * User
 */
public class User {

    private Socket socket;
    private InetAddress addr;
    private String name;
    BufferedReader recv = null;
    BufferedWriter sender = null;

    public User(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
        this.name = socket.getInetAddress().toString().substring(1);
        try {
            recv = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            sender = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the addr
     */
    public InetAddress getAddr() {
        return addr;
    }

    /**
     * @param addr the addr to set
     */
    public void setAddr(InetAddress addr) {
        this.addr = addr;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

	public void send(String strCmd) throws IOException {
        this.sender.write(strCmd + "\n");
        this.sender.flush();
    }

    public String recvMsg() throws IOException {
        return this.recv.readLine();
    }
}