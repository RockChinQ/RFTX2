package com.rftx.conn;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.util.BasicInfo;

public class ControlConn extends AbstractConn {
    boolean authed=false;
    String token="";
    int identity=-1;
    public static final int CLIENT=0,SERVER=1;
    public ControlConn(RFTXHost host,int identity){
        this.host=host;
        this.identity=identity;
    }
    @Override
    public void run() {
        byte[] buffer=new byte[1024];
        int len;
        try{
            writer.writeInt(0);
            host.getAuthenticator().send(this);
            readMsg:while((len=getReader().read(buffer,0,1024))!=-1){
                String msg=new String(buffer);
                String[] cmd=msg.split(" ");
                switch(cmd[0]){
                    //server specific
                    case "auth":{
                        token=cmd[1];
                        authed=host.getAuthenticator().auth(cmd[1]);
                        break;
                    }
                    //post <taskToken> <sendPath> <recvPath> <fromOSCode>
                    //get <taskToken> <sendPath> <recvPath> <fromOSCode>
                    case "post":{
                        FileTaskInfo info=new FileTaskInfo();
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
                        }else if(identity==SERVER){
                            
                        }
                        break;
                    }
                }
            }
        }catch(Exception e){}
    }
}
