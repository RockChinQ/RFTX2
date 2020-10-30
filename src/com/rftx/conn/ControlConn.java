package com.rftx.conn;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.exception.PeerIdentityException;
import com.rftx.util.BasicInfo;

public class ControlConn extends AbstractConn {
    boolean authed=false;
    String token="";
    String peerName;
    int identity=-1;
    public static final int CLIENT=0,SERVER=1;
    public ControlConn(RFTXHost host,int identity){
        this.host=host;
        this.identity=identity;
    }
    public void post(String taskToken,String localFile,String remoteFile){
        
    }
    @Override
    public void run() {
        byte[] buffer=new byte[1024];
        try{
            if(identity==CLIENT){
                writer.writeInt(BasicInfo.CONNTYPE_CONTROL);
                host.getAuthenticator().send(this);
            }
            writeMsg("name "+host.hostName);
            while(getReader().read(buffer,0,1024)!=-1){
                String msg=new String(buffer);
                String[] cmd=msg.split(" ");
                switch(cmd[0]){
                    case "name":{
                        peerName=cmd[1];
                        break;
                    }
                    //server specific
                    case "auth":{
                        if(identity==SERVER){
                            token=cmd[1];
                            authed=host.getAuthenticator().auth(cmd[1]);
                        }else if(identity==CLIENT){
                            throw new PeerIdentityException("illegal auth msg from server");
                        }
                        break;
                    }
                    //client specific
                    case "authSuc":{
                        if(identity==CLIENT)
                            authed=true;
                        else if(identity==SERVER)
                            throw new PeerIdentityException("illegal authSuc msg from a client:client ip:"+socket.getInetAddress());
                        break;
                    }
                    //post <taskToken> <sendPath> <recvPath> <fromOSCode>
                    //get <taskToken> <sendPath> <recvPath> <fromOSCode>
                    case "post":{
                        var info = new FileTaskInfo();
                        info.taskToken=cmd[1];
                        info.localPath=cmd[3];
                        info.remotePath=cmd[2];
                        info.remoteOSCode=cmd[4];
                        if(identity==CLIENT){
                            //send GET
                            TransportConn transportConn=new TransportConn(host,TransportConn.RECEIVER,info,socket.getInetAddress().getHostAddress(),socket.getPort());
                            // transportConn.writer.writeInt(BasicInfo.CONNTYPE_SERVER_SEND);
                            transportConn.getProxyThread().start();
                            writeMsg("get "+info.taskToken+" "+info.remotePath+" "+info.localPath+" "+BasicInfo.getOSName().replace(" ", "?"));
                        }else if(identity==SERVER){
                            //index TransportConn and set file info
                            //server is a recveiver here!!!!
                            synchronized(host.transportConns){
                                var transportConn=BasicInfo.indexTransportConnByTaskToken(host.transportConns, info.taskToken);
                                transportConn.info=info;
                                synchronized(transportConn){
                                    transportConn.notify();
                                }
                            }
                        }
                        break;
                    }
                    case "get":{
                        FileTaskInfo info=new FileTaskInfo();
                        info.taskToken=cmd[1];
                        info.localPath=cmd[2];
                        info.remotePath=cmd[3];
                        info.remoteOSCode=cmd[4];
                        if(identity==CLIENT){
                            TransportConn transportConn=new TransportConn(host,TransportConn.SENDER,info,socket.getInetAddress().getHostAddress(),socket.getPort());
                            transportConn.getProxyThread().start();
                            //send POST
                            writeMsg("post "+info.taskToken+" "+info.localPath+" "+info.remotePath+" "+BasicInfo.getOSName().replace(" ", "?"));
                        }else if(identity==SERVER){
                            //server is a sender here!!!!!
                            synchronized(host.transportConns){
                                var transportConn=BasicInfo.indexTransportConnByTaskToken(host.transportConns, info.taskToken);
                                transportConn.info=info;
                                synchronized(transportConn){
                                    transportConn.notify();
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }catch(Exception e){
            host.controlConns.remove(this);
        }
    }
}
