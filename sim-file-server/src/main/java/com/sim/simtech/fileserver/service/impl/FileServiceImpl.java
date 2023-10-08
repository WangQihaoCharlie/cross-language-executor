package com.sim.simtech.fileserver.service.impl;


import com.google.protobuf.ByteString;
import com.sim.simtech.fileserver.config.GrpcService;
import com.sim.simtech.fileserver.service.FileServiceGrpc;
import com.sim.simtech.fileserver.service.FileService;

import com.sim.simtech.fileserver.util.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@GrpcService
public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase implements FileService {

    @Autowired
    private MinioUtils minioUtils;

    @Override
    public void downloadFile(com.sim.simtech.fileserver.service.DownloadFileRequest request,
                             io.grpc.stub.StreamObserver<com.sim.simtech.fileserver.service.DownloadFileResponse> responseObserver) {
        InputStream fileContent = minioUtils.getObject(request.getAlgorithmType(), request.getInstanceId());
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                responseObserver.onNext(com.sim.simtech.fileserver.service.DownloadFileResponse.newBuilder()
                        .setContent(ByteString.copyFrom(buffer, 0, bytesRead))
                        .build());
            }
        }
        catch (IOException exception){
            System.out.println("No such File");
        }
        responseObserver.onCompleted();
    }

}
