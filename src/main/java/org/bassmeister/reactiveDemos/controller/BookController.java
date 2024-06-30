package org.bassmeister.reactiveDemos.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class BookController {

  private final BookService bookService;

  @GetMapping(value = "/books", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BookDTO> listAllBooks() {
    return bookService.listAllBooks();
  }

  @GetMapping(value = "/books/byAuthor/{authorId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BookDTO> findAllByAuthor(@PathVariable("authorId") UUID authorId) {
    return bookService.findAllByAuthor(authorId);
  }

  @PostMapping("/books")
  public Mono<BookDTO> addNewBook(BookDTO newArticle) {
    return bookService.createBook(newArticle);
  }
}
