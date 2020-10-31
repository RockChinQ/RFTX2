package com.rftx.exception;
/**
 * this will be created when peer is a RFTX host but sent invalid token to auth.
 */
public class AuthenticateException extends Exception{
    public AuthenticateException(String msg){
        super(msg);
    }
}
