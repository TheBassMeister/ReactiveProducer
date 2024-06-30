package org.bassmeister.reactiveDemos.data;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public interface BookRepository extends ReactiveCrudRepository<Book, UUID> {

  Flux<Book> findAllByAuthorId(UUID authorId);
}
