package com.rftx.auth;

import java.util.HashMap;

public class TokenAuthenticator implements IAuthenticator {
    HashMap<String,String> authTokens=new HashMap<>();
    @Override
    public boolean auth(String msg)throws Exception{
        if(authTokens.containsKey(msg)){
            return true;
        }
        return false;
    }
}
