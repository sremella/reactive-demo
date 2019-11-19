package com.kubra.reactivedemo.person;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class PersonController {

  private static Flux<Person> personFlux;
  private static final List<Person> personList = new ArrayList<>();

  static {
    personFlux = Flux
        .zip(Flux.range(1, 6), Flux.just("John", "Jane", "Max", "Alex", "Aloy", "Sarah"))
        .map(t -> new Person(t.getT1(), t.getT2()));

    personFlux
        .doOnNext(personList::add)
        .subscribe();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @GetMapping("/persons/{id}")
  public Mono<Person> getPerson(@PathVariable int id,
      @RequestParam(defaultValue = "1", required = false) int delay) {

    return personFlux
        .filter(p -> p.getId() == id)
        .delayElements(Duration.ofSeconds(delay))
        .next();
  }

  @GetMapping("/persons")
  public Flux<Person> persons() {
    return personFlux;
  }
}
