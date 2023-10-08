package com.simtech.sim.cloudcompiler.service.impl;

import com.simtech.sim.cloudcompiler.config.DownloadFileRequest;
import com.simtech.sim.cloudcompiler.config.DownloadFileResponse;
import com.simtech.sim.cloudcompiler.config.FileServiceGrpc;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class GrpcDownloadServiceImpl {


    @Resource
    FileServiceGrpc.FileServiceBlockingStub fileServiceBlockingStub;


    public String retrieveFile(String instanceId, String algType) {
        DownloadFileRequest request = DownloadFileRequest.newBuilder()
                .setInstanceId(instanceId)
                .setAlgorithmType(algType)
                .build();
        Iterator<DownloadFileResponse> helloResponse = fileServiceBlockingStub.downloadFile(request);

        return helloResponse.next().toString();
    }

}
