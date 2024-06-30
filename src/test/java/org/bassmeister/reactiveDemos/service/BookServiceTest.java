package org.bassmeister.reactiveDemos.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BookServiceTest extends ServiceTests {

  private final BookRepository bookRepository;

  private final BookService bookService;

  @Autowired
  public BookServiceTest(BookRepository bookRepository, BookService bookService) {
    this.bookRepository = bookRepository;
    this.bookService = bookService;
  }

  @Test
  void findBooks() {
    Book book1 =
        bookRepository
            .save(Book.builder().title("Article 1").creationTime(Instant.now()).build())
            .block();
    Book book2 =
        bookRepository
            .save(Book.builder().title("Article 2").creationTime(Instant.now()).build())
            .block();

    Flux<BookDTO> savedBooks = bookService.listAllBooks();
    StepVerifier.create(savedBooks)
        .assertNext(
            next -> {
              assert book1 != null;
              assertThat(next.getId()).isNotNull().isEqualTo(book1.getId());
              assertThat(next.getTitle()).isEqualTo(book1.getTitle());
            })
        .assertNext(
            next -> {
              assert book2 != null;
              assertThat(next.getId()).isNotNull().isEqualTo(book2.getId());
              assertThat(next.getTitle()).isEqualTo(book2.getTitle());
            })
        .verifyComplete();
  }
}
