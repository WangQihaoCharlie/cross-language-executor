package com.sim.simtech.fileserver;

import com.sim.simtech.fileserver.config.GrpcService;
import com.sim.simtech.fileserver.config.ServiceManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
public class SimFileServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(SimFileServerApplication.class, args);
        Map<String, Object> grpcServiceBeanMap = configurableApplicationContext.getBeansWithAnnotation(GrpcService.class);
        ServiceManager serviceManager = configurableApplicationContext.getBean(ServiceManager.class);
        serviceManager.loadService(grpcServiceBeanMap);
    }

}
