package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class SelfTransferTest0 {
    public static RFTXHost host=new RFTXHost("selftrans");
    public static void main(String[] args) throws Exception{
        Debugger.debug=true;
        host.initServer(3000);
        host.getAuthenticator().addValidToken("test", "test");
        host.server.start();
        host.initClient();
        host.client.connect("127.0.0.1", 3000, "test");
        javax.swing.JOptionPane.showConfirmDialog(null,"start?");
        host.post("selftrans", "self", "testfile\\send\\selfSend.txt", "testfile\\recv\\selfrecv.txt");
    }
}
