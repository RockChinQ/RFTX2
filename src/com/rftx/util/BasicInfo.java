package com.rftx.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.rftx.conn.ControlConn;
import com.rftx.conn.TransportConn;

public class BasicInfo {
    public static final int CONNTYPE_CONTROL=0,CONNTYPE_SERVER_RECV=1,CONNTYPE_SERVER_SEND=2;
    public static String getOSName(){
        return System.getProperty("os.name");
    }
    public static TransportConn indexTransportConnByTaskToken(ArrayList<TransportConn> arrayList,String token,int identity){
        int len=arrayList.size();
        for(int index=0;index<len;index++){
            if(arrayList.get(index).info.taskToken.equals(token)&&arrayList.get(index).identity==identity){
                return arrayList.get(index);
            }
        }
        return null;
    }
    public static ControlConn indexControlConnByPeerName(ArrayList<ControlConn> arrayList,String peerName){
        Debugger.say("Index control:"+peerName+" in "+arrayList.size());
        int len=arrayList.size();
        for(int i=0;i<len;i++){
            Debugger.say("is cconn:"+arrayList.get(i).peerName+"?");
            if(arrayList.get(i).peerName.equals(peerName)){
                Debugger.say("get cconn:"+arrayList.get(i).peerName+" identity:"+(arrayList.get(i).identity==0?"CLINET":"SERVER"));
                return arrayList.get(i);
            }
        }
        return null;
    }
    public static String getErrorInfo(Exception e){
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replaceAll("\t","    ");
    }
}
