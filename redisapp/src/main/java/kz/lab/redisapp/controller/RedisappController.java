package kz.lab.redisapp.controller;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kz.lab.redisapp.model.ClientData;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class RedisappController {
    private final ReactiveRedisOperations<String, ClientData> clientDataOperations;

    @GetMapping("/info/{clientId}")
    public Mono<ClientData> getClientInfo(@PathVariable String clientId) {
        return clientDataOperations.opsForValue().get("client:" + clientId);
    }

    @GetMapping("/all")
    public Flux<ClientData> getAllClientsInfo() {
        return clientDataOperations.keys("*").flatMap(clientDataOperations.opsForValue()::get);
    }
}
