package org.bassmeister.reactiveDemos.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "creationTime"})
public class BookDTO {

  private UUID id;

  private String title;

  private Instant creationTime;

  private AuthorDTO author;
}
