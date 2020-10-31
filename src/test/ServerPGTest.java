package test;

import javax.swing.JOptionPane;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ServerPGTest {
    static RFTXHost server=new RFTXHost("server");
    public static void main(String[] args)throws Exception{
        Debugger.debug=true;
        server.initServer(3000);
        server.server.addValidToken("testClient", "client");
        server.server.start();
        JOptionPane.showMessageDialog(null, "click button to continue", "wait", JOptionPane.INFORMATION_MESSAGE);
        // server.post("client", "post", "testFileServer\\wall.png", "testFileClient\\wall.png");
        server.get("client", "get", "testFileServer\\wall2.png", "testFileClient\\wall.png");
    }
}
