package com.rftx.core;

import java.net.Socket;

import com.rftx.conn.ControlConn;

public class RFTXClient {
    private RFTXHost host;
    RFTXClient(RFTXHost host){
        this.host=host;
    }
    public void setAuthToken(String token){
        host.authenticator.clientToken=token;
    }
    /**
     * connect a RFTX server
     */
    public void connect(String addr,int port,String useToken)throws Exception{
        Socket clientSocket=new Socket(addr, port);
        ControlConn conn=new ControlConn(host,ControlConn.CLIENT);
        host.getAuthenticator().clientToken=useToken;
        conn.setSocket(clientSocket);
        conn.initRW();
        host.controlConns.add(conn);
        conn.getProxyThread().start();
    }
}
