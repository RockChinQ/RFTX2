package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ClientOnly {
    static RFTXHost clientHost=new RFTXHost("client");
    public static void main(String[] args) throws Exception{
        Debugger.debug=true;
        clientHost.initClient();
        clientHost.client.connect("192.168.1.4", 3000, "auth");
        Thread.sleep(2000);
        clientHost.get("server","task0","getFile.txt", "transportTest.txt");
        clientHost.post("server","task1","getFile.txt", "transportTest0.txt");
    }
    
}
