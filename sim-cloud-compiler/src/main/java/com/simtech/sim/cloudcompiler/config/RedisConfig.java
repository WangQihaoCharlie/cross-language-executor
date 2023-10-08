package com.simtech.sim.cloudcompiler.config;

import com.simtech.sim.cloudcompiler.entity.dto.KernelDetail;
import com.simtech.sim.cloudcompiler.kernel.SimpleKernelConnector;
import com.simtech.sim.cloudcompiler.kernel.pool.WebSocketKernelPool;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PreDestroy;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class RedisConfig {



    // 配置 Redis 连接工厂

    private final BlockingQueue<Map.Entry<String, RedisConnection>> redisStreamPool = new LinkedBlockingQueue<>();


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration("192.168.1.47");
        return new LettuceConnectionFactory(redisConfiguration);
    }

    // 配置 RedisTemplate
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }


    public void createStreamPool(BlockingQueue<KernelDetail> queue){

        queue.forEach(obj -> {

            LettuceConnectionFactory lettuceConnectionFactory = this.redisConnectionFactory();
            RedisConnection connection = lettuceConnectionFactory.getConnection();

            Map<byte[], byte[]> message = new HashMap<>();

            message.put("0".getBytes(), String.format("Hello! Kernel id %s", obj.getId()).getBytes());
            connection.xAdd(obj.getId().getBytes(), message);

            redisStreamPool.add(new AbstractMap.SimpleEntry<>(obj.getId(), lettuceConnectionFactory.getConnection()));
        });
    }


    public Map.Entry<String, RedisConnection> getStream() {
        try {
            return redisStreamPool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unable to get pair from pool", e);
        }
    }

    public void releaseStream(Map.Entry<String, RedisConnection> streamName) {
        redisStreamPool.offer(streamName);
    }

    public void destroyStream(KernelDetail detail){
        this.redisConnectionFactory().getConnection().del(detail.getId().getBytes());
    }

}
