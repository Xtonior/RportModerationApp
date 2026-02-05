package kz.lab.moderationapp.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import kz.lab.moderationapp.exception.KafkaException;
import kz.lab.moderationapp.model.ReportEvent;
import kz.lab.moderationapp.service.ModerationService;
import kz.lab.moderationapp.service.UserDataFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaListenerImpl {
    @Autowired
    UserDataFetcherService userDataFetcherService;

    @Autowired
    ModerationService moderationService;

    @KafkaListener(topics = "${kafka.topics.topic-1}", containerFactory = "kafkaListenerContainerFactory", groupId = "topic-1-listener")
    public void ListenTopic1(ConsumerRecord<String, ReportEvent> record) throws KafkaException {
        log.info("got a message");
        ReportEvent event = record.value();

        if (event.getEventId() == null || event.getClientId() == null) {
            throw new KafkaException("Failed to parse kafka message");
        }

        userDataFetcherService.fetchData(event.getClientId())
                .flatMap(cl -> {
                    return moderationService.HandleReport(event, cl);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Data not found in Redis/API")))
                .doOnSuccess(
                        x -> log.info("end of service"))
                .doOnError(e -> log.error("Error in pipeline", e))
                .subscribe();
    }
}
