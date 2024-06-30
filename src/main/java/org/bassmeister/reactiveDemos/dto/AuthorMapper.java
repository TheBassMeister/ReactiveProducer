package org.bassmeister.reactiveDemos.dto;

import org.bassmeister.reactiveDemos.data.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

  @Mapping(source = "profileName", target = "alias")
  @Mapping(source = "books", target = "writtenBooks")
  AuthorDTO toDto(Author author);
}
