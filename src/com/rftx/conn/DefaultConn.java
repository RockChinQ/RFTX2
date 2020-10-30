package com.rftx.conn;

import java.net.Socket;

import com.rftx.core.RFTXHost;
import com.rftx.exception.PeerIdentityException;
import com.rftx.util.BasicInfo;

public class DefaultConn extends AbstractConn{
    public DefaultConn(RFTXHost host,Socket socket)throws Exception{
        this.host=host;
        this.socket=socket;
        initRW();
    }
    @Override
    public void run(){
        try{
            //read type
            int type=reader.readInt();
            switch(type){
                case BasicInfo.CONNTYPE_CONTROL:{
                    ControlConn controlConn=new ControlConn(host, ControlConn.SERVER);
                    controlConn.socket=this.socket;
                    controlConn.reader=this.reader;
                    controlConn.writer=this.writer;
                    host.controlConns.add(controlConn);
                    controlConn.getProxyThread().start();
                    break;
                }
                case BasicInfo.CONNTYPE_SERVER_SEND:{
                    TransportConn transportConn=new TransportConn(host,TransportConn.SENDER);
                    transportConn.socket=this.socket;
                    transportConn.reader=this.reader;
                    transportConn.writer=this.writer;
                    host.transportConns.add(transportConn);
                    transportConn.getProxyThread().start();
                    break;
                }
                case BasicInfo.CONNTYPE_SERVER_RECV:{
                    TransportConn transportConn=new TransportConn(host,TransportConn.RECEIVER);
                    transportConn.socket=this.socket;
                    transportConn.reader=this.reader;
                    transportConn.writer=this.writer;
                    host.transportConns.add(transportConn);
                    transportConn.getProxyThread().start();
                    break;
                }
                default:{
                    throw new PeerIdentityException("cannot identify the type of conn:"+type);
                }
            }
        }catch(Exception e){}
        host.defaultConns.remove(this);
    }

}
