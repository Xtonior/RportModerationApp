package kz.lab.moderationapp.service;

import org.springframework.stereotype.Service;

import kz.lab.moderationapp.model.ReportEvent;
import kz.lab.moderationapp.model.ClientData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ModerationService {
    public static final Mono<? extends ReportEvent> HandleReport = null;

    public Mono<ReportEvent> HandleReport(ReportEvent event, ClientData userData) {
        log.info("Event clientId: {}, userData: {}", event.getClientId(), userData.userDepartment());
        return Mono.just(event);
    }
}
