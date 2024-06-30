package org.bassmeister.reactiveDemos.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthorRepository extends ReactiveCrudRepository<Author, UUID> {

  Mono<Author> findByName(String name);
}
