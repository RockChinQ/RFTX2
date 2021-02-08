package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ChineseFileName {
    static RFTXHost host = new RFTXHost("selftrans");
    public static void main(String[] args)throws Exception {
        Debugger.debug = true;
        host.initServer(3001);
        host.getAuthenticator().addValidToken("test", "test");
        host.server.start();
        host.initClient();
        host.client.connect("127.0.0.1", 3001, "test");
        javax.swing.JOptionPane.showConfirmDialog(null,"start?");
        host.post("selftrans", "self", "F:\\视频\\命案追踪正式版.mp4", "F:\\mazz.mp4");
    }
    
}
