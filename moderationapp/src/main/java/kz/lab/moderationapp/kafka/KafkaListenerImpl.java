package kz.lab.moderationapp.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import kz.lab.moderationapp.exception.KafkaException;
import kz.lab.moderationapp.model.ReportEvent;
import kz.lab.moderationapp.service.ModerationService;
import kz.lab.moderationapp.service.RedisService;
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

    @Autowired
    RedisService redisService;

    @Value("${spring.data.redis.eventStorageTtl}")
    long eventTtl;

    @KafkaListener(topics = "${kafka.topics.topic-1}", containerFactory = "kafkaListenerContainerFactory", groupId = "topic-1-listener")
    public void ListenTopic1(ConsumerRecord<String, ReportEvent> record) throws KafkaException {
        log.info("got a message");
        ReportEvent event = record.value();

        if (event.getEventId() == null || event.getClientId() == null) {
            throw new KafkaException("Failed to parse kafka message");
        }

        String eventId = event.getEventId().toString();

        redisService.isAlreadyProcessed(eventId)
                .flatMap(alreadyProcessed -> {
                    log.info("Event {} already processed, skipping", eventId);
                    return Mono.empty();
                })
                .switchIfEmpty(
                        userDataFetcherService.fetchData(event.getClientId())
                                .flatMap(cl -> moderationService.HandleReport(event, cl))
                                .flatMap(result ->
                                redisService.saveEvent(eventId, event, eventTtl)
                                        .thenReturn(result)
                                ))
                .doOnSuccess(x -> log.info("Processing finished for event: {}", eventId))
                .doOnError(e -> log.error("Error in pipeline for event: {}", eventId, e))
                .subscribe();
    }
}
