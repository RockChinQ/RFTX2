package com.rftx.exception;
/**
 * this exception will be created when RFTX find that peer is not a RFTXHost
 * is you cannot confirm the peer,you host may be under hacker attack.
 */
public class PeerIdentityException extends Exception{
    public PeerIdentityException(String msg){
        super(msg);
    }
}
