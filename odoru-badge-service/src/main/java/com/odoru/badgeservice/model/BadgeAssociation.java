package com.odoru.badgeservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing the association between a club member and their random badge.
 */
@Document(collection = "badge_associations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeAssociation {

  /** The unique identifier of the association. */
  @Id
  private String id;

  /** The unique identifier of the member. */
  @NotBlank(message = "Member ID is required")
  @Indexed(unique = true)
  private String memberId;

  /** The unique random badge number. */
  @NotBlank(message = "Badge number is required")
  @Indexed(unique = true)
  private String badgeNumber;
}
