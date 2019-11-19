package com.kubra.reactivedemo.greeting;

import java.time.Duration;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class GreetingReactiveController {

  @GetMapping("/greetings/sse")
  public Flux<Greeting> greetingsSse() {
    Flux<Greeting> delayElements = Flux
        .<Greeting>generate(sink -> sink.next(new Greeting("Hello @" + Instant.now().toString())))
        .delayElements(Duration.ofSeconds(1))
        .log();
    return delayElements;
  }

  @GetMapping("/greetings")
  public Flux<Greeting> greetings() {
    return Flux
        .<Greeting>generate(sink -> sink.next(new Greeting("Hello @" + Instant.now().toString())))
        .take(50);
  }
}
