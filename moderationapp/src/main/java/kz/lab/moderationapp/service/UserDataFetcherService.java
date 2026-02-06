package kz.lab.moderationapp.service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import kz.lab.moderationapp.model.ClientDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataFetcherService {

    @Value("${redisapp.endpoint}")
    String endpoint;

    @Value("${redisapp.timeout}")
    Long timeout;

    @Value("${redisapp.retries}")
    Long retries;

    @Value("${redisapp.retryDelay}")
    Long retryDelay;

    @Autowired
    WebClient webClient;

    public Mono<ClientDataDto> fetchData(UUID clientId) {
        return webClient.get()
                .uri(builder -> builder
                    .path(endpoint)
                    .build(clientId)
                )
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new RuntimeException("Server Error")))
                .bodyToMono(ClientDataDto.class)
                .timeout(Duration.ofSeconds(timeout))
                .retryWhen(Retry.backoff(retries, Duration.ofSeconds(retryDelay))
                        .jitter(0.75)
                        .filter(throwable -> throwable instanceof TimeoutException ||
                                throwable instanceof RuntimeException))
                .doOnError(e -> log.error("Failed to fetch data after retries", e));
    }
}
