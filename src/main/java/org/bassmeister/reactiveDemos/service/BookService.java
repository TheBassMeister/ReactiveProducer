package org.bassmeister.reactiveDemos.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.dto.BookMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  private final AuthorRepository authorRepository;

  private final BookMapper bookMapper;

  public Flux<BookDTO> listAllBooks() {
    Flux<Book> books = bookRepository.findAll().flatMap(this::loadRelations);
    return books.map(bookMapper::toBookDto);
  }

  public Flux<BookDTO> findAllByAuthor(UUID authorId) {
    Flux<Book> books = bookRepository.findAllByAuthorId(authorId).flatMap(this::loadRelations);
    return books.map(bookMapper::toBookDto);
  }

  public Mono<BookDTO> createBook(BookDTO dto) {
    return Mono.just(dto)
        .map(bookMapper::toBook)
        .flatMap(bookRepository::save)
        .map(bookMapper::toBookDto);
  }

  private Mono<Book> loadRelations(Book book) {
    Mono<Book> bookMono = Mono.just(book);
    if (book.getAuthorId() != null) {
      bookMono =
          bookMono
              .zipWith(authorRepository.findById(book.getAuthorId()))
              .map(
                  result -> {
                    result.getT1().setAuthor(result.getT2());
                    return result.getT1();
                  });
    }
    return bookMono;
  }
}
