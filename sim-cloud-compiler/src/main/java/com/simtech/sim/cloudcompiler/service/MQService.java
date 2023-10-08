package com.simtech.sim.cloudcompiler.service;

public interface MQService {
    void notifyTarget(String message, String exchangeName, String queueName, String routingKey);
}
