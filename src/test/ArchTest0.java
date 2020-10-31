package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ArchTest0 {
    static RFTXHost host=new RFTXHost("TestServer");
    static RFTXHost host1=new RFTXHost("TestClient");
    public static void main(String[] args) throws Exception{
        Debugger.debug=true;
        host.initServer(3000);
        host.getAuthenticator().getAuthTokenMap().put("TestClient", "testClientToken");
        host.server.start();
        host1.initClient();
        host1.client.connect("localhost", 3000,"testClientToken");
        //send file
        Thread.sleep(2000);
        host.get("TestClient", "testFile", "transportTest.txt", "TransportTest2.txt");
    }
}
