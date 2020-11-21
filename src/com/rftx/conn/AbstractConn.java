package com.rftx.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.rftx.core.RFTXHost;

public abstract class AbstractConn implements Runnable{
    public static long CID_INDEX=0;
    public long cid=++CID_INDEX;
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
    /**
     * close socket and stop thread.
     */
    public void dispose() throws Exception{
        this.proxyThr.stop();
        this.socket.close();
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
