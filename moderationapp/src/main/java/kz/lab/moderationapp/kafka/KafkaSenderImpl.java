package kz.lab.moderationapp.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import kz.lab.moderationapp.exception.KafkaException;

@Component
public class KafkaSenderImpl {
    private final KafkaTemplate<String, Object> template;

    public KafkaSenderImpl(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void send(final String topic, final String key, final Object data) throws KafkaException {
        CompletableFuture<SendResult<String, Object>> future = template.send(topic, key, data);

        try {
            future.whenComplete((result, ex) -> {
                System.out.println("Sended kafka message");
            });
        } catch (Exception e) {
            throw new KafkaException("Kafka failure", e);
        }
    }
}
