package com.simtech.sim.cloudcompiler.service.impl;

import com.simtech.sim.cloudcompiler.service.MQService;
import com.simtech.sim.simcommon.utils.AmqpUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan("com.simtech.sim.simcommon")
@AllArgsConstructor
public class MQServiceImpl implements MQService {

    private AmqpUtils amqpUtils;

    @Override
    public void notifyTarget(String message, String exchangeName, String queueName, String routingKey){
        amqpUtils.sendSingleWayMessage(message, exchangeName, queueName, routingKey);
    }
}
