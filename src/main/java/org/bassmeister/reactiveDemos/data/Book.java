package org.bassmeister.reactiveDemos.data;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  @Id private UUID id;

  private String title;

  private Instant creationTime;

  @Column("author_id")
  private UUID authorId;

  @Transient private Author author;
}
