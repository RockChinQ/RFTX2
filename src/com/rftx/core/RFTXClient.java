package com.rftx.core;

import java.net.Socket;

import com.rftx.conn.ControlConn;

public class RFTXClient {
    private RFTXHost host;
    RFTXClient(RFTXHost host){
        this.host=host;
    }
    /**
     * connect a RFTX server
     */
    public void connect(String addr,int port)throws Exception{
        Socket clientSocket=new Socket(addr, port);
        ControlConn conn=new ControlConn(host,ControlConn.CLIENT);
        conn.setSocket(clientSocket);
        conn.initRW();
        host.controlConns.add(conn);
        conn.getProxyThread().start();
    }
}
