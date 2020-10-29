package com.rftx.auth;

public interface IAuthenticator {
    boolean auth(String msg)throws Exception;
}
