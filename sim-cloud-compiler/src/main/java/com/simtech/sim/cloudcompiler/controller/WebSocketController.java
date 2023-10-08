package com.simtech.sim.cloudcompiler.controller;

import com.alibaba.fastjson.JSON;
import com.simtech.sim.cloudcompiler.entity.dto.AlgDetail;
import com.simtech.sim.cloudcompiler.entity.dto.KernelDetail;
import com.simtech.sim.cloudcompiler.entity.kernel.WsResponse;
import com.simtech.sim.cloudcompiler.kernel.pool.WebSocketKernelPool;
import com.simtech.sim.cloudcompiler.service.JupyterRequestService;
import com.simtech.sim.cloudcompiler.service.MQService;
import com.simtech.sim.cloudcompiler.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/compiler")
@Slf4j
@Component
public class WebSocketController {


    private final WebSocketService webSocketService;

    private final JupyterRequestService jupyterRequestService;

    private final RabbitTemplate rabbitTemplate;



    public WebSocketController(WebSocketService webSocketService, JupyterRequestService jupyterRequestService, RabbitTemplate rabbitTemplate) {
        this.webSocketService = webSocketService;
        this.jupyterRequestService = jupyterRequestService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/execute-single-line")
    public String singleLineExecutor(@RequestParam String code) throws InterruptedException, ExecutionException {
        AtomicReference<String> result = new AtomicReference<>();
        Thread singleLineThread = new Thread(()-> {
            try {
                result.set(webSocketService.sendWebSocketMessage(code));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        singleLineThread.start();
        singleLineThread.join();

        return result.get();
    }


    @PostMapping("/execute-file")
    public String algFileExecutor(@RequestParam String instanceId, @RequestParam String type, @RequestParam String data) throws ExecutionException, InterruptedException {
        return jupyterRequestService.executeAlgorithmFile(instanceId, type, data);
    }

    @RabbitListener(queues = "algSender", concurrency = "10")
    public void processRequest(byte[] body, @Header(AmqpHeaders.REPLY_TO) String replyTo,
                               @Header(AmqpHeaders.CORRELATION_ID) String correlation) throws ExecutionException, InterruptedException {
        String response = new String(body, StandardCharsets.UTF_8);
        AlgDetail detail = JSON.parseObject(response, AlgDetail.class);

        String raw_result = jupyterRequestService.executeAlgorithmFile(detail.getInstanceId(), detail.getType(), detail.getData());
        String result = JSON.parseObject(raw_result, WsResponse.class).getContent().getText();
        rabbitTemplate.convertAndSend(replyTo, result, message -> {
            message.getMessageProperties().setCorrelationId(correlation);
            return message;
        });
    }






}
