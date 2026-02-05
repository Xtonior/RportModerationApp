package kz.lab.moderationapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kz.lab.moderationapp.exception.KafkaException;
import kz.lab.moderationapp.kafka.KafkaSenderImpl;
import kz.lab.moderationapp.model.ReportEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaService {
    @Autowired
    private final KafkaSenderImpl kafkaSender;

    @Value("${kafka.topics.topic-2}")
    private String topic2;

    public void sendTopic2(ReportEvent event) throws KafkaException {
        try {
            kafkaSender.send(topic2, event.getEventId().toString(), event);
        } catch (KafkaException e) {
            throw new KafkaException("KafkaService: Failed to send event", e);
        }
    }
}
