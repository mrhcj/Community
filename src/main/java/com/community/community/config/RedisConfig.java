package com.community.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //key序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //value序列化方式
        template.setKeySerializer(RedisSerializer.json());
        //hash key序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //hash value序列化方式
        template.setKeySerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
