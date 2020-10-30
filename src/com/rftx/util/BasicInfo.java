package com.rftx.util;

import java.util.ArrayList;

import com.rftx.conn.AbstractConn;
import com.rftx.conn.TransportConn;

public class BasicInfo {
    public static final int CONNTYPE_CONTROL=0,CONNTYPE_SERVER_RECV=1,CONNTYPE_SERVER_SEND=2;
    public static String getOSName(){
        return System.getProperty("os.name");
    }
    public static TransportConn indexTransportConnByTaskToken(ArrayList<TransportConn> arrayList,String token){
        int len=arrayList.size();
        for(var index=0;index<len;index++){
            if(arrayList.get(index).info.taskToken.equals(token)){
                return arrayList.get(index);
            }
        }
        return null;
    }
}
