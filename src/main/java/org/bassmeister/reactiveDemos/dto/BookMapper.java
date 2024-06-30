package org.bassmeister.reactiveDemos.dto;

import org.bassmeister.reactiveDemos.data.Author;
import org.bassmeister.reactiveDemos.data.Book;
import org.bassmeister.reactiveDemos.service.BookService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = BookService.class)
public interface BookMapper {

  @InheritConfiguration
  @Mapping(target = "authorId", ignore = true)
  Book toBook(BookDTO bookDTO);

  BookDTO toBookDto(Book book);

  @Mapping(source = "profileName", target = "alias")
  AuthorDTO toAuthorDto(Author author);

  Author toAuthor(AuthorDTO authorDTO);
}
