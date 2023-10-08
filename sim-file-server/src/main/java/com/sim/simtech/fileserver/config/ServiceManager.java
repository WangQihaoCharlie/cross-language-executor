package com.sim.simtech.fileserver.config;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class ServiceManager {
    private Server server;
    private final int grpcServerPort = 50051;

    private final Logger logger = log;

    public void loadService(Map<String, Object> grpcServiceBeanMap) throws IOException, InterruptedException {
        ServerBuilder serverBuilder = ServerBuilder.forPort(grpcServerPort);
        // 采用注解扫描方式，添加服务
        for (Object bean : grpcServiceBeanMap.values()) {
            serverBuilder.addService((BindableService) bean);
            log.info(bean.getClass().getSimpleName() + " is regist in Spring Boot");
        }
        server = serverBuilder.build().start();

        log.info("grpc server is started at " + grpcServerPort);

        // 增加一个钩子，当JVM进程退出时，Server 关闭
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("*** shutting down gRPC server since JVM is shutting down");
                if (server != null) {
                    server.shutdown();
                }
                log.info("*** server shut down！！！！");
            }
        });
        server.awaitTermination();
    }
}