package com.simtech.sim.cloudcompiler.service.impl;

import com.alibaba.fastjson.JSON;
import com.simtech.sim.cloudcompiler.config.RedisConfig;
import com.simtech.sim.cloudcompiler.config.WebSocketConfig;
import com.simtech.sim.cloudcompiler.entity.temp.Content;
import com.simtech.sim.cloudcompiler.service.JupyterRequestService;
import com.simtech.sim.cloudcompiler.service.WebSocketService;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class JupyterRequestServiceImpl implements JupyterRequestService {

    private final GrpcDownloadServiceImpl grpcDownloadService;

    private final WebSocketService webSocketService;

    private final RedisConfig redisConfig;

    public JupyterRequestServiceImpl(GrpcDownloadServiceImpl grpcDownloadService, WebSocketService webSocketService, RedisConfig redisConfig) {
        this.grpcDownloadService = grpcDownloadService;
        this.webSocketService = webSocketService;
        this.redisConfig = redisConfig;
    }

    @Override
    public String executeAlgorithmFile(String instance_id, String fileType, String data) throws InterruptedException, ExecutionException {

        String code = "{" + grpcDownloadService.retrieveFile(instance_id, fileType) + "}";
        Map.Entry<String, RedisConnection> pair = redisConfig.getStream();
        RedisConnection connection = pair.getValue();
        Map<byte[], byte[]> messager = new HashMap<>();

        messager.put(java.util.UUID.randomUUID().toString().getBytes(), data.getBytes());
        connection.xAdd(pair.getKey().getBytes(), messager);

        code = JSON.parseObject(code, Content.class).toString();

        redisConfig.releaseStream(pair);

        return webSocketService.sendWebSocketMessage(code);

    }
}
