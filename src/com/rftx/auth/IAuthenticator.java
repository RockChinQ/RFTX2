package com.rftx.auth;

import com.rftx.conn.AbstractConn;

public interface IAuthenticator {
    boolean auth(String msg)throws Exception;
    void send(AbstractConn conn)throws Exception;
}
