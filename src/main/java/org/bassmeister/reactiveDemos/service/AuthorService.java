package org.bassmeister.reactiveDemos.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.AuthorRepository;
import org.bassmeister.reactiveDemos.data.BookRepository;
import org.bassmeister.reactiveDemos.dto.AuthorDTO;
import org.bassmeister.reactiveDemos.dto.AuthorMapper;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.dto.BookMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthorService {

  private final AuthorRepository authorRepository;

  private final BookRepository bookRepository;

  private final AuthorMapper authorMapper;

  private final BookMapper bookMapper;

  public Flux<AuthorDTO> getAllAuthors() {
    return authorRepository.findAll().flatMap(this::getAuthorsBooks).map(authorMapper::toDto);
  }

  private Mono<Author> getAuthorsBooks(Author author) {
    Mono<Author> authorMono = Mono.just(author);
    return authorMono.flatMap(
        aut ->
            bookRepository
                .findAllByAuthorId(author.getId())
                .map(
                    article -> {
                      aut.getBooks().add(article.getTitle());
                      return author;
                    })
                .takeLast(1)
                .switchIfEmpty(Flux.just(author))
                .next());
  }

  public Flux<BookDTO> getBooks(UUID authorId) {
    return bookRepository.findAllByAuthorId(authorId).map(bookMapper::toBookDto);
  }
  
}
