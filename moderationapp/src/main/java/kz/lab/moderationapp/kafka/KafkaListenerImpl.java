package kz.lab.moderationapp.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import kz.lab.moderationapp.controller.ModerationController;
import kz.lab.moderationapp.exception.KafkaException;
import kz.lab.moderationapp.model.ReportEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaListenerImpl {
    @Autowired
    ModerationController moderationController;

    @KafkaListener(topics = "${kafka.topics.topic-1}", containerFactory = "kafkaListenerContainerFactory", groupId = "topic-1-listener")
    public void ListenTopic1(ConsumerRecord<String, ReportEvent> record) throws KafkaException {
        log.info("topic-1 got a message");
        ReportEvent event = record.value();

        if (event.getEventId() == null || event.getClientId() == null) {
            throw new KafkaException("Failed to parse kafka message");
        }

        moderationController.process(event);
    }

    @KafkaListener(topics = "${kafka.topics.topic-2}", containerFactory = "kafkaListenerContainerFactory", groupId = "topic-2-listener")
    public void ListenTopic2(ConsumerRecord<String, ReportEvent> record) throws KafkaException {
        log.info("topic-2 got a message");
        ReportEvent event = record.value();

        if (event.getEventId() == null || event.getClientId() == null) {
            throw new KafkaException("Failed to parse kafka message");
        }

        log.info("Topic 2: \n");
        log.info("Event ID: {}\n", event.getEventId());
        log.info("Event client: {}\n", event.getClientId());
        log.info("Event category: {}\n", event.getCategory());
        log.info("Event text: {}\n", event.getText());
    }
}
