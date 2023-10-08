package com.simtech.sim.cloudcompiler.config;

import com.alibaba.fastjson.JSON;
import com.simtech.sim.cloudcompiler.entity.kernel.WsResponse;
import com.simtech.sim.cloudcompiler.service.WebSocketConnectionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

@Slf4j
@Component
public class WebSocketConfig {

    private String result;
    private CountDownLatch latch = new CountDownLatch(1);

    @Getter
    private final CompletableFuture<String> messageReceivedSignal = new CompletableFuture<>();


    public CompletableFuture<String> getExecutionResultAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                latch.await(); // 等待CountDownLatch减为0
                return result;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });
    }



    public WebSocketClient createWebSocketClient(String id) {
        try {
            String websocketUrl = "ws://localhost:8888/api/kernels/" + id + "/channels";
            log.info("WebSocket URL: {}", websocketUrl);

            return new WebSocketClient(new URI(websocketUrl), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("[websocket] 连接成功");
                }

                @Override
                public void onMessage(String message) {

                    if (JSON.parseObject(message, WsResponse.class).getMsg_type().equals("stream")) {
                        log.info("[websocket] 收到消息={}", message);
                        result = message;
                        messageReceivedSignal.complete(message);
                        latch.countDown(); // 将CountDownLatch减1
                        latch = new CountDownLatch(1);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] 退出连接");
                }

                @Override
                public void onError(Exception ex) {
                    log.error("[websocket] 连接错误={}", ex.getMessage());
                }
            };
        } catch (Exception e) {
            log.error("WebSocket连接异常: {}", e.getMessage());
            return null;
        }
    }


}