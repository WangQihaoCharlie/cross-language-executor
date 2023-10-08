package com.simtech.sim.cloudcompiler.service;
public interface WebSocketConnectionListener {
    void onConnected();
    void onMessageReceived(String message);
    void onError(Exception ex);
    void onDisconnected(int code, String reason, boolean remote);
}
