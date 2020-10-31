package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ExceptionTest {
    static RFTXHost client=new RFTXHost("client");
    public static void main(String[] args)throws Exception{
        Debugger.debug=true;
        client.setExceptionListener(new Listener());
        client.initClient();
        client.client.connect("192.168.1.4", 3000, "client");
    }
}
