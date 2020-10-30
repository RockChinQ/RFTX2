package com.rftx.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

import com.rftx.core.FileTaskInfo;
import com.rftx.core.RFTXHost;
import com.rftx.util.BasicInfo;

public class TransportConn extends AbstractConn {
    public FileTaskInfo info = new FileTaskInfo();
    int identity = -1;
    public static final int SENDER = 0, RECEIVER = 1;
    private String addr;
    private int port;
    private boolean readyToRun=false;
    public TransportConn(RFTXHost host, int identity, FileTaskInfo info, String addr, int port) {
        this.host = host;
        this.identity = identity;
        this.addr = addr;
        this.port = port;
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
        //如果socket是null，则一定是由client发起连接
        if(getSocket()==null){
            try{
                socket=new Socket(addr,port);
                writer=new DataOutputStream(socket.getOutputStream());
                reader=new DataInputStream(socket.getInputStream());
                writer.writeInt(identity==RECEIVER?BasicInfo.CONNTYPE_SERVER_SEND:BasicInfo.CONNTYPE_SERVER_RECV);
                writer.writeUTF(info.taskToken);
            }catch(Exception e){e.printStackTrace();}
        }else{
            try{
                String taskToken=reader.readUTF();
                this.info.taskToken=taskToken;
            }catch(Exception e){e.printStackTrace();}
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
            }catch(Exception e){e.printStackTrace();}
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
            }catch(Exception e){e.printStackTrace();}
        }
        host.transportConns.remove(this);
    }
}
