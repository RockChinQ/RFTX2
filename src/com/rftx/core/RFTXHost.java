package com.rftx.core;

import java.util.ArrayList;
import com.rftx.auth.TokenAuthenticator;
import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.conn.TransportConn;

public class RFTXHost {
    public ArrayList<DefaultConn> defaultConns=new ArrayList<>();
    public ArrayList<ControlConn> controlConns=new ArrayList<>();
    public ArrayList<TransportConn> transportConns=new ArrayList<>();

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
