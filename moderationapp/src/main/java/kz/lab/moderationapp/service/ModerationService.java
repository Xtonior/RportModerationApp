package kz.lab.moderationapp.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import kz.lab.moderationapp.model.ClientDataDto;
import kz.lab.moderationapp.model.ReportEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ModerationService {
    @Value("${moderation.workday.start}")
    private LocalTime workdayStart;

    @Value("${moderation.workday.end}")
    private LocalTime workdayEnd;

    public Mono<ReportEvent> handleReport(ReportEvent event, ClientDataDto clientDataDto) {
        log.info("Moderating event clientId: {}", event.getClientId());

        if (!StringUtils.hasText(clientDataDto.category())) {
            log.error("Empty client category!");
            return Mono.empty();
        }

        if (!StringUtils.hasText(event.getCategory())) {
            log.error("Empty event client category!");
            return Mono.empty();
        }

        if (clientDataDto.hasActiveRequests()) {
            if (clientDataDto.category().equals(event.getCategory())) {
                log.info("Skipping report, client already has active request in the category");
                return Mono.empty();
            }
        }

        if (!isWorkTime()) {
            log.info("Skipping report due to wotktime");
            return Mono.empty();
        }

        return Mono.just(event);
    }

    public boolean isWorkTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(workdayStart) && now.isBefore(workdayEnd);
    }
}
