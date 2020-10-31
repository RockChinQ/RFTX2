package test;

import com.rftx.core.RFTXHost;
import com.rftx.util.Debugger;

public class ServerOnly {
    static RFTXHost serverHost=new RFTXHost("server");
    public static void main(String[] args)throws Exception {
        Debugger.debug=true;
        serverHost.initServer(3000);
        serverHost.getAuthenticator().getAuthTokenMap().put("client", "auth");
        serverHost.server.start();
    }
}
