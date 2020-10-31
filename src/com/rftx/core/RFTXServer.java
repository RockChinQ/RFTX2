package com.rftx.core;

import java.net.ServerSocket;
import java.net.Socket;

import com.rftx.conn.DefaultConn;
import com.rftx.util.Debugger;

public class RFTXServer implements Runnable{
    RFTXHost host;
    private Thread proxyThr=new Thread(this);
    RFTXServer(RFTXHost host,int port)throws Exception{
        this.host=host;
        serverSocket=new ServerSocket(port);
    }
    public void start(){
        proxyThr.start();
    }
    public void stop(){
        //server accepting conn thread can be stopped anytime.
        proxyThr.stop();
    }
    public void addValidToken(String name,String token){
        host.authenticator.getAuthTokenMap().put(name, token);
    }
    public String removeToken(String name){
        return host.authenticator.getAuthTokenMap().remove(name);
    }
    //related to accepting conn
    ServerSocket serverSocket;

    @Override
    public void run(){
        Debugger.say("Accepting...");
        while(true){
            try{
                Socket newSocket=serverSocket.accept();
                DefaultConn defaultConn=new DefaultConn(host,newSocket);
                host.defaultConns.add(defaultConn);
                defaultConn.getProxyThread().start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
