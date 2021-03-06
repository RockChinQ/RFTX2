package com.rftx.conn;

import java.io.File;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.exception.AuthenticateException;
import com.rftx.exception.PeerIdentityException;
import com.rftx.exception.PeerProcessException;
import com.rftx.util.BasicInfo;
import com.rftx.util.Debugger;

public class ControlConn extends AbstractConn {
    boolean authed=false;
    public String token="";
    public String peerName="";
    public int identity=-1;
    public static final int CLIENT=0,SERVER=1;
    public ControlConn(RFTXHost host,int identity){
        this.host=host;
        this.identity=identity;
    }
    public void post(String taskToken,String localFile,String remoteFile)throws Exception{
        synchronized(this){
            if(identity==CLIENT){
                FileTaskInfo info=new FileTaskInfo();
                info.taskToken=taskToken;
                info.localPath=localFile;
                info.size=new File(localFile).length();
                info.remotePath=remoteFile;
                info.progress=0;
                TransportConn transportConn=new TransportConn(host, TransportConn.SENDER,info,this);
                transportConn.launchByClient=true;
                host.transportConns.add(transportConn);
                //create conn for this
                transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
                transportConn.initRW();

                transportConn.getProxyThread().start();
                synchronized(this){
                    this.wait();
                }
                Debugger.say("Clinet SEND post msg at actively call");
                writeMsg("post "+taskToken+" "+localFile.replaceAll(" ", "?")+" "+remoteFile.replaceAll(" ", "?")+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
            }else if(identity==SERVER){
                writeMsg("post "+taskToken+" "+localFile.replaceAll(" ", "?")+" "+remoteFile.replaceAll(" ", "?")+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
            }
        } 
    }
    public void get(String taskToken,String localFile,String remoteFile)throws Exception{
        synchronized(this){
            if(identity==CLIENT){
                FileTaskInfo info=new FileTaskInfo();
                info.taskToken=taskToken;
                info.localPath=localFile;
                info.remotePath=remoteFile;
                info.progress=0;
                TransportConn transportConn=new TransportConn(host, TransportConn.RECEIVER,info,this);
                transportConn.launchByClient=true;
                host.transportConns.add(transportConn);
                //create conn for this
                transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
                transportConn.initRW();
                transportConn.getProxyThread().start();
                synchronized(this){
                    this.wait();
                }
                Debugger.say("Client SEND get msg at actively call");
                writeMsg("get "+taskToken+" "+remoteFile.replaceAll(" ", "?")+" "+localFile.replaceAll(" ", "?")+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
            }else if(identity==SERVER){
                writeMsg("get "+taskToken+" "+remoteFile.replaceAll(" ", "?")+" "+localFile.replaceAll(" ", "?")+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
            }
        }
    }
    @Override
    public void run() {
        Debugger.say("control conn start:"+(identity==SERVER?"SERVER":"CLIENT"));
        if(identity==SERVER){
            host.server.connected(this);
        }else if(identity==CLIENT){
            host.client.connected(this);
        }
        this.getProxyThread().setName("cc-"+(identity==SERVER?"SERVER":"CLIENT"));
        try{
            synchronized(this){
                if(identity==CLIENT){
                    writer.writeInt(BasicInfo.CONNTYPE_CONTROL);
                    host.getAuthenticator().send(this);
                }
                Debugger.say("send name:"+host.hostName);
                if(identity==SERVER)
                    writeMsg("name "+host.hostName);
            }
            // String msg;
            byte[] msg=new byte[2048];
            int len=0;
            while((len=getReader().read(msg)) != -1) {
                String strmsg=new String(msg, StandardCharsets.UTF_8);
                String[] cmd=strmsg.replaceAll(String.valueOf('\u0000'),"").split(" ");
                Debugger.say((identity==SERVER?"SERVER":"CLIENT")+" read msg:"+strmsg);
                switch(cmd[0]){
                    case "name":{
                        peerName=cmd[1];
                        Debugger.say((identity==SERVER?"SERVER":"CLIENT")+" peer's new name:"+peerName);
                        if(identity==CLIENT){
                            writeMsg("name "+host.hostName);
                        }
                        break;
                    }
                    //server specific
                    case "auth":{
                        if(identity==SERVER){
                            token=cmd[1];
                            authed=host.getAuthenticator().auth(cmd[1].replaceAll("\u0000", ""));
                            Debugger.say("auth result:"+authed+" "+host.getAuthenticator().getAuthTokenMap().get("testClient"));
                            if(!authed){
                                writeMsg("authFai");
                                throw new AuthenticateException("invalid auth token:"+cmd[1]+" from:"+socket.getInetAddress());
                            }else{
                                writeMsg("authSuc");
                                host.server.authenticated(this);
                            }
                        }else if(identity==CLIENT){
                            throw new PeerIdentityException("illegal auth msg from server");
                        }
                        break;
                    }
                    //client specific
                    case "authSuc":{
                        if(identity==CLIENT){
                            authed=true;
                            host.client.authenticated(this);
                        }else if(identity==SERVER)
                            throw new PeerIdentityException("illegal authSuc msg from a client:client ip:"+socket.getInetAddress());
                        break;
                    }
                    case "authFai":{
                        if(identity==CLIENT){
                            throw new AuthenticateException("auth failed,token:"+host.getAuthenticator().clientToken);
                        }else if (identity==SERVER){
                            throw new PeerIdentityException("illegal authFai msg from a client:client ip:"+socket.getInetAddress());
                        }
                        break;
                    }
                    //post <taskToken> <sendPath> <recvPath> <fromOSCode>
                    //get <taskToken> <sendPath> <recvPath> <fromOSCode>
                    case "post":{
                        if(!authed){
                            break;
                        }
                        FileTaskInfo info = new FileTaskInfo();
                        info.taskToken=cmd[1];
                        info.localPath=cmd[3];
                        info.remotePath=cmd[2];
                        info.remoteOSCode=cmd[4];
                        if(identity==CLIENT){
                            //send GET
                            TransportConn transportConn=new TransportConn(host,TransportConn.RECEIVER,info,this);
                            transportConn.launchByClient=true;
                            host.transportConns.add(transportConn);
                            //create conn for this
                            transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
                            transportConn.initRW();
                            // transportConn.writer.writeInt(BasicInfo.CONNTYPE_SERVER_SEND);
                            transportConn.getProxyThread().start();
                            synchronized(this){
                                this.wait();
                            }
                            Debugger.say("SEND get msg");
                            writeMsg("get "+info.taskToken+" "+info.remotePath+" "+info.localPath+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
                        }else if(identity==SERVER){
                            //index TransportConn and set file info
                            //server is a recveiver here!!!!
                            synchronized(host.transportConns){
                                TransportConn transportConn=BasicInfo.indexTransportConnByTaskToken(host.transportConns, info.taskToken,TransportConn.RECEIVER);
                                transportConn.info=info;
                                transportConn.controlConnToNotify=this;
                                synchronized(transportConn){
                                    transportConn.notify();
                                }
                                Debugger.say("Server start to recv");
                            }
                        }
                        break;
                    }
                    case "get":{
                        if(!authed){
                            break;
                        }
                        FileTaskInfo info=new FileTaskInfo();
                        info.taskToken=cmd[1];
                        info.localPath=cmd[2];
                        info.remotePath=cmd[3];
                        info.remoteOSCode=cmd[4];
                        if(identity==CLIENT){
                            TransportConn transportConn=new TransportConn(host,TransportConn.SENDER,info,this);
                            transportConn.launchByClient=true;
                            host.transportConns.add(transportConn);
                            //create conn for this
                            transportConn.socket=new Socket(this.socket.getInetAddress().getHostAddress(),this.socket.getPort());
                            transportConn.initRW();
                            transportConn.getProxyThread().start();
                            synchronized(this){
                                this.wait();
                            }
                            //send POST
                            Debugger.say("SEND post msg");
                            writeMsg("post "+info.taskToken+" "+info.localPath+" "+info.remotePath+" "+BasicInfo.getOSName().replaceAll(" ", "?"));
                        }else if(identity==SERVER){
                            //server is a sender here!!!!!
                            synchronized(host.transportConns){
                                TransportConn transportConn=BasicInfo.indexTransportConnByTaskToken(host.transportConns, info.taskToken,TransportConn.SENDER);
                                transportConn.info=info;
                                transportConn.controlConnToNotify=this;
                                synchronized(transportConn){
                                    transportConn.notify();
                                }
                            }
                        }
                        break;
                    }
                    case "err":{
                        host.throwException(new PeerProcessException(strmsg.substring(4)));
                        break;
                    }
                }
                msg=new byte[2048];
            }
        }catch(Exception e){
            host.throwException(e);
            host.controlConns.remove(this);
        }
        try{
            socket.close();
        }catch(Exception e){

        }
        if(identity==SERVER){
            host.server.disconnected(this);
        }else if(identity==CLIENT){
            host.client.disconnected(this);
        }
        Debugger.say("conn reset:"+peerName);
    }
}
