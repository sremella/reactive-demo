package com.kubra.reactivedemo.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonTypeId;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
@Slf4j
@TestPropertySource(locations = "classpath:test.properties")
class PersonControllerTest {

  private static final String BASE_URI = "http://localhost:8080/persons/";
  private RestTemplate restTemplate = new RestTemplate();
  private WebClient webClient = WebClient.create(BASE_URI);

  private Instant instant;

  @BeforeEach
  void start() {
    instant = Instant.now();
    log.debug("Starting test at {}", instant);
  }

  @AfterEach
  void end() {
    log.debug("Completed test in {} seconds", Duration.between(instant, Instant.now()).toSeconds());
  }

  @Test
  void shouldGetPersonsEachRT() {
    List<Person> persons = IntStream.range(1, 7)
        .mapToObj(id -> {
          Instant start = Instant.now();
          log.debug("Requesting person {} at {}", id, start);
          Person person = restTemplate.getForEntity(BASE_URI + id, Person.class).getBody();
          log.debug("Received person {} in {} seconds", id,
              Duration.between(start, Instant.now()).toSeconds());
          return person;
        })
        .collect(Collectors.toList());

    assertEquals(6, persons.size());
  }

  @Test
  void shouldGetPersonWC() {
    List<Person> persons = new ArrayList<>();

    List<Mono<Person>> personMonos = IntStream.range(1, 7)
        .mapToObj(id -> {
          Instant start = Instant.now();
          log.debug("Requesting person {} at {}", id, start);
          return webClient.get()
              .uri(BASE_URI + id)
              .retrieve()
              .bodyToMono(Person.class)
              .doOnSuccess(person -> {
                log.debug("Received person {} in {} seconds", person.getId(),
                    Duration.between(start, Instant.now()).toSeconds());
                persons.add(person);
              });
        })
        .collect(Collectors.toList());

    Mono.when(personMonos).block();
    assertEquals(6, persons.size());
  }

}