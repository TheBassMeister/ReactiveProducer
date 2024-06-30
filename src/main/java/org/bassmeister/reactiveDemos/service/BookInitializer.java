package org.bassmeister.reactiveDemos.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
@Profile("!test")
public class BookInitializer implements ApplicationRunner {

  private final AuthorRepository authorRepository;

  private final BookRepository bookRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Author poe = authorRepository.findByName("Edgar Allen Poe").block();

    if (poe != null) {
      List<Book> poeBooks = new ArrayList<>();

      poeBooks.add(
          Book.builder()
              .title("The Raven")
              .creationTime(randomTime())
              .authorId(poe.getId())
              .build());

      poeBooks.add(
          Book.builder()
              .title("The Fall Of The House of Usher")
              .creationTime(randomTime())
              .authorId(poe.getId())
              .build());

      poeBooks.add(
          Book.builder()
              .title("The Black Cat")
              .creationTime(randomTime())
              .authorId(poe.getId())
              .build());

      bookRepository.saveAll(Flux.fromIterable(poeBooks)).blockLast();
    }
  }

  private Instant randomTime() {
    return Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt());
  }
}
