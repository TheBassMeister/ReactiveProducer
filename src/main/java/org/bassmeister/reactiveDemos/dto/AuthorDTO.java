package org.bassmeister.reactiveDemos.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AuthorDTO {

  private String name;

  private String alias;

  List<String> writtenBooks = new ArrayList<>();
}
