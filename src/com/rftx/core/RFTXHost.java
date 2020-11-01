package com.rftx.core;

import java.beans.ExceptionListener;
import java.util.ArrayList;
import com.rftx.auth.TokenAuthenticator;
import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.conn.TransportConn;
import com.rftx.listener.TaskListener;
import com.rftx.util.BasicInfo;

public class RFTXHost implements TaskListener{
    public ArrayList<DefaultConn> defaultConns=new ArrayList<>();
    public ArrayList<ControlConn> controlConns=new ArrayList<>();
    public ArrayList<TransportConn> transportConns=new ArrayList<>();
    public ArrayList<FileTaskInfo> getAllTaskInfo(){
        ArrayList<FileTaskInfo> result=new ArrayList<>();
        for(TransportConn transportConn:transportConns){
            result.add(transportConn.getTaskInfo());
        }
        return result;
    }
    public FileTaskInfo getTaskInfo(String token){
        for(TransportConn transportConn:transportConns){
            if(transportConn.info.taskToken.equals(token)){
                return transportConn.getTaskInfo();
            }
        }
        return null;
    }
    public ArrayList<TransportConn> getAllTransportConn(){
        return this.transportConns;
    }
    public TransportConn getTransportConn(String token){
        for(TransportConn transportConn:transportConns){
            if(transportConn.info.taskToken.equals(token)){
                return transportConn;
            }
        }
        return null;
    }
    //basic info 
    public String hostName="";
    //Client and server
    public RFTXClient client;
    public RFTXServer server;
    //auth
    TokenAuthenticator authenticator=new TokenAuthenticator();
    //exception
    ExceptionListener exceptionListener;
    //listener
    TaskListener taskListener;
    public RFTXHost(String hostName){
        this.hostName=hostName;
    }
    public RFTXClient initClient(){
        this.client=new RFTXClient(this);
        return this.client;
    }
    public RFTXServer initServer(int port)throws Exception{
        this.server=new RFTXServer(this,port);
        return this.server;
    }
    public TokenAuthenticator getAuthenticator(){
        return authenticator;
    }
    public synchronized void post(String peerName,String taskToken,String localFile,String remoteFile)throws Exception{
        BasicInfo.indexControlConnByPeerName(controlConns, peerName).post(taskToken, localFile, remoteFile);
    }
    public synchronized void get(String peerName,String taskToken,String localFile,String remoteFile)throws Exception{
        BasicInfo.indexControlConnByPeerName(controlConns, peerName).get(taskToken, localFile, remoteFile);
    }
    public void setExceptionListener(ExceptionListener listener){
        this.exceptionListener=listener;
    }
    public ExceptionListener getExceptionListener(){
        return this.exceptionListener;
    }
    public void throwException(Exception e){
        if(getExceptionListener()!=null){
            getExceptionListener().exceptionThrown(e);
        }
    }
    public TaskListener getTaskListener(){
        return this.taskListener;
    }

    @Override
    public void start(FileTaskInfo info) {
        // TODO Auto-generated method stub
        if(this.taskListener!=null){
            taskListener.start(info);
        }
    }

    @Override
    public void finish(FileTaskInfo info) {
        // TODO Auto-generated method stub
        if(this.taskListener!=null){
            taskListener.finish(info);
        }
    }

    @Override
    public void interrupt(FileTaskInfo info) {
        // TODO Auto-generated method stub
        if(this.taskListener!=null){
            taskListener.interrupt(info);
        }
    }
}
