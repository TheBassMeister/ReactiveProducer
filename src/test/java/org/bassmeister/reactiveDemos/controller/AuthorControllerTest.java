package org.bassmeister.reactiveDemos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.AuthorDTO;
import org.bassmeister.reactiveDemos.dto.AuthorMapper;
import org.bassmeister.reactiveDemos.dto.AuthorMapperImpl;
import org.bassmeister.reactiveDemos.dto.BookMapperImpl;
import org.bassmeister.reactiveDemos.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient(timeout = "36000")
public class AuthorControllerTest {

  private final WebTestClient testClient;

  private final AuthorRepository authorRepository;

  private final BookRepository bookRepository;

  private final AuthorMapper authorMapper;

  public AuthorControllerTest() {
    this.authorRepository = mock(AuthorRepository.class);
    this.bookRepository = mock(BookRepository.class);
    this.authorMapper = new AuthorMapperImpl();

    this.testClient =
        WebTestClient.bindToController(
                new AuthorController(
                    new AuthorService(
                        authorRepository, bookRepository, authorMapper, new BookMapperImpl())))
            .build();
  }

  @Test
  void listAllAuthors() {
    Author poe =
        Author.builder()
            .id(UUID.randomUUID())
            .name("Edgar Allen Poe")
            .profileName("The Raven")
            .build();
    Author loveCraft =
        Author.builder().id(UUID.randomUUID()).name("H.P. Lovecraft").profileName("Khtulu").build();
    when(authorRepository.findAll()).thenReturn(Flux.just(poe, loveCraft));
    when(bookRepository.findAllByAuthorId(poe.getId()))
        .thenReturn(
            Flux.just(
                Book.builder()
                    .title("The Fall Of The House Of Usher")
                    .creationTime(Instant.ofEpochMilli(54))
                    .build()));

    when(bookRepository.findAllByAuthorId(loveCraft.getId())).thenReturn(Flux.empty());

    FluxExchangeResult<AuthorDTO> res =
        testClient
            .get()
            .uri("/authors")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(AuthorDTO.class);

    StepVerifier.create(res.getResponseBody())
        .assertNext(
            author -> {
              assertThat(author.getName()).isEqualTo("Edgar Allen Poe");
              assertThat(author.getAlias()).isEqualTo("The Raven");
              assertThat(author.getWrittenBooks())
                  .hasSize(1)
                  .contains("The Fall Of The House Of Usher");
            })
        .assertNext(
            author -> {
              assertThat(author.getName()).isEqualTo("H.P. Lovecraft");
              assertThat(author.getAlias()).isEqualTo("Khtulu");
              assertThat(author.getWrittenBooks()).isEmpty();
            })
        .verifyComplete();
  }
}
