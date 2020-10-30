package com.rftx.auth;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.rftx.conn.AbstractConn;

public class TokenAuthenticator implements IAuthenticator {
    //host token,as client;
    String clientToken="";
    //tokens list,as server
    HashMap<String,String> authTokens=new HashMap<>();
    @Override
    public boolean auth(String msg)throws Exception{
        if(authTokens.containsKey(msg)){
            return true;
        }
        return false;
    }
    @Override
    public void send(AbstractConn conn) throws Exception {
        conn.getWriter().write(("!auth "+clientToken).getBytes(StandardCharsets.UTF_8));
        conn.getWriter().flush();
    }
}
