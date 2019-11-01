package com.kubra.reactivedemo.person;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class PersonController {

  private static Flux<Person> personFlux;
  private static final List<Person> personList = new ArrayList<>();

  static {
    personFlux = Flux
        .zip(Flux.range(1, 6), Flux.just("John", "Jame", "Max", "Alex", "Aloy", "Sarah"))
        .map(t -> new Person(t.getT1(), t.getT2()));

    personFlux
        .doOnNext(personList::add)
        .subscribe();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @GetMapping("/persons/{id}")
  public Person getPerson(@PathVariable int id, @RequestParam(defaultValue = "2", required = false) int delay)
      throws InterruptedException {
    Thread.sleep(delay * 1000);
    return personList.stream()
        .filter(p -> p.getId() == id)
        .findFirst()
        .get();
  }

  @GetMapping("/persons")
  public Flux<Person> persons() {
    return personFlux;
  }
}
