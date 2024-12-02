package ru.papapers.optimalchoice.ui.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.papapers.optimalchoice.api.domain.CriterionDto;

import java.util.UUID;

@Service
public class CriterionService {

    private final WebClient webClient;

    public CriterionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public CriterionDto add(CriterionDto criterionDto, UUID purposeId) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("purpose")
                        .pathSegment(purposeId.toString())
                        .pathSegment("criterion")
                        .build())
                .body(BodyInserters.fromValue(criterionDto))
                .retrieve()
                .bodyToMono(CriterionDto.class)
                .block();
    }

    public void delete(CriterionDto criterionDto, UUID purposeId) {
         webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("purpose")
                        .pathSegment(purposeId.toString())
                        .pathSegment("criterion")
                        .pathSegment(criterionDto.getId().toString())
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
