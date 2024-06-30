package org.bassmeister.reactiveDemos.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bassmeister.reactiveDemos.dto.AuthorDTO;
import org.bassmeister.reactiveDemos.dto.BookDTO;
import org.bassmeister.reactiveDemos.service.AuthorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping(value = "/authors", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AuthorDTO> listAllAuthors() {
        return authorService.getAllAuthors();
    }

    @GetMapping(value="/authors/{authorId}/books")
    public Flux<BookDTO> getBooksByAuthor(@PathVariable("authorId") UUID authorId){
        return authorService.getBooks(authorId);
    }
}
