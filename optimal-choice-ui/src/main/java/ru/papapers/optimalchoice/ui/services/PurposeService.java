package ru.papapers.optimalchoice.ui.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.papapers.optimalchoice.api.domain.PurposeDto;

import java.util.UUID;

@Service
public class PurposeService {

    private final WebClient webClient;

    public PurposeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public PurposeDto save(PurposeDto purposeDto) {
        return webClient
                .post()
                .uri("/purpose")
                .body(BodyInserters.fromValue(purposeDto))
                .retrieve()
                .bodyToMono(PurposeDto.class)
                .block();
    }

    public PurposeDto get(UUID id) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("purpose")
                        .pathSegment(id.toString()).build())
                .retrieve()
                .bodyToMono(PurposeDto.class)
                .block();
    }
}
