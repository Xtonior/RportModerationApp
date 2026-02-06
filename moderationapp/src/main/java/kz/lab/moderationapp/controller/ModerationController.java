package kz.lab.moderationapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kz.lab.moderationapp.exception.KafkaException;
import kz.lab.moderationapp.kafka.KafkaSenderImpl;
import kz.lab.moderationapp.model.ReportEvent;
import kz.lab.moderationapp.service.ModerationService;
import kz.lab.moderationapp.service.RedisService;
import kz.lab.moderationapp.service.UserDataFetcherService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationService moderationService;
    private final RedisService redisService;
    private final KafkaSenderImpl kafkaSender;
    private final UserDataFetcherService userDataFetcherService;

    @Value("${spring.data.redis.eventStorageTtl}")
    private long eventTtl;

    @Value("${kafka.topics.topic-2}")
    private String topic2;

    public void process(ReportEvent event) throws KafkaException {
        ReportEvent handled = handleReportEvent(event).block();

        if (handled == null)
            return;

        kafkaSender.send(topic2, handled.getEventId().toString(), handled);
    }

    private Mono<ReportEvent> handleReportEvent(ReportEvent event) {
        String id = event.getEventId().toString();
        return redisService.isAlreadyProcessed(id)
                .filter(alreadyProcessed -> !alreadyProcessed)
                .flatMap(unused -> userDataFetcherService.fetchData(event.getClientId()))
                .flatMap(fetchedData -> moderationService.handleReport(event, fetchedData))
                .flatMap(result -> redisService.saveEvent(id, event, eventTtl)
                        .thenReturn(event));
    }
}
