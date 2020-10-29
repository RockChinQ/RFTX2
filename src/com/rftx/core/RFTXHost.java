package com.rftx.core;

import java.util.ArrayList;

import com.rftx.conn.ControlConn;
import com.rftx.conn.DefaultConn;
import com.rftx.conn.TransportConn;

public class RFTXHost {
    ArrayList<DefaultConn> defaultConns=new ArrayList<>();
    ArrayList<ControlConn> controlConns=new ArrayList<>();
    ArrayList<TransportConn> transportConns=new ArrayList<>();

    //basic info 
    String hostName="";
    RFTXHost(String hostName){
        this.hostName=hostName;
    }
}
