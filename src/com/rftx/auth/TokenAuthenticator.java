package com.rftx.auth;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.rftx.conn.AbstractConn;

public class TokenAuthenticator implements IAuthenticator {
    //host token,as client;
    public String clientToken="";
    //tokens list,as server
    HashMap<String,String> authTokens=new HashMap<>();

    /**
     * set conn token 
     * conn token will be send when RFTXClient conn a RFTXServer
     * @param connToken
     */
    public void setConnToken(String connToken){
        this.clientToken=connToken;
    }
    /**
     * get conn token
     * @return
     */
    public String getConnToken(){
        return this.clientToken;
    }
    /**
     * add a valid token
     */
    public void addValidToken(String name,String token){
        this.authTokens.put(name, token);
    }
    /**
     * remove a valid token by name
     * @param name
     */
    public void removeValidToken(String name){
        this.authTokens.remove(name);
    }
    public HashMap<String,String> getAuthTokenMap(){
        return authTokens;
    }
    @Override
    public boolean auth(String msg)throws Exception{
        if(authTokens.containsValue(msg)){
            return true;
        }
        return false;
    }
    @Override
    public void send(AbstractConn conn) throws Exception {
        conn.writeMsg("auth "+clientToken);
    }
}
