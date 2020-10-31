package com.rftx.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.rftx.core.RFTXHost;

public abstract class AbstractConn implements Runnable{
    Socket socket;
    DataInputStream reader;
    DataOutputStream writer;
    //proxy thread that handle this conn
    private Thread proxyThr=new Thread(this);
    public Thread getProxyThread(){
        return proxyThr;
    }
    //host obj
    RFTXHost host;
    public Socket getSocket(){
        return socket;
    }
    public DataInputStream getReader(){
        return reader;
    }
    public DataOutputStream getWriter(){
        return writer;
    }
    public void setSocket(Socket socket){
        this.socket=socket;
    }
    public void initRW()throws Exception{
        this.reader=new DataInputStream(socket.getInputStream());
        this.writer=new DataOutputStream(socket.getOutputStream());
    }
    public void writeMsg(String msg)throws Exception{
        getWriter().writeUTF(msg);
        getWriter().flush();
    }
    public void writeMsgIgnoreException(String msg){
        try{
            writeMsg(msg);
        }catch(Exception ignored){}
    }
}
