package org.bassmeister.reactiveDemos.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.AuthorDTO;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.dto.BookMapper;
import org.bassmeister.reactiveDemos.dto.BookMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DirtiesContext
public class AuthorServiceTest extends ServiceTests {

  private final AuthorRepository authorRepository;
  private final BookRepository bookRepository;
  private final AuthorService authorService;
  private final BookMapper bookMapper = new BookMapperImpl();

  @Autowired
  public AuthorServiceTest(
      AuthorRepository authorRepository,
      AuthorService authorService,
      BookRepository bookRepository) {
    this.authorRepository = authorRepository;
    this.authorService = authorService;
    this.bookRepository = bookRepository;
  }

  @Test
  void findAllAuthors() {
    Flux<AuthorDTO> authors = authorService.getAllAuthors();
    List<AuthorDTO> foundAuthors = new ArrayList<>();
    Consumer<AuthorDTO> authorDTOConsumer = foundAuthors::add;
    // Flux returns authors in random order, cannot use assertNext
    StepVerifier.create(authors)
        .consumeNextWith(authorDTOConsumer)
        .consumeNextWith(authorDTOConsumer)
        .consumeNextWith(authorDTOConsumer)
        .consumeNextWith(authorDTOConsumer)
        .verifyComplete();
    List<String> names = foundAuthors.stream().map(AuthorDTO::getName).toList();
    assertThat(names).contains("James Joyce", "Charles Dickens", "Jules Verne", "Edgar Allen Poe");
  }

  @Test
  void findAuthorContainsBooks() {
    Author author = authorRepository.findByName("James Joyce").block();
    Book ulysses =
        bookRepository
            .save(
                Book.builder()
                    .title("Ulysses")
                    .creationTime(Instant.now())
                    .authorId(author.getId())
                    .build())
            .block();

    Mono<Author> joyce = authorRepository.findByName("James Joyce");
    StepVerifier.create(joyce)
        .assertNext(
            james -> {
              assertThat(james.getName()).isEqualTo("James Joyce");
              assertThat(james.getBooks()).hasSize(1);
              assertThat(james.getBooks().getFirst()).isEqualTo("Ulysses");
            });
  }

  @Test
  void getAllBooksByAuthor() {
    Author author = authorRepository.findByName("Charles Dickens").block();
    Book christmas =
        bookRepository
            .save(
                Book.builder()
                    .title("A Christmas Carol")
                    .creationTime(Instant.now())
                    .authorId(author.getId())
                    .build())
            .block();
    Book oliver =
        bookRepository
            .save(
                Book.builder()
                    .title("Oliver Twist")
                    .creationTime(Instant.now())
                    .authorId(author.getId())
                    .build())
            .block();

    Flux<BookDTO> books = authorService.getBooks(author.getId());
    List<BookDTO> bookList = new ArrayList<>();

    StepVerifier.create(books)
        .consumeNextWith(bookList::add)
        .consumeNextWith(bookList::add)
        .verifyComplete();

    assertThat(bookList).hasSize(2);
    assertThat(bookList).contains(bookMapper.toBookDto(christmas), bookMapper.toBookDto(oliver));
  }
}
