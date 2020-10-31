package com.rftx.exception;
/**
 * created when receive a file process exception from peer.
 */
public class PeerProcessException extends Exception{
    public PeerProcessException(String msg){
        super(msg);
    }
}
