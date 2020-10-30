package com.rftx.core;

import java.util.ArrayList;
import com.rftx.auth.TokenAuthenticator;
import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.conn.TransportConn;

public class RFTXHost {
    ArrayList<DefaultConn> defaultConns=new ArrayList<>();
    ArrayList<ControlConn> controlConns=new ArrayList<>();
    ArrayList<TransportConn> transportConns=new ArrayList<>();

    //basic info 
    String hostName="";
    //Client and server
    RFTXClient client;
    RFTXServer server;
    //auth
    TokenAuthenticator authenticator=new TokenAuthenticator();
    RFTXHost(String hostName){
        this.hostName=hostName;
    }
    public void initClient(){
        this.client=new RFTXClient(this);
    }
    public void initServer(){
        this.server=new RFTXServer(this);
    }
    public TokenAuthenticator getAuthenticator(){
        return authenticator;
    }
}
