package com.simtech.sim.cloudcompiler.config;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServiceConfig {

    @Bean
    public ManagedChannel getChannel() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        return channel;
    }

    @Bean
    public FileServiceGrpc.FileServiceBlockingStub getStub1(ManagedChannel channel) {
        return FileServiceGrpc.newBlockingStub(channel);
    }

}