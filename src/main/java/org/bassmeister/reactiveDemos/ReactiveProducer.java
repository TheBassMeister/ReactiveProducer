package org.bassmeister.reactiveDemos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ReactiveProducer {

  public static void main(String[] args) {
    Hooks.onOperatorDebug(); // costly
    SpringApplication.run(ReactiveProducer.class, args);
  }
}
