package com.rftx.core;

import java.net.ServerSocket;
import java.net.Socket;

import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.listener.ServerConnListener;
import com.rftx.util.Debugger;

public class RFTXServer implements Runnable, ServerConnListener {
    static int count = 0;
    RFTXHost host;
    private Thread proxyThr;

    RFTXServer(RFTXHost host, int port) throws Exception {
        this.host = host;
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        proxyThr = new Thread(this);
        proxyThr.setName("RFTXServer-" + (count++));
        proxyThr.start();
    }

    public void stop() throws Exception {
        // server accepting conn thread can be stopped anytime.
        serverSocket.close();
        proxyThr.stop();
    }

    public void addValidToken(String name, String token) {
        host.authenticator.getAuthTokenMap().put(name, token);
    }

    public String removeToken(String name) {
        return host.authenticator.getAuthTokenMap().remove(name);
    }

    // related to accepting conn
    ServerSocket serverSocket;

    @Override
    public void run() {
        Debugger.say("Accepting...");
        while (true) {
            try {
                Socket newSocket = serverSocket.accept();
                DefaultConn defaultConn = new DefaultConn(host, newSocket);
                host.defaultConns.add(defaultConn);
                defaultConn.getProxyThread().start();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    ServerConnListener connListener;

    public void setServerConnListener(ServerConnListener connListener) {
        this.connListener = connListener;
    }

    public ServerConnListener getServerConnListener() {
        return this.connListener;
    }
    @Override
    public void connected(ControlConn conn) {
        // TODO Auto-generated method stub
        if(this.connListener!=null){
            connListener.connected(conn);
        }

    }

    @Override
    public void authenticated(ControlConn conn) {
        // TODO Auto-generated method stub
        if(this.connListener!=null){
            connListener.authenticated(conn);
        }
    }

    @Override
    public void disconnected(ControlConn conn) {
        // TODO Auto-generated method stub
        if(this.connListener!=null){
            connListener.disconnected(conn);
        }
    }

}
