package com.rftx.core;

import java.util.ArrayList;
import com.rftx.auth.TokenAuthenticator;
import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.conn.TransportConn;
import com.rftx.util.BasicInfo;

public class RFTXHost {
    public ArrayList<DefaultConn> defaultConns=new ArrayList<>();
    public ArrayList<ControlConn> controlConns=new ArrayList<>();
    public ArrayList<TransportConn> transportConns=new ArrayList<>();

    //basic info 
    public String hostName="";
    //Client and server
    public RFTXClient client;
    public RFTXServer server;
    //auth
    TokenAuthenticator authenticator=new TokenAuthenticator();
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
}
