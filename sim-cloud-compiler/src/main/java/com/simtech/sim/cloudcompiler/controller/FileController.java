package com.simtech.sim.cloudcompiler.controller;

import com.simtech.sim.cloudcompiler.config.DownloadFileRequest;
import com.simtech.sim.cloudcompiler.config.DownloadFileResponse;
import com.simtech.sim.cloudcompiler.config.FileServiceGrpc;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    FileServiceGrpc.FileServiceBlockingStub fileServiceBlockingStub;

    @SneakyThrows
    @RequestMapping("/hello")
    public String hello() {
        DownloadFileRequest request = DownloadFileRequest.newBuilder()
                .setInstanceId("hello")
                .setAlgorithmType("researchset")
                .build();
        Iterator<DownloadFileResponse> helloResponse = fileServiceBlockingStub.downloadFile(request);
        System.out.println(helloResponse.next());
        return "success";
    }

}
