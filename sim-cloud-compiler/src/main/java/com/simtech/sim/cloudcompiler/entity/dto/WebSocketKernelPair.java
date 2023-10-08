package com.simtech.sim.cloudcompiler.entity.dto;

import lombok.Data;
import org.java_websocket.client.WebSocketClient;

@Data
public class WebSocketKernelPair {

    private KernelDetail kernelDetail;

    private WebSocketClient webSocketClient;
}
