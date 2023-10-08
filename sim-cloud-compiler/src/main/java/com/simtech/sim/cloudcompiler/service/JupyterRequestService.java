package com.simtech.sim.cloudcompiler.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface JupyterRequestService {
    String executeAlgorithmFile(String instance_id, String fileType, String data) throws InterruptedException, ExecutionException;
}
