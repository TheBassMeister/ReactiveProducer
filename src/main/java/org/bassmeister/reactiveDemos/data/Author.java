package org.bassmeister.reactiveDemos.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

  @Id private UUID id;

  @NonNull private String name;

  @Column("profile_name")
  private String profileName;

  @Transient @Builder.Default private List<String> books = new ArrayList<>();
}
