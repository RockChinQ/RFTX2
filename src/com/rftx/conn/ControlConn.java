package com.rftx.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.exception.PeerIdentityException;
import com.rftx.util.BasicInfo;
import com.rftx.util.Debugger;

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
    public void post(String taskToken,String localFile,String remoteFile)throws Exception{
        if(identity==CLIENT){
            var info=new FileTaskInfo();
            info.taskToken=taskToken;
            info.localPath=localFile;
            info.size=new File(localFile).length();
            info.remotePath=remoteFile;
            info.progress=0;
            var transportConn=new TransportConn(host, TransportConn.SENDER,info,socket.getInetAddress().getHostAddress(),socket.getPort());
            transportConn.launchByClient=true;
            host.transportConns.add(transportConn);
            //create conn for this
            transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
            transportConn.initRW();

            transportConn.getProxyThread().start();
            writeMsg("post "+taskToken+" "+localFile+" "+remoteFile+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
        }else if(identity==SERVER){
            writeMsg("post "+taskToken+" "+localFile+" "+remoteFile+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
        }
    }
    public void get(String taskToken,String localFile,String remoteFile)throws Exception{
        if(identity==CLIENT){
            var info=new FileTaskInfo();
            info.taskToken=taskToken;
            info.localPath=localFile;
            info.remotePath=remoteFile;
            info.progress=0;
            var transportConn=new TransportConn(host, TransportConn.RECEIVER,info,socket.getInetAddress().getHostAddress(),socket.getPort());
            transportConn.launchByClient=true;
            host.transportConns.add(transportConn);
            //create conn for this
            transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
            transportConn.initRW();
            transportConn.getProxyThread().start();
            writeMsg("get "+taskToken+" "+localFile+" "+remoteFile+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
        }else if(identity==SERVER){
            writeMsg("get "+taskToken+" "+localFile+" "+remoteFile+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
        }
    }
    @Override
    public void run() {
        Debugger.say("control conn start:"+(identity==SERVER?"SERVER":"CLIENT"));
        byte[] buffer=new byte[1024];
        try{
            if(identity==CLIENT){
                writer.writeInt(BasicInfo.CONNTYPE_CONTROL);
                host.getAuthenticator().send(this);
            }
            Debugger.say("send name:"+host.hostName);
            writeMsg("name "+host.hostName);
            String msg;
            while((msg=getReader().readUTF())!=null){
                String[] cmd=msg.split(" ");
                Debugger.say((identity==SERVER?"SERVER":"CLIENT")+" read msg:"+msg);
                switch(cmd[0]){
                    case "name":{
                        peerName=cmd[1];
                        Debugger.say("new name:"+peerName);
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
                            writeMsg("get "+info.taskToken+" "+info.remotePath+" "+info.localPath+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
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
                            writeMsg("post "+info.taskToken+" "+info.localPath+" "+info.remotePath+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
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
