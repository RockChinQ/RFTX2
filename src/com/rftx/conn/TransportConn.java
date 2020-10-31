package com.rftx.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.util.BasicInfo;
import com.rftx.util.Debugger;

public class TransportConn extends AbstractConn {
    public FileTaskInfo info = new FileTaskInfo();
    int identity = -1;
    public static final int SENDER = 0, RECEIVER = 1;
    private boolean readyToRun=false;
    public boolean launchByClient=false;
    ControlConn controlConnToNotify;
    public TransportConn(RFTXHost host, int identity, FileTaskInfo info,ControlConn controlConnToNotify) {
        this.host = host;
        this.identity = identity;
        this.info=info;
        this.controlConnToNotify=controlConnToNotify;
        readyToRun=true;
    }
    //use when server accepted and confirmed a transportConn
    //set info to UNREADY will make thread to wait
    public TransportConn(RFTXHost host, int identity) {
        this.host = host;
        this.identity = identity;
        readyToRun=false;
    }
    @Override
    public void run() {
        synchronized(this){
            if(launchByClient){
                try{
                    writer.writeInt(identity==RECEIVER?BasicInfo.CONNTYPE_SERVER_SEND:BasicInfo.CONNTYPE_SERVER_RECV);
                    Debugger.say("send taskToken:"+info.taskToken);
                    writeMsg(info.taskToken);
                    Debugger.say("wait for confirm");
                    String confirm=reader.readUTF();
                    Debugger.say("token confirmed:"+confirm);
                    synchronized(controlConnToNotify){
                        controlConnToNotify.notify();
                    }
                }catch(Exception e){
                    host.throwException(e);
                    host.transportConns.remove(this);
                    return;
                }
            }else{
                try{
                    String taskToken=reader.readUTF();
                    Debugger.say("recv taskToken0:"+taskToken);
                    info.taskToken=taskToken;
                    Debugger.say("sending confirm");
                    writeMsg("confirmToken\n");
                    Debugger.say("token confirmed");
                }catch(Exception e){
                    host.throwException(e);
                    host.transportConns.remove(this);
                    return;
                }
            }
        }
        if(identity==SENDER){
            try{
                synchronized(this){
                    if(!readyToRun)
                        wait();
                }
                writer.writeLong(new File(info.localPath).length());
                //打开localfile的输入流,向socket发送
                
                DataInputStream fileIn=new DataInputStream(new FileInputStream(new File(info.localPath)));
                byte[] buffer=new byte[1024];
                int len=0;
                while((len=fileIn.read(buffer, 0, 1024))!=-1){
                    writer.write(buffer, 0, len);
                    info.progress+=len;
                }
                fileIn.close();
                writer.close();
            }catch(Exception e){
                host.throwException(e);
            }
        }else if(identity==RECEIVER){
            try{
                synchronized(this){
                    if(!readyToRun)
                        wait();
                }
                info.size=reader.readLong();
                //open file
                DataOutputStream fileOut=new DataOutputStream(new FileOutputStream(new File(info.localPath)));
                //loop read and write
                byte[] buffer=new byte[1024];
                int len=0;
                while((len=reader.read(buffer, 0, 1024))!=-1){
                    fileOut.write(buffer, 0, len);
                    info.progress+=len;
                }
                //close
                fileOut.close();
                this.reader.close();
            }catch(Exception e){
                host.throwException(e);
                try{
                    controlConnToNotify.writeMsg("err "+BasicInfo.getErrorInfo(e));
                }catch(Exception e0){}
            }
        }
        host.transportConns.remove(this);
    }
}
