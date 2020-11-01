package com.rftx.core;

public class FileTaskInfo {
    public String taskToken;
    public String localPath;
    public long size;
    
    public String remoteOSCode;
    public String remotePath;

    public long progress;
    
    public static final int SENDER = 0, RECEIVER = 1;
    public int localHostIdentity=0;

    public String remoteAddr="";
    public int remotePort=0;

    public long launchTime=0;
}
