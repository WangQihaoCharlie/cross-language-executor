package com.simtech.sim.cloudcompiler.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.simtech.sim.cloudcompiler.config.WebSocketConfig;
import com.simtech.sim.cloudcompiler.entity.dto.KernelDetail;
import com.simtech.sim.cloudcompiler.entity.kernel.WsResponse;
import com.simtech.sim.cloudcompiler.kernel.pool.WebSocketKernelPool;
import com.simtech.sim.cloudcompiler.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {
    private final WebSocketKernelPool webSocketKernelPool;
    private final WebSocketConfig webSocketConfig;



    public WebSocketServiceImpl(WebSocketConfig webSocketConfig, WebSocketKernelPool webSocketKernelPool){
        this.webSocketConfig = webSocketConfig;
        this.webSocketKernelPool = webSocketKernelPool;
    }

    public String sendWebSocketMessage(String code) throws InterruptedException, ExecutionException {


        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

        KernelDetail kernel = webSocketKernelPool.getKernel();

        WebSocketClient webSocketClient = webSocketConfig.createWebSocketClient(kernel.getId());

        Thread thread = new Thread(webSocketClient::connect);

        thread.start();
        thread.join();

        Thread.sleep(10);

        Thread sendingThread = new Thread(() -> {

            int maxRetries = 5;
            int baseWaitTimeMs = 5;

            int retries = 0;
            boolean messageSent = false;
            while (retries < maxRetries && !messageSent) {
                if (webSocketClient.isOpen()) {
                    webSocketClient.send(JSON.toJSONString(new WsResponse().getDefaultWsRequest(code), serializeConfig));
                    messageSent = true;
                } else {
                    log.error("WebSocketClient未连接，重试中..");
                    retries++;
                    int waitTime = (int) Math.pow(2, retries) * baseWaitTimeMs; // 指数退避算法
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!messageSent) {
                log.error("消息发送失败");
            }


        });

        sendingThread.start();

        CompletableFuture<String> stringCompletableFuture = webSocketConfig.getExecutionResultAsync();
        String result = stringCompletableFuture.get();


        webSocketClient.close();
        webSocketKernelPool.releaseKernel(kernel);


        return result;

    }

    @Override
    public CompletableFuture<String> processMessage() {
        return null;
    }

}
