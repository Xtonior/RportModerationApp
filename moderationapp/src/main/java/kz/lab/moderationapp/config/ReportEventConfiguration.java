package kz.lab.moderationapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import kz.lab.moderationapp.model.ReportEvent;

@Configuration
public class ReportEventConfiguration {
    @Bean
    ReactiveRedisOperations<String, ReportEvent> redisOperations(ReactiveRedisConnectionFactory factory) {
        JacksonJsonRedisSerializer<ReportEvent> serializer = new JacksonJsonRedisSerializer<>(ReportEvent.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, ReportEvent> builder = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, ReportEvent> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
