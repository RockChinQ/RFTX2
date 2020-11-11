package com.rftx.core;

import java.net.Socket;

import com.rftx.conn.ControlConn;
import com.rftx.listener.ClientConnListener;


public class RFTXClient implements ClientConnListener{
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
    ClientConnListener connListener;
    public void setClientConnListener(ClientConnListener listener){
        this.connListener=listener;
    }
    public ClientConnListener getClientConnListener(){
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
