package org.bassmeister.reactiveDemos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.dto.BookMapper;
import org.bassmeister.reactiveDemos.dto.BookMapperImpl;
import org.bassmeister.reactiveDemos.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@AutoConfigureWebTestClient(timeout = "36000")
public class BookControllerTest {

  private final WebTestClient testClient;

  private final BookRepository bookRepository;

  private final AuthorRepository authorRepository;

  private final BookMapper bookMapper;

  public BookControllerTest() {
    this.bookRepository = mock(BookRepository.class);
    this.authorRepository = mock(AuthorRepository.class);
    this.bookMapper = new BookMapperImpl();

    this.testClient =
        WebTestClient.bindToController(
                new BookController(new BookService(bookRepository, authorRepository, bookMapper)))
            .build();
  }

  @Test
  void listAllArticles() {
    Book book1 =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Article 1")
            .creationTime(Instant.now().minus(7, ChronoUnit.DAYS))
            .build();

    Book book2 =
        Book.builder().id(UUID.randomUUID()).title("Article 2").creationTime(Instant.now()).build();

    Book book3 =
        Book.builder().id(UUID.randomUUID()).title("Article 3").creationTime(Instant.now()).build();

    when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2, book3));
    FluxExchangeResult<BookDTO> res =
        testClient
            .get()
            .uri("/books")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(BookDTO.class);

    StepVerifier.create(res.getResponseBody())
        .assertNext(next -> assertThat(next).isEqualTo(bookMapper.toBookDto(book1)))
        .assertNext(next -> assertThat(next).isEqualTo(bookMapper.toBookDto(book2)))
        .assertNext(next -> assertThat(next).isEqualTo(bookMapper.toBookDto(book3)))
        .verifyComplete();
  }

  @Test
  void articleWithAuthor() {

    UUID authorId = UUID.randomUUID();
    Author author =
        Author.builder()
            .id(authorId)
            .name("Charles Dickens")
            .profileName("The Artful Dodger")
            .build();
    Book book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Oliver Twist")
            .creationTime(Instant.ofEpochMilli(1))
            .authorId(authorId)
            .build();

    when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));
    when(bookRepository.findAll()).thenReturn(Flux.just(book));
    FluxExchangeResult<BookDTO> res =
        testClient
            .get()
            .uri("/books")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(BookDTO.class);

    StepVerifier.create(res.getResponseBody())
        .assertNext(
            next -> {
              assertThat(next.getAuthor()).isNotNull();
              assertThat(next.getAuthor().getName()).isEqualTo(author.getName());
              assertThat(next.getAuthor().getAlias()).isEqualTo(author.getProfileName());
            })
        .verifyComplete();
  }

  @Test
  void findAllByAuthor() {
    UUID authorId = UUID.randomUUID();
    Author author =
        Author.builder()
            .id(authorId)
            .name("Charles Dickens")
            .profileName("The Artful Dodger")
            .build();
    Book book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Oliver Twist")
            .creationTime(Instant.ofEpochMilli(1))
            .authorId(authorId)
            .build();
    Book book2 =
        Book.builder()
            .id(UUID.randomUUID())
            .title("A Christmas Carol")
            .creationTime(Instant.ofEpochMilli(1))
            .authorId(authorId)
            .build();

    when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));
    when(bookRepository.findAllByAuthorId(authorId)).thenReturn(Flux.just(book, book2));
    FluxExchangeResult<BookDTO> res =
        testClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/books/byAuthor/{authorId}").build(authorId))
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(BookDTO.class);

    StepVerifier.create(res.getResponseBody())
        .assertNext(
            next -> {
              assertThat(next.getAuthor()).isNotNull();
              assertThat(next.getAuthor().getName()).isEqualTo(author.getName());
              assertThat(next.getTitle()).isEqualTo(book.getTitle());
            })
        .assertNext(
            next -> {
              assertThat(next.getAuthor()).isNotNull();
              assertThat(next.getAuthor().getName()).isEqualTo(author.getName());
              assertThat(next.getTitle()).isEqualTo(book2.getTitle());
            })
        .verifyComplete();
  }
}
