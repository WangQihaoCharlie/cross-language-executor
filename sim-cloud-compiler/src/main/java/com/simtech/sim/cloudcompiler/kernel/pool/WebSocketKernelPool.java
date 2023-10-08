package com.simtech.sim.cloudcompiler.kernel.pool;

import com.simtech.sim.cloudcompiler.config.RedisConfig;
import com.simtech.sim.cloudcompiler.entity.dto.KernelDetail;
import com.simtech.sim.cloudcompiler.kernel.SimpleKernelConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class WebSocketKernelPool {

    private final SimpleKernelConnector simpleKernelConnector;

    private final RedisConfig redisConfig;


    private final BlockingQueue<KernelDetail> pairs = new LinkedBlockingQueue<>();

    public WebSocketKernelPool(SimpleKernelConnector simpleKernelConnector, RedisConfig redisConfig) throws Exception {
        this.simpleKernelConnector = simpleKernelConnector;
        this.redisConfig = redisConfig;
        int MAX_POOL_SIZE = 15;
        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            KernelDetail pair = createNewKernel();
            pairs.offer(pair);
        }
        this.redisConfig.createStreamPool(this.pairs);
    }

    public KernelDetail getKernel() {
        try {
            return pairs.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unable to get pair from pool", e);
        }
    }

    public void releaseKernel(KernelDetail pair) {
        pairs.offer(pair);
    }

    private KernelDetail createNewKernel() {
        KernelDetail kernelDetail = simpleKernelConnector.createKernel();
        log.info("Kernel Initialized {}", kernelDetail.getId());
        return kernelDetail;
    }

    @PreDestroy
    public void removeKernelAndStream(){
        log.info("Quitting... Destroying Kernel");
        log.info("Quitting... Destroying Redis Stream Object");

        for(KernelDetail kernelDetail : pairs){
            simpleKernelConnector.deleteKernel(kernelDetail.getId());
            redisConfig.destroyStream(kernelDetail);
        }
    }

}
