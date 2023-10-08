package com.simtech.sim.cloudcompiler.service;

import org.java_websocket.client.WebSocketClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface WebSocketService {
    String sendWebSocketMessage(String code) throws InterruptedException, ExecutionException;

    CompletableFuture<String> processMessage();
}
