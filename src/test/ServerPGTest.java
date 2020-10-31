package test;

import javax.swing.JOptionPane;

import com.rftx.conn.TransportConn;
import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ServerPGTest {
    static RFTXHost server = new RFTXHost("server");

    public static void main(String[] args) throws Exception {
        Debugger.debug = true;
        server.initServer(3000);
        server.server.addValidToken("testClient", "client");
        server.server.start();
        JOptionPane.showMessageDialog(null, "click button to continue", "wait", JOptionPane.INFORMATION_MESSAGE);
        // server.post("client", "post", "testFileServer\\wall.png", "testFileClient\\wall.png");
        server.post("client", "post", "test File Server\\1.14.4.rar", "test File Client\\1.14.4-2.rar");
    }
}
