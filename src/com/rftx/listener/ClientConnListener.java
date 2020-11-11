package com.rftx.listener;

import com.rftx.conn.ControlConn;

public interface ClientConnListener {
    void connected(ControlConn conn);
    void authenticated(ControlConn conn);
    void disconnected(ControlConn conn);
}
