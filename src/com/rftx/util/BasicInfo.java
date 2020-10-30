package com.rftx.util;

public class BasicInfo {
    public static final int CONNTYPE_CONTROL=0,CONNTYPE_SERVER_RECV=1,CONNTYPE_SERVER_SEND=2;
    public static String getOSName(){
        return System.getProperty("os.name");
    }
}
